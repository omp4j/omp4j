package org.omp4j.preprocessor

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import java.io._

import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.preprocessor.grammar._

/** Class representing the preprocessor itself.
  * @constructor Create preprocessor for given files.
  * @param Files to be parsed
  * @throws ParseException TODO
  */
class Preprocessor(files: Iterable[File])(implicit conf: Config) {

	/** Start parsing file by file
	  * @throws IllegalArgumentException when non-existing file is passed.
	  * @throws ParseException when some parse error occures
	  */
	def run = {
		files.foreach{ f =>
			try {
				parseFile(f)
			} catch {
				case e: Exception => throw new ParseException("From file '" + f.getPath() + "'", e)
			}
		}
	}

	/** Parse one particular file.
	  * @param file Valid source file to be parsed
	  * @throws ParseException Unexpected exception
	  * @throws SyntaxErrorException If OMP directive has invalid syntax
	  */
	private def parseFile(file: File): String = {
		val lexer = new Java8Lexer(new ANTLRFileStream(file.getPath()))
		val tokens = new CommonTokenStream(lexer)
		val parser = new Java8Parser(tokens)
		val t: Java8Parser.CompilationUnitContext = parser.compilationUnit()

		// t.inspect(parser);	// display gui tree

		// val myVis = new MyVisitor(tokens, parser, t)
		// println(myVis.fetch)

		val transVis = new TranslationVisitor(tokens, parser, t)
		val res: String = transVis.translate
		println(res)	// DEBUG
		res
	}
}
