package org.omp4j.test

import org.antlr.v4.runtime._
import org.omp4j.exception._
import org.omp4j.preprocessor._
import org.omp4j.tree._

/** LoadedContext with TranslationListener */
class TranslatorLoadedContext(path: String) extends AbstractLoadedContext(path) {

	/** Execute translation of the first directive */
	def tryTranslation = {
		(new Translator(new TokenStreamRewriter(tokens), parser, directives, null)(null)).translate(directives.head._2, Set[OMPVariable](), Set[OMPVariable](), Set[OMPVariable](), false, "")
	}
}

/** Unit test for Translator */
class TranslatorSpec extends AbstractSpec {

	describe("Missing init. expression in file") {

		it("01.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/initExprUniqueness/01.java")).tryTranslation
		}

		it("02.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/initExprUniqueness/02.java")).tryTranslation
		}

	}

	describe("Invalid cond. expression in file") {

		it("01.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/conditionValidity/01.java")).tryTranslation
		}

		it("02.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/conditionValidity/02.java")).tryTranslation
		}

		it("03.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/conditionValidity/03.java")).tryTranslation
		}

		it("04.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/conditionValidity/04.java")).tryTranslation
		}

		it("05.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/conditionValidity/05.java")).tryTranslation
		}

		it("06.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/conditionValidity/06.java")).tryTranslation
		}

	}

	describe("Invalid inc. expression in file") {

		it("01.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/01.java")).tryTranslation
		}

		it("02.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/02.java")).tryTranslation
		}

		it("03.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/03.java")).tryTranslation
		}

		it("04.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/04.java")).tryTranslation
		}

		it("05.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/05.java")).tryTranslation
		}

		it("06.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/06.java")).tryTranslation
		}

		it("07.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/07.java")).tryTranslation
		}

		it("08.java should cause ParseException") {
			an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/08.java")).tryTranslation
		}

	}

}
