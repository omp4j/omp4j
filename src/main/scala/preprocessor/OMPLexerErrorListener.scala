package org.omp4j.preprocessor

import org.antlr.v4.runtime._
import org.omp4j.exception._
import org.omp4j.grammar._

/** Lexer error listener. */
class OMPLexerErrorListener extends BaseErrorListener {

	/** Only throws exceptions
	 * @inheritdoc
	 * @throws SyntaxErrorException on error
	 */
	override def syntaxError(recognizer: Recognizer[_,_], offendingSymbol: Object, line: Int, charPositionInLine: Int, msg: String, e: RecognitionException) = {
		throw new SyntaxErrorException(msg, e)
	}
}
