package org.omp4j.test

import org.antlr.v4.runtime.TokenStreamRewriter
import org.omp4j.exception._
import org.omp4j.preprocessor._

/** LoadedContext with TranslationListener */
class DCLoadedContext(path: String) extends AbstractLoadedContext(path) {

	/** Return number of found directives or throw SyntaxErrorException */
	def directiveCount = {
		val directives = (new DirectiveVisitor(tokens, parser)).visit(t)
		cleanup()
		directives.size
	}

	/** Execute translation of the first directive */
	def tryTranslation = {
		val rewriter = new TokenStreamRewriter(tokens)
		val res = directives.head._2.translate(rewriter, ompFile, directives)
		cleanup()
		res
	}

}

/** Unit test for DirectiveVisitor */
class DirectiveVisitorSpec extends AbstractSpec {

	// check count of found valid directives
	describe("Number of found directives in file") {
		it("01.java should equal 1") {
			(new DCLoadedContext("/directiveCount/01.java")).directiveCount should equal (1)
		}
		it("02.java should equal 3") {
			(new DCLoadedContext("/directiveCount/02.java")).directiveCount should equal (3)
		}
		it("03.java should equal 4") {
			(new DCLoadedContext("/directiveCount/03.java")).directiveCount should equal (4)
		}
		it("07.java should equal 4") {
			(new DCLoadedContext("/directiveCount/07.java")).directiveCount should equal (4)
		}
	}

	// check throwing SyntaxErrorException while processing invalid directives
	describe("SyntaxErrorException should be through from file") {
		it("04.java") {
			an [SyntaxErrorException] should be thrownBy (new DCLoadedContext("/directiveCount/04.java")).directiveCount
		}
		it("05.java") {
			an [SyntaxErrorException] should be thrownBy (new DCLoadedContext("/directiveCount/05.java")).directiveCount
		}
		it("06.java") {
			an [SyntaxErrorException] should be thrownBy (new DCLoadedContext("/directiveCount/06.java")).directiveCount
		}
	}

	// check translation syntax errors
	describe("Missing init. expression in file") {

		it("01.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/initExprUniqueness/01.java")).tryTranslation
		}

		it("02.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/initExprUniqueness/02.java")).tryTranslation
		}

	}

	describe("Invalid cond. expression in file") {

		it("01.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/conditionValidity/01.java")).tryTranslation
		}

		it("02.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/conditionValidity/02.java")).tryTranslation
		}

		it("03.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/conditionValidity/03.java")).tryTranslation
		}

		it("04.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/conditionValidity/04.java")).tryTranslation
		}

		it("05.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/conditionValidity/05.java")).tryTranslation
		}

		it("06.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/conditionValidity/06.java")).tryTranslation
		}

	}

	describe("Invalid inc. expression in file") {

		it("01.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/incValidity/01.java")).tryTranslation
		}

		it("02.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/incValidity/02.java")).tryTranslation
		}

		it("03.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/incValidity/03.java")).tryTranslation
		}

		it("04.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/incValidity/04.java")).tryTranslation
		}

		it("05.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/incValidity/05.java")).tryTranslation
		}

		it("06.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/incValidity/06.java")).tryTranslation
		}

		it("07.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/incValidity/07.java")).tryTranslation
		}

		it("08.java should cause ParseException") {
			an [ParseException] should be thrownBy (new DCLoadedContext("/incValidity/08.java")).tryTranslation
		}

	}

}
