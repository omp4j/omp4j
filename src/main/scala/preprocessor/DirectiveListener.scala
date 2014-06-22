package org.omp4j.preprocessor

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.tree._

import scala.collection.mutable.ListBuffer
import org.omp4j.preprocessor.grammar._

// for now only prints blocks with previous single-line comment
class DirectiveListener(tokens: CommonTokenStream, parser: Java8Parser) extends Java8BaseListener {

	/** ListBuffer of tuples (previous comment, statement) */
	private var commentedBlocks = new ListBuffer[(String, Java8Parser.StatementContext)]()

	override def enterStatement(ctx: Java8Parser.StatementContext) = {
		val semi = ctx.getStart()
		val i = semi.getTokenIndex()

		val cmtChannel = tokens.getHiddenTokensToLeft(i, Java8Lexer.COMMENTS)
		if (cmtChannel != null) {
			val cmt = cmtChannel.get(cmtChannel.size() - 1)	// get last comment
			if (cmt != null) {
				commentedBlocks += new Tuple2(cmt.getText(), ctx)
				// println(ctx.getText() + "\t" + cmt.getText())
			}
		}
	}

	/** Get list of tuples (ompParseTree, statementContext) */
	def getOmpBlocks(): List[Directive] = {
		val transformated: ListBuffer[Directive] = for {
			(cmt, ctx) <- commentedBlocks
			ompLexer  = new OMPLexer(new ANTLRInputStream(cmt.substring(2)))
			ompTokens = new CommonTokenStream(ompLexer)
			ompParser = new OMPParser(ompTokens)

			ompCtx = ompParser.ompUnit()
		} yield new Directive(ompCtx, ompParser, ctx, parser)

		transformated.toList
	}
}

case class Directive(
	ompCtx: OMPParser.OmpUnitContext,
	ompParser: OMPParser,
	ctx: Java8Parser.StatementContext,
	parser: Java8Parser
) {
	override def toString() = {
		ompCtx.toStringTree(ompParser) +
		"\n" +
		ctx.toStringTree(parser)
	}
}
