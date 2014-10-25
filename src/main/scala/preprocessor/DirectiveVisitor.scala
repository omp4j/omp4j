package org.omp4j.preprocessor

import org.antlr.v4.runtime._
import org.omp4j.Config
import org.omp4j.directive._
import org.omp4j.exception._
import org.omp4j.grammar._

import scala.collection.immutable.ListMap
import scala.collection.mutable.Stack
import scala.util.control.Breaks._

/** Static DirectiveVisitor properties */
object DirectiveVisitor {
	/** Ordered map of key: ParserRuleContext; value: Directive */
	type DirectiveMap = ListMap[ParserRuleContext, Directive]
}

/** Fetch list of Directives (aka OMPParseTree, corresponding statement and parsers) */
class DirectiveVisitor(tokens: CommonTokenStream, parser: Java8Parser)(implicit conf: Config) extends Java8BaseVisitor[DirectiveVisitor.DirectiveMap] {

	/** List of all directive ancestors */
	private val stack = Stack[Directive]()

	/** Save proper statement */
	override def visitStatement(stmtCtx: Java8Parser.StatementContext): DirectiveVisitor.DirectiveMap = {

		var result: Directive = null

		// TODO: functionally
		breakable {

			val semi = stmtCtx.getStart
			val i = semi.getTokenIndex

			val cmtChannel = tokens.getHiddenTokensToLeft(i, Java8Lexer.COMMENTS)
			if (cmtChannel != null && cmtChannel.size > 0) {
				val cmt = cmtChannel.get(cmtChannel.size - 1)	// get last comment

				val rawComment = cmt.getText
				val raw = rawComment.substring(2)

				// validate directive - starting with 'omp'
				val ompPattern = "^\\s*omp\\s.*$".r
				ompPattern.findFirstIn(raw) match {
					case Some(_) => ;
					case None    => break	// TODO: log
				}

				try {
					val ompLexer  = new OMPLexer(new ANTLRInputStream(raw))
					ompLexer.removeErrorListeners
					ompLexer.addErrorListener(new OMPLexerErrorListener )
					val ompTokens = new CommonTokenStream(ompLexer)
					
					val ompParser = new OMPParser(ompTokens)
					ompParser.removeErrorListeners
					ompParser.addErrorListener(new OMPLexerErrorListener )
					val ompCtx = ompParser.ompUnit

					result = stack.headOption match {
						case Some(parent) => Directive(parent, ompCtx, cmt, stmtCtx)
						case None => Directive(null, ompCtx, cmt, stmtCtx)
					}

				} catch {
					case e: SyntaxErrorException => throw new SyntaxErrorException("Syntax error before line " + stmtCtx.start.getLine + "': " + e.getMessage + "'", e)
					case e: Exception => throw new ParseException("Unexpected exception", e)
				}

			}	// if
		}	// breakable

		result match {
			case null => super.visitStatement(stmtCtx)
			case _    =>
				stack.push(result)
				val rr = ListMap(stmtCtx -> result) ++ super.visitStatement(stmtCtx)
				stack.pop()
				rr
		}
	}

	override def defaultResult() = ListMap()
	override def aggregateResult(a: DirectiveVisitor.DirectiveMap, b: DirectiveVisitor.DirectiveMap) = a ++ b

	/** Error listener implementation for simple throwing syntax exceptions */
	private class OMPLexerErrorListener extends BaseErrorListener {
		/** @throws SyntaxErrorException */		
		override def syntaxError(recognizer: Recognizer[_,_], offendingSymbol: Object, line: Int, charPositionInLine: Int, msg: String, e: RecognitionException) = {
			throw new SyntaxErrorException(msg, e)
		}
	}
}
