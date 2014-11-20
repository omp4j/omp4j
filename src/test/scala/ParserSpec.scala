package org.omp4j.test

import org.omp4j.grammar._
import org.omp4j.exception._
import org.omp4j.preprocessor._
import org.antlr.v4.runtime._

/** Unit test for omp4j-grammar Parser */
class ParserSpec extends AbstractSpec {

	def parse(text: String) = {
		val ompLexer  = new OMPLexer(new ANTLRInputStream(text))
		ompLexer.removeErrorListeners
		ompLexer.addErrorListener(new OMPLexerErrorListener )
		val ompTokens = new CommonTokenStream(ompLexer)
		
		val ompParser = new OMPParser(ompTokens)
		ompParser.removeErrorListeners
		ompParser.addErrorListener(new OMPLexerErrorListener )

		val ompCtx = ompParser.ompUnit
		ompCtx
	}

	def parseOk(text: String) = {
		parse(text)
		true
	}

	describe("Parser should parse simple directives such as") {
		val oks = List(
			"omp parallel",
			"omp parallel for",
			"omp sections",
			"omp section",
			"omp single",
			"omp master",
			"omp barrier",
			"omp critical",
			"omp critical(variable)",
			"omp atomic"
		)

		oks.foreach(text =>
			it(text) {
				parseOk(text) should equal(true)
			}

		)
	}

	describe("Parser should parse all 'omp parallel' options: [TODO]") {
		val oks = List(
			"omp parallel threadNum(15)",
			"omp parallel threadNum(15) schedule(static)",
			"omp parallel schedule(static)",
			"omp parallel threadNum(15) schedule(static) public(fooVar)",
			"omp parallel threadNum(15) schedule(static) public(fooVar) public(gooVar)",
			"omp parallel threadNum(15) schedule(static) public(fooVar) private(gooVar)",
			"omp parallel threadNum(15) schedule(static) public(fooVar, gooVar) private(gooVar)",
			"omp parallel threadNum(15) schedule(static) public(fooVar, gooVar) private(fooVar, gooVar)",
			"omp parallel schedule(static) threadNum(15)",
			"omp parallel schedule(static) threadNum(15) public(fooVar)",
			"omp parallel schedule(static) threadNum(15) public(fooVar) public(gooVar)",
			"omp parallel public(fooVar) public(gooVar)",
			"omp parallel schedule(static) public(fooVar) threadNum(15) private(gooVar)",
			"omp parallel threadNum(15) public(fooVar, gooVar) private(gooVar) schedule(static)",
			"omp parallel public(fooVar, gooVar) threadNum(15) schedule(static) private(fooVar, gooVar)"
		)

		oks.foreach(text =>
			it(text) {
				parseOk(text) should equal(true)
			}
		)
	}
	describe("Parser should fail while parsing invalid 'omp parallel' options: [TODO]") {
		val wrongs = List(
			"omp parallel threadNum(15) threadNum(1)",
			"omp parallel schedule(static) schedule(static)",
			"omp parallel public(fooVar, gooVar) threadNum(15) schedule(static) private(fooVar, gooVar) sthDifferent",
			"omp parallel sthDifferent public(fooVar, gooVar) threadNum(15) schedule(static) private(fooVar, gooVar) ",
			"omp parallel public(fooVar, gooVar) sthDifferent threadNum(15) schedule(static) private(fooVar, gooVar) ",
			"omp parallel public(fooVar, gooVar) threadNum(15) schedule(static) sthDifferent private(fooVar, gooVar) "
		)

		wrongs.foreach(text =>
			it(text) {
				an [SyntaxErrorException] should be thrownBy parseOk(text)
			}

		)
	}
}
