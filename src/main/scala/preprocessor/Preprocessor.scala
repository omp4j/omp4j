package org.omp4j.preprocessor

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import java.net.MalformedURLException

import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.loader.Loader
import org.omp4j.compiler.Compiler
import org.omp4j.utils.FileTreeWalker
import org.omp4j.preprocessor.grammar._

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

			// run preprocessing
			conf.files.foreach(f => parseFile(f))

			// compile again -> result
			val compiler = new Compiler(FileTreeWalker.recursiveListFiles(conf.prepDir), conf.flags)
			compiler.compile
			// conf.files.foreach(f => println(f.getAbsolutePath))

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

	/** Parse one particular file.
	  * @param file Valid source file to be parsed
	  * @throws ParseException Unexpected exception
	  * @throws SyntaxErrorException If OMP directive has invalid syntax
	  */
	private def parseFile(file: File) = {
		val lexer = new Java8Lexer(new ANTLRFileStream(file.getPath))
		val tokens = new CommonTokenStream(lexer)
		val parser = new Java8Parser(tokens)
		val t: Java8Parser.CompilationUnitContext = parser.compilationUnit

		// t.inspect(parser);	// display gui tree

		val transVis = new TranslationVisitor(tokens, parser, t)
		saveResult(file, transVis.translate)
	}

	private def saveResult(origFile: File, text: String) = {
		// println(text)	// TODO: DEBUG

		val newFile = Files.createTempFile(conf.prepDir.toPath, origFile.getName + "-", ".java").toFile
		val writer = new PrintWriter(newFile, "UTF-8")
		writer.println(text)
		writer.close
	}
}
