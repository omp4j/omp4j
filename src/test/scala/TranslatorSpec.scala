package org.omp4j.test

import java.io.File
import org.scalatest._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.preprocessor._
import org.omp4j.preprocessor.grammar._
import org.omp4j.exception._

/** LoadedContext with TranslationListener */
class TranslatorLoadedContext(path: String) extends AbstractLoadedContext(path) {

	/** Execute translation of the first directive */
	def tryTranslation = {
		(new Translator(tokens, parser, directives, null)(null)).translate(directives.head, null, Set[OMPVariable](), Set[OMPVariable](), Set[OMPVariable](), false, "")
	}
}

/** Unit test for Translator */
class TranslatorSpec extends AbstractSpec {

	// check initExpr. uniqueness
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/initExprUniqueness/01.java")).tryTranslation
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/initExprUniqueness/02.java")).tryTranslation

	// check condition validity
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/conditionValidity/01.java")).tryTranslation
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/conditionValidity/02.java")).tryTranslation
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/conditionValidity/03.java")).tryTranslation
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/conditionValidity/04.java")).tryTranslation
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/conditionValidity/05.java")).tryTranslation
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/conditionValidity/06.java")).tryTranslation

	// check increment validity
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/01.java")).tryTranslation
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/02.java")).tryTranslation
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/03.java")).tryTranslation
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/04.java")).tryTranslation
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/05.java")).tryTranslation
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/06.java")).tryTranslation
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/07.java")).tryTranslation
	an [ParseException] should be thrownBy (new TranslatorLoadedContext("/incValidity/08.java")).tryTranslation
}
