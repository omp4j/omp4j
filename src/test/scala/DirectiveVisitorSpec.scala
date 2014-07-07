package org.omp4j.test

import java.io.File
import org.scalatest._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.exception._
import org.omp4j.preprocessor._
import org.omp4j.preprocessor.grammar._

/** LoadedContext with TranslationListener */
class DCLoadedContext(path: String) extends AbstractLoadedContext(path) {

	/** Return number of found directives or throw SyntaxErrorException */
	def directiveCount = {
		val directives = (new DirectiveVisitor(tokens, parser)).visit(t)
		directives.size
	}

}

/** Unit test for DirectiveVisitor */
class DirectiveVisitorSpec extends AbstractSpec {

	// check count of found valid directives
	(new DCLoadedContext("/directiveCount/01.java")).directiveCount should equal (1)
	(new DCLoadedContext("/directiveCount/02.java")).directiveCount should equal (3)
	(new DCLoadedContext("/directiveCount/03.java")).directiveCount should equal (4)

	// check throwing SyntaxErrorException while processing invalid directives
	an [SyntaxErrorException] should be thrownBy (new DCLoadedContext("/directiveCount/04.java")).directiveCount
	an [SyntaxErrorException] should be thrownBy (new DCLoadedContext("/directiveCount/05.java")).directiveCount
	an [SyntaxErrorException] should be thrownBy (new DCLoadedContext("/directiveCount/06.java")).directiveCount

}
