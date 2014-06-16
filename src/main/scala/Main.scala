
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionMode;

package org.omp4j {
	// simple ANTLR example
	class ScalaParser(files: Array[String]) {

		def doAll() = {
			try {
				for (f <- files) parseFile(f)
			} catch {
				case e: Exception => println("exception: "+e)
			}
		}

		def parseFile(f: String) = {
			try {
				// Create a scanner that reads from the input stream passed to us
				val lexer: Lexer = new Java8Lexer(new ANTLRFileStream(f));
				val tokens: CommonTokenStream = new CommonTokenStream(lexer);

				// Create a parser that reads from the scanner
				val parser: Java8Parser = new Java8Parser(tokens);
				// start parsing at the compilationUnit rule
				val t: ParserRuleContext = parser.compilationUnit();
				t.inspect(parser);
				println(t.toStringTree(parser));
				println(t.getChild(0).toStringTree(parser));
			} catch {
				case e: Exception => println("parser exception: "+e)
			}
			
		}
	}

	object Main {
		def main(args: Array[String]): Unit = {
			val sp = new ScalaParser(args)
			sp.doAll()
		}
		
	}
}
