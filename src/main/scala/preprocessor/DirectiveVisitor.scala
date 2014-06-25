package org.omp4j.preprocessor

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._
import org.antlr.v4.runtime.dfa.DFA

import java.util.BitSet
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._

import org.omp4j.preprocessor.grammar._
import org.omp4j.preprocessor.exception._

/** Fetch list of Directives (aka OMPParseTree, corresponding statement and parsers) */
class DirectiveVisitor(tokens: CommonTokenStream, parser: Java8Parser) extends Java8BaseVisitor[List[Directive]] {

	/** Save proper statement */
	override def visitStatement(stmtCtx: Java8Parser.StatementContext) = {

		var result = List[Directive]()

		breakable {

		val semi = stmtCtx.getStart()
		val i = semi.getTokenIndex()

		val cmtChannel = tokens.getHiddenTokensToLeft(i, Java8Lexer.COMMENTS)
		if (cmtChannel != null && cmtChannel.size() > 0) {
			val cmt = cmtChannel.get(cmtChannel.size() - 1)	// get last comment
			// println(stmtCtx.getText() + "\t'" + cmt.getText() + "'")

			val rawComment = cmt.getText()
			val raw = rawComment.substring(2)

			// validate directive - starting with 'omp'
			val ompPattern = "^\\s*omp\\s.*$".r
			ompPattern.findFirstIn(raw) match {
				case Some(_) => ;
				case None    => println("Ignoring directive '" + raw + "'")	// TODO: log
				                break
			}

			try {
				val ompLexer  = new OMPLexer(new ANTLRInputStream(raw))
				ompLexer.removeErrorListeners();
				ompLexer.addErrorListener(new OMPLexerErrorListener())

				val ompTokens = new CommonTokenStream(ompLexer)
				
				val ompParser = new OMPParser(ompTokens)
				ompParser.removeErrorListeners();
				ompParser.addErrorListener(new OMPLexerErrorListener())

				val ompCtx = ompParser.ompUnit()

				result = List[Directive](new Directive(ompCtx, ompParser, stmtCtx, parser))
			} catch {
				case e: SyntaxErrorException => println("Syntax error before line " + stmtCtx.start.getLine() + ": '" + e.getMessage() + "'")	// TODO: log
				case e: Exception => throw new ParseException("Unexpected exception", e)
			}

		}	// if
		}	// breakable

		result ::: visitChildren(stmtCtx)
	}

	override def defaultResult() = List[Directive]()
	override def aggregateResult(a: List[Directive], b: List[Directive]) = a ::: b

	/** Error listener implementation for simple throwing syntax exceptions */
	private class OMPLexerErrorListener() extends BaseErrorListener {
		/** @throws SyntaxErrorException */		
		override def syntaxError(recognizer: Recognizer[_,_], offendingSymbol: Object, line: Int, charPositionInLine: Int, msg: String, e: RecognitionException) = {
			throw new SyntaxErrorException(msg, e)
		}
	}
}
