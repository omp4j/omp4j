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
  *
  * @constructor Create preprocessor for given files.
  * @param conf the preprocessor configuration
  * TODO: throws
  */
class Preprocessor(implicit conf: Config) {

	/** Recursively translate all files until some directive exists.
	  *
	  *	New lifecycle, TODO: lifecycle
	  *	- get config
	  *	- get parseTree for each file (check exceptions)
	  *	- remove threadId tokens and validate source using compiler
	  *	- using previously parsed trees, make one level translation
	  *	- run until a directive exists
	  * @param firstRun true if this is the first recursion level, false otherwise
	  * @return a tuple containing an array of translated files and the list of tuples of working and preprocessed directories
          * TODO: throws
	  */
	def run(firstRun: Boolean = true): (Array[File], List[(File, File)]) = {

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
				FileTreeWalker.recursiveDelete(big)
			}
			(result, List(tmpDirs.head))
		} else (result, tmpDirs)
	}

	/** Validate sources passed.
	  *
	  * The method is not meant for external usage, however it may come in handy
	  *
	  * @param parsed An array of file and parsed AST properties
	  * @throws CompilationException if validation fails
	  * TODO: throws
	 */
	def validate(parsed: Array[(File, (CommonTokenStream, Java8Parser, Java8Parser.CompilationUnitContext))]) = {
		// TODO: parallelly
		val toValidate = ArrayBuffer[File]()

		// create tmp files without threadIds
		parsed.foreach{case (f, (tok, par, cun)) =>
			val tir = new ThreadIdRemover(cun, tok)
			val threadLessText = tir.removedIds()

			toValidate += FileSaver.saveToFile(threadLessText, conf.validationDir, cun.packageDeclaration, f.getAbsolutePath.split(File.separator).last)
		}

		// copy runtime libs
		conf copyRuntimeClassesTo conf.compilationDir

		// compile them
		val compiler = new Compiler(toValidate.toArray)
		compiler.compile(destDir = conf.compilationDir.getAbsolutePath, addCP = conf.compilationDir.getAbsolutePath)
		compiler.jar()
	}

	/** Insert all tokens into TokenSet in order to prevent their later usage.
	 *
	 * @param toks token stream from whence the tokens come
	 */
	private def registerTokens(toks: CommonTokenStream) = {
		toks.getTokens.asScala.toList.foreach(t => conf.tokenSet.testAndSet(t.getText))
	}

	/** Parse one particular file.
	  *
	  * @param file Valid source file to be parsed
	  * @throws ParseException if unexpected error occurs
	  * @throws SyntaxErrorException if OMP directive has invalid syntax
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
	/** Translate top directives of the compilation unit context provided.
	  *
	  * Initially, create the Directive Hierarchy Model. Secondly run source and bytecode analysis.
	  * Finally, translate first level directives, i.e. the top ones in the hierarchy.
	  *
	  * @param tokens token stream
	  * @param parser the Java8 ANTLR parser
	  * @param cunit compilation unit of a file
	  * @return source code without first-level directives that may run in parallel
	  * TODO: throws
	  */
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
