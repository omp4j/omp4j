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

	/** Return set of string <type> <identifier> such as "int ok1" etc.*/
	def localsAsText = {
		val inherLocals = (new Translator(tokens, parser, directives, null)(null)).getPossiblyInheritedLocals(directives.head.ctx)
		inherLocals.map{ v => v.varType + " " + v.name }
	}

	/** Return set of string <type> <identifier> such as "int ok1" etc.*/
	def paramsAsText = {
		val inherLocals = (new Translator(tokens, parser, directives, null)(null)).getPossiblyInheritedParams(directives.head.ctx)
		inherLocals.map{ v => v.varType + " " + v.name }
	}

	/** Return size of parent-list */
	def getParentListSize = {
		val neck = (new Translator(tokens, parser, directives, null)(null)).getParentList(directives.head.ctx)
		neck.size
	}

	/** Execute translation of the first directive */
	def tryTranslation = {
		(new Translator(tokens, parser, directives, null)(null)).translate(directives.head, null, Set[OMPVariable](), Set[OMPVariable](), Set[OMPVariable](), false, "")
	}

}

/** Unit test for Translator */
class TranslatorSpec extends AbstractSpec {

	// check inherited vars in block after first (and the only) directive
	(new TranslatorLoadedContext("/inheritedLocals/01.java")).localsAsText should contain only ("int ok1", "int ok2", "int ok3")
	(new TranslatorLoadedContext("/inheritedLocals/02.java")).localsAsText should contain only ("int ok1", "int ok2", "int ok3", "String ok4", "int ok5", "float ok6", "int ok7")
	(new TranslatorLoadedContext("/inheritedLocals/03.java")).localsAsText should contain only ("int ok1", "int ok2")
	(new TranslatorLoadedContext("/inheritedLocals/04.java")).localsAsText should contain only ("int ok1", "int ok2")
	(new TranslatorLoadedContext("/inheritedLocals/05.java")).localsAsText should contain only ("int ok1", "int ok2")

	// check method params
	(new TranslatorLoadedContext("/inheritedParams/01.java")).paramsAsText should contain only ("String[] args", "int a", "String b", "float c")

	// check parentlist size
	(new TranslatorLoadedContext("/parentListSize/01.java")).getParentListSize should equal (33)

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
