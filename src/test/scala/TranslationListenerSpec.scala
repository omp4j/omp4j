package org.omp4j.test

import java.io.File
import org.scalatest._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.preprocessor._
import org.omp4j.preprocessor.grammar._

/** LoadedContext with TranslationListener */
class LoadedContext(path: String) extends AbstractLoadedContext(path) {
	/** Return set of string <type> <identifier> such as "int ok1" etc.*/
	def tranLinAsText = {
		val inherLocals = (new TranslationListener(directives, tokens, null, parser)).getPossiblyInheritedLocals(directives.head.ctx)
		inherLocals.map{ l => l.`type`().getText() + " " + l.variableDeclarators().variableDeclarator(0).variableDeclaratorId().getText() }
	}
}

/** Unit test for TranslationListener */
class TranslationListenerSpec extends AbstractSpec {

	// check inherited vars in block after first and the only directive
	(new LoadedContext("/inheritedLocals/01.java")).tranLinAsText should contain only ("int ok1", "int ok2", "int ok3")
	(new LoadedContext("/inheritedLocals/02.java")).tranLinAsText should contain only ("int ok1", "int ok2", "int ok3", "String ok4", "int ok5", "float ok6", "int ok7")
	(new LoadedContext("/inheritedLocals/03.java")).tranLinAsText should contain only ("int ok1", "int ok2")
	(new LoadedContext("/inheritedLocals/04.java")).tranLinAsText should contain only ("int ok1", "int ok2")
	(new LoadedContext("/inheritedLocals/05.java")).tranLinAsText should contain only ("int ok1", "int ok2")

}
