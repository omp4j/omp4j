import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree._

package org.omp4j.preprocessor {

// for now only prints blocks with previous single-line comment
class DirectiveListener(tokens: CommonTokenStream, parser: Java8Parser) extends Java8BaseListener {
	override def enterStatement(ctx: Java8Parser.StatementContext) = {
		val semi = ctx.getStart()
		val i = semi.getTokenIndex()

		val cmtChannel = tokens.getHiddenTokensToLeft(i, Java8Lexer.COMMENTS)
		if (cmtChannel != null) {

			val cmt = cmtChannel.get(cmtChannel.size() - 1)
			if (cmt != null) {
				println(ctx.getText() + "\t" + cmt.getText())
			}
		}
	}
}

}	// package
