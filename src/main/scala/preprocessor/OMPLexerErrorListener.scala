package org.omp4j.preprocessor

import org.antlr.v4.runtime._
import org.omp4j.exception._
import org.omp4j.grammar._

/** Error listener implementation for simple throwing syntax exceptions */
class OMPLexerErrorListener extends BaseErrorListener {
	/** @throws SyntaxErrorException */		
	override def syntaxError(recognizer: Recognizer[_,_], offendingSymbol: Object, line: Int, charPositionInLine: Int, msg: String, e: RecognitionException) = {
		throw new SyntaxErrorException(msg, e)
	}
}
