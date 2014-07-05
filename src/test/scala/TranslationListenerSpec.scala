package org.omp4j.test

import java.io.File
import org.scalatest._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.preprocessor._
import org.omp4j.preprocessor.grammar._

/** Unit test for TranslationListener */
class TranslationListenerSpec extends AbstractSpec {
	private class LoadedContext(path: String) {
		val file = new File(getClass.getResource(path).toURI().getPath())
		lazy val lexer = new Java8Lexer(new ANTLRFileStream(file.getPath()))
		lazy val tokens = new CommonTokenStream(lexer)
		lazy val parser = new Java8Parser(tokens)
		lazy val t = parser.compilationUnit()
		lazy val directives = (new DirectiveVisitor(tokens, parser)).visit(t)
	
		def getAsText = {
			val inherLocals: Set[Java8Parser.LocalVariableDeclarationContext] = (new TranslationListener(directives, tokens, null, parser)).getPossiblyInheritedLocals(directives.head.ctx)
			inherLocals.map{ l => l.`type`().getText() + " " + l.variableDeclarators().variableDeclarator(0).variableDeclaratorId().getText() }
		}
	}

	// check inherited vars in block after first and the only directive
	(new LoadedContext("/inheritedLocals/01.java")).getAsText should contain only ("int ok1", "int ok2", "int ok3")
	(new LoadedContext("/inheritedLocals/02.java")).getAsText should contain only ("int ok1", "int ok2", "int ok3", "String ok4", "int ok5", "float ok6", "int ok7")
	// TODO: listener (anonymous class)
	// TODO: nested class
}
