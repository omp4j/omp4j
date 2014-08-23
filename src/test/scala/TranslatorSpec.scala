package org.omp4j.test

import java.io.File
import org.scalatest._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.preprocessor._
import org.omp4j.preprocessor.grammar._

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

	def getParentListSize = {
		val neck = (new Translator(tokens, parser, directives, null)(null)).getParentList(directives.head.ctx)
		neck.size
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
}
