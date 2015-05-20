package org.omp4j.preprocessor

import java.io.{File, PrintWriter}
import java.net.MalformedURLException
import org.omp4j.tree.OMPFile
import org.omp4j.utils.{TmpDir, FileDuplicator, FileSaver, FileTreeWalker}

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
  */
class Preprocessor(implicit conf: Config) {

	/** Recursively translate all files until some directive exists.
	  *
	  * The work-flow of the preprocessor is as follows:
	  * <ul>
	  *     <li>Configuration creation (accept `conf`)</li>
	  *     <li>Code analysis</li>
	  *     <li>Directive recognition</li>
	  *     <li>Top-level directives translation (according to hierarchy model)</li>
	  *     <li>Saving the output</li>
	  *     <li>Recursive call</li>
	  *
	  * The penultimate tuple of second list represents the directories where translated source files are really stored
	  *
	  * @param firstRun true if this is the first recursion level, false otherwise
	  * @return a tuple containing an array of translated files and the list of tuples of working and preprocessed directories
	  * @throws ParseException if an parsing-related error occurred
	  * @throws SyntaxErrorException if syntax error in the directive occurred
	  * @throws CompilationException if file cannot be compiled by Java compiler
	  */
	def run(firstRun: Boolean = true): (Array[File], List[(File, File)]) = {

		if (firstRun) conf.logger.log("Running first preprocessor level")
		else conf.logger.log("Running next preprocessor level")

		// Parse sources.
		val parsed = conf.files.map(f => (f, parseFile(f)))

		// validate
		validate(parsed)

		// Register tokens.
		for {(f, (tok, par, cun)) <- parsed} {
			registerTokens(tok)
		}

		val finalSourcesBuffer = ArrayBuffer[File]()
		val nextLevelSourcesBuffer = ArrayBuffer[File]()

		// Translate and save.
		for {(f, (tok, par, cun)) <- parsed} {
			try {
				val translatedSource = translate(tok, par, cun)
				nextLevelSourcesBuffer += FileSaver.saveToFile(translatedSource, conf.preprocessedDir, cun.packageDeclaration, f.getAbsolutePath.split(TmpDir.separator).last)
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

		(result, tmpDirs)
	}

	/** Validate sources passed.
	  *
	  * The method is not meant for external usage, however it may come in handy
	  *
	  * @param parsed An array of file and parsed AST properties
	  * @throws CompilationException if validation fails
	 */
	def validate(parsed: Array[(File, (CommonTokenStream, Java8Parser, Java8Parser.CompilationUnitContext))]) = {

		val toValidate = ArrayBuffer[File]()

		// Create tmp files without threadIds.
		parsed.foreach{case (f, (tok, par, cun)) =>
			conf.logger.log("Validating file %s" format f.getAbsolutePath)

			val tir = new ThreadIdRemover(cun, tok)
			val threadLessText = tir.removedIds()

			toValidate += FileSaver.saveToFile(threadLessText, conf.validationDir, cun.packageDeclaration, f.getAbsolutePath.split(TmpDir.separator).last)
		}

		// copy runtime libs
		conf copyRuntimeClassesTo conf.compilationDir

		// compile them
		conf.logger.log("Compiling for validation")
		val compiler = new Compiler(toValidate.toArray)
		compiler.compile(destDir = conf.compilationDir.getAbsolutePath, addCP = conf.compilationDir.getAbsolutePath)
		conf.logger.log("Packing for validation")
		compiler.jar()
		conf.loader = new Loader(conf.jar)
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
		conf.logger.log(s"Parsing file '${file.getAbsolutePath}'")

		val lexer = new Java8Lexer(new ANTLRFileStream(file.getPath))
		val tokens = new CommonTokenStream(lexer)
		val parser = new Java8Parser(tokens)

		val cunit = try {
				// try faster SLL(*)
				conf.logger.log("Trying fast SLL method...")

				parser.getInterpreter.setPredictionMode(PredictionMode.SLL)
				parser.removeErrorListeners()
				parser.setErrorHandler(new BailErrorStrategy)
				val res = parser.compilationUnit

				conf.logger.log("Successfully")
				res
			} catch {
				case ex: RuntimeException =>
				if (ex.isInstanceOf[RuntimeException] && ex.getCause.isInstanceOf[RecognitionException]) {
					conf.logger.log("Failed, trying standard LL method instead...")
					tokens.reset()
					// back to standard listeners/handlers
					parser.addErrorListener(ConsoleErrorListener.INSTANCE)
					parser.setErrorHandler(new DefaultErrorStrategy)
					// try standard LL(*)
					parser.getInterpreter.setPredictionMode(PredictionMode.LL)
					val res = parser.compilationUnit

					conf.logger.log("Successfully")
					res
				} else {
					conf.logger.log("Failed")
					throw new ParseException(s"Both parsing strategies SLL(*) and LL(*) failed while parsing file '${file.getAbsolutePath}'", ex)
				}
			}

		(tokens, parser, cunit)
	}

	/** Translate top directives of the compilation unit context provided.
	  *
	  * Initially, create the Directive Hierarchy Model. Secondly run source and bytecode analysis.
	  * Finally, translate first level directives, i.e. the top ones in the hierarchy.
	  *
	  * @param tokens token stream
	  * @param parser the Java8 ANTLR parser
	  * @param cunit compilation unit of a file
	  * @return source code without first-level directives that may run in parallel
	  */
	private def translate(tokens: CommonTokenStream, parser: Java8Parser, cunit: Java8Parser.CompilationUnitContext): String = {

		conf.logger.log("Translating a file...")
		conf.logger.log("Building directive hierarchy model")
		// List of directives
		val directives = new DirectiveVisitor(tokens, parser).visit(cunit)
		if (directives.size == 0) throw new NothingToTranslateException

		conf.logger.log("Building class hierarchy model")
		val rewriter = new TokenStreamRewriter(tokens)
		val ompFile = new OMPFile(cunit, parser)

		// top level directives
		directives.filter(_._2.parent == null).foreach { case (ctx, d) =>
			conf.logger.log("Translating a directive...")
			d.translate(rewriter, ompFile, directives)
			conf.logger.log("Done")
		}

		val res = rewriter.getText

		conf.logger.log("File translation done")
		res
	}
}
