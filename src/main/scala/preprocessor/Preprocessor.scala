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

import org.omp4j.preprocessor.grammar._

// for now only prints blocks with previous single-line comment
class Preprocessor(files: Array[String]) {

	/** Start parsing file by file */
	def run() = {
		try {
			for (f <- files) parseFile(f)
		} catch {
			case e: Exception => println("exception: "+e)
		}		
	}

	/** Parse one particular file */
	private def parseFile(f: String) = {
		try {
			val lexer = new Java8Lexer(new ANTLRFileStream(f))
			val tokens = new CommonTokenStream(lexer)
			val parser = new Java8Parser(tokens)
			val t: ParserRuleContext = parser.compilationUnit()

			// t.inspect(parser);	// display gui tree
			
			val walker = new ParseTreeWalker()
			val dl = new DirectiveListener(tokens, parser)
			walker.walk(dl, t)

			for ((cmtTree, ctx) <- dl.getOmpBlocks()) {
				// println(cmtTree.toStringTree() + " ... " + ctx.toStringTree())
			}

		} catch {
			case e: Exception => println("parser exception: "+e)
		}		
	}
}
