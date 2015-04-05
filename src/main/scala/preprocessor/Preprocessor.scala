package org.omp4j.preprocessor

import java.io.{File, PrintWriter}
import java.net.MalformedURLException
import org.omp4j.tree.OMPFile
import org.omp4j.utils.{FileDuplicator, FileSaver, FileTreeWalker}

import scala.collection.JavaConverters._

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.atn._
import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.grammar._
import org.omp4j.system._

import scala.collection.mutable.ArrayBuffer

/** Class representing the preprocessor itself.
  * @constructor Create preprocessor for given files.
  * @param conf the preprocessor configuration
  * @throws ParseException TODO
  */
class Preprocessor(implicit conf: Config) {

	/** Start parsing file by file, return (files, tmpDirs) */
	def run(firstRun: Boolean = true): (Array[File], List[(File, File)]) = {

		/* New lifecycle, TODO: rewrite
		- get config
		- get parseTree for each file (check exceptions)
		- remove threadId tokens and validate source using compiler
		- using previously parsed trees, make one level translation
		- run until a directive exists
		 */

		// parse sources        TODO: parallelly
		val parsed = conf.files.map(f => (f, parseFile(f)))

		// validate
		validate(parsed)

		// register tokens
		for {(f, (tok, par, cun)) <- parsed} {
			registerTokens(tok)
		}

		val finalSourcesBuffer = ArrayBuffer[File]()
		val nextLevelSourcesBuffer = ArrayBuffer[File]()

		// translate and save   TODO: parallelly
		for {(f, (tok, par, cun)) <- parsed} {
			try {
				val translatedSource = translate(tok, par, cun)
				nextLevelSourcesBuffer += FileSaver.saveToFile(translatedSource, conf.preprocessedDir, cun.packageDeclaration, f.getAbsolutePath.split(File.separator).last)
			} catch {
				case e: NothingToTranslateException => finalSourcesBuffer += f
			}
		}

		val finalSources: Array[File] = finalSourcesBuffer.toArray
		val nextLevelSources: Array[File] = nextLevelSourcesBuffer.toArray

		val (result, tmpDirs): (Array[File], List[(File, File)]) =
			if (nextLevelSources.length == 0) (finalSources.toArray, List((conf.workDir, conf.preprocessedDir)))
			else {
				val nextConf = conf.nextLevel(finalSources ++ nextLevelSources)
				val P = new Preprocessor()(nextConf)
				val (nextRes, nextTmps) = P.run(firstRun = false)
				(nextRes, (conf.workDir, conf.preprocessedDir) :: nextTmps)
			}

		if (firstRun) {
			tmpDirs.tail.foreach{ case (big, small) =>
				cleanup(big)
			}
			(result, List(tmpDirs.head))
		} else (result, tmpDirs)
	}

	/** don't call externally */
	def validate(parsed: Array[(File, (CommonTokenStream, Java8Parser, Java8Parser.CompilationUnitContext))]) = {
		// TODO: parallelly
		val toValidate = ArrayBuffer[File]()

		// create tmp files without threadIds
		parsed.foreach{case (f, (tok, par, cun)) =>
			val tir = new ThreadIdRemover(cun, tok)
			val threadLessText = tir.removedIds

			toValidate += FileSaver.saveToFile(threadLessText, conf.validationDir, cun.packageDeclaration, f.getAbsolutePath.split(File.separator).last)
		}

		// copy runtime libs
		conf copyRuntimeClassesTo conf.compilationDir

		// compile them
		val compiler = new Compiler(toValidate.toArray)
		compiler.compile(destDir = conf.compilationDir.getAbsolutePath, addCP = conf.compilationDir.getAbsolutePath)
		compiler.jar()
	}


	/** Delete file given */
	private def cleanup(deleteMe:File) = FileTreeWalker.recursiveDelete(deleteMe)

	/** Insert all tokens to tokenSet in order to prevent their usage */
	private def registerTokens(toks: CommonTokenStream) = {
		toks.getTokens.asScala.toList.foreach(t => conf.tokenSet.testAndSet(t.getText))
	}

	/** Parse one particular file.
	  * @param file Valid source file to be parsed
	  * @throws ParseException Unexpected exception
	  * @throws SyntaxErrorException If OMP directive has invalid syntax
	  */
	def parseFile(file: File) = {
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

		(tokens, parser, cunit)
	}
	/** Use TranslationVisitor to get translated code (as a String) */
	private def translate(tokens: CommonTokenStream, parser: Java8Parser, cunit: Java8Parser.CompilationUnitContext): String = {

		// List of directives
		val directives = new DirectiveVisitor(tokens, parser).visit(cunit)
		if (directives.size == 0) throw new NothingToTranslateException

		val rewriter = new TokenStreamRewriter(tokens)
		val ompFile = new OMPFile(cunit, parser)

		// top level directives
		directives.filter(_._2.parent == null).foreach { case (ctx, d) =>
			d.translate(rewriter, ompFile, directives)
		}

		rewriter.getText
	}
}
