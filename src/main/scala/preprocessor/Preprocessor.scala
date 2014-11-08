package org.omp4j.preprocessor

import java.io.{File, PrintWriter}
import java.net.MalformedURLException
import org.omp4j.tree.OMPFile

import scala.collection.JavaConverters._

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.atn._
import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.grammar._
import org.omp4j.system._
import org.omp4j.utils.FileTreeWalker

/** Class representing the preprocessor itself.
  * @constructor Create preprocessor for given files.
  * @param Files to be parsed
  * @throws ParseException TODO
  */
class Preprocessor(args: Array[String]) {

	/** Implicit and the only Config */
	implicit val conf = new Config(args)

	/** Start parsing file by file */
	def run: Int = {

		var exitCode: Int = 1
		try {
			// init conf (compile, pack and load)
			conf.init

			// parse sources        TODO: parallelly
			val parsed = conf.files.map(f => (f, parseFile(f)))
			// register tokens
			parsed.foreach {case (f, (tok, par, cun)) => registerTokens(tok)}
			// translate and save   TODO: parallelly
			for {(f, (tok, par, cun)) <- parsed} {
				val res = translate(tok, par, cun)
				saveResult(f, res)
			}

			// compile again -> result
			val compiler = new Compiler(FileTreeWalker.recursiveListFiles(conf.prepDir) ++ FileTreeWalker.getRuntimeFiles, conf.flags)
			compiler.compile

			exitCode = 0

		} catch {	// TODO: various behaviour and output
			case e: IllegalArgumentException => e.printStackTrace
			case e: MalformedURLException    => e.printStackTrace
			case e: SecurityException        => e.printStackTrace
			case e: CompilationException     => e.printStackTrace
			case e: ParseException           => e.printStackTrace
			case e: SyntaxErrorException     => e.printStackTrace
			case e: RuntimeException         => e.printStackTrace
			// unexpected exception
			case e: Exception                => e.printStackTrace
		} finally {
			cleanup
		}
		exitCode
	}

	/** Delete workdir */
	private def cleanup = FileTreeWalker.recursiveDelete(conf.workDir)

	/** Insert all tokens to tokenSet in order to prevent their usage */
	private def registerTokens(toks: CommonTokenStream) = {
		toks.getTokens.asScala.toList.foreach(t => conf.tokenSet.testAndSet(t.getText))
	}

	/** Parse one particular file.
	  * @param file Valid source file to be parsed
	  * @throws ParseException Unexpected exception
	  * @throws SyntaxErrorException If OMP directive has invalid syntax
	  */
	private def parseFile(file: File) = {
		val lexer = new Java8Lexer(new ANTLRFileStream(file.getPath))
		val tokens = new CommonTokenStream(lexer)
		val parser = new Java8Parser(tokens)

		val cunit = try {
				// try faster SLL(*)
				parser.getInterpreter.setPredictionMode(PredictionMode.SLL)
				parser.removeErrorListeners
				parser.setErrorHandler(new BailErrorStrategy)
				parser.compilationUnit
			} catch {
				case ex: RuntimeException =>
				if (ex.isInstanceOf[RuntimeException] && ex.getCause.isInstanceOf[RecognitionException]) {
					tokens.reset
					// back to standard listeners/handlers
					parser.addErrorListener(ConsoleErrorListener.INSTANCE)
					parser.setErrorHandler(new DefaultErrorStrategy)
					// try standard LL(*)
					parser.getInterpreter.setPredictionMode(PredictionMode.LL)
					parser.compilationUnit
				} else {
					throw new ParseException("Both parsing strategies SLL(*) and LL(*) failed", ex)
				}
			}
//		cunit.inspect(parser);	// display gui tree

		(tokens, parser, cunit)
	}
	/** Use TranslationVisitor to get translated code (as a String) */
	private def translate(tokens: CommonTokenStream, parser: Java8Parser, cunit: Java8Parser.CompilationUnitContext): String = {

		// List of directives
		val directives = (new DirectiveVisitor(tokens, parser)).visit(cunit)
		val rewriter = new TokenStreamRewriter(tokens)
		val ompFile = new OMPFile(cunit, parser)

		// top level directives
		directives.filter(_._2.parent == null).foreach { case (ctx, d) =>
			d.translate(rewriter, ompFile)
		}

		rewriter.getText
	}

	/** Save results to file*/
	private def saveResult(origFile: File, text: String) = {
//		println(text)	// TODO: DEBUG

		val newFile: File = File.createTempFile(s"${origFile.getName}-", ".java", conf.prepDir)
		val writer = new PrintWriter(newFile, "UTF-8")
		writer.println(text)
		writer.close
	}
}
