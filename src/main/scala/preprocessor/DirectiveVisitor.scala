package org.omp4j.preprocessor

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import scala.collection.mutable.ListBuffer
import org.omp4j.preprocessor.grammar._

/** Fetch list of Directives (aka OMPParseTree, corresponding statement and parsers) */
class DirectiveVisitor(tokens: CommonTokenStream, parser: Java8Parser) extends Java8BaseVisitor[List[Directive]] {

	/** Save proper statement */
	override def visitStatement(stmtCtx: Java8Parser.StatementContext) = {
		val semi = stmtCtx.getStart()
		val i = semi.getTokenIndex()

		val cmtChannel = tokens.getHiddenTokensToLeft(i, Java8Lexer.COMMENTS)
		if (cmtChannel != null) {
			if (cmtChannel.size() > 0) {
				val cmt = cmtChannel.get(cmtChannel.size() - 1)	// get last comment
				if (cmt != null) {
					// println(stmtCtx.getText() + "\t'" + cmt.getText() + "'")

					val rawComment = cmt.getText()
					val raw = rawComment.substring(2)

					val ompLexer  = new OMPLexer(new ANTLRInputStream(raw))
					val ompTokens = new CommonTokenStream(ompLexer)
					val ompParser = new OMPParser(ompTokens)

					val ompCtx = ompParser.ompUnit()

					// TODO: validate omp

					(new Directive(ompCtx, ompParser, stmtCtx, parser)) :: visitChildren(stmtCtx)

				}
				else visitChildren(stmtCtx)
			} // TODO: maybe get rid of multiple 'return' statements
			else visitChildren(stmtCtx)
		}
		else visitChildren(stmtCtx)
	}

	override def defaultResult() = List[Directive]()
	override def aggregateResult(a: List[Directive], b: List[Directive]) = a ::: b
}
