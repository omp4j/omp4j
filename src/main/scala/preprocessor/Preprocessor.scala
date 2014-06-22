package org.omp4j.preprocessor

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree._

import java.io._
import org.omp4j.preprocessor.grammar._
import org.omp4j.preprocessor.exception._

/** Class representing the preprocessor itself.
  * @constructor Create preprocessor for files given.
  * @param fileStrs Relative paths to files to be parsed.
  */
class Preprocessor(filesStrs: Array[String]) {

	/** Start parsing file by file
	  * @throws IllegalArgumentException when non-existing file is passed.
	  * @throws ParseException when some parse error occures
	  */
	def run() = {
		val files = for (fs <- filesStrs; f = new File(fs)) yield f
		files.map{ f => if (!f.exists())
			throw new IllegalArgumentException("File '" + f.getPath() + "' does not exist")
		}

		for (f <- files) {
			try parseFile(f)
			catch {
				case e: Exception => throw new ParseException("From file '" + f.getPath() + "'", e)
			}
		}
	}

	/** Parse one particular file.
	  * @param file Valid source file to be parsed
	  * @throws ParseException TODO
	  */
	private def parseFile(file: File) = {
		val lexer = new Java8Lexer(new ANTLRFileStream(file.getPath()))
		val tokens = new CommonTokenStream(lexer)
		val parser = new Java8Parser(tokens)
		val t: Java8Parser.CompilationUnitContext = parser.compilationUnit()

		t.inspect(parser);	// display gui tree
		
		val walker = new ParseTreeWalker()
		val dl = new DirectiveListener(tokens, parser)
		walker.walk(dl, t)

		val ompBlocks = dl.getOmpBlocks()

		// ompBlocks.foreach(d => println(d + "\n"))
		// println("-----------------")

		val f = new OMPFile(t, parser)


		// for ((cmtTree, ctx) <- dl.getOmpBlocks()) {
		// 	// println(cmtTree.toStringTree() + " ... " + ctx.toStringTree())
		// }

		// val walker2 = new ParseTreeWalker()
		// val smtl = new SubtreeMatchListener(parser, ompBlocks)
		// walker2.walk(smtl, t)


	}
}

// class SubtreeMatchListener(parser: Java8Parser, ompBlocks: List[Directive]) extends Java8BaseListener {
// 	override def enterStatement(ctx: Java8Parser.StatementContext) = {

// 		val candidates = ompBlocks.filter(_.ctx == ctx)
// 		candidates.size match {
// 			case 0 => {}
// 			case 1 => translate(candidates.head)
// 			case _ => throw new ParseException("Err1")
// 		}
// 		// for (d <- ompBlocks) {
// 		// 	if (d.ctx == ctx) println(d)
// 		// }
// 	}

// 	def translate(d: Directive) = {
// 		println(d)
// 	}
// }
