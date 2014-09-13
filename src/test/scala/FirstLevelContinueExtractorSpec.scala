package org.omp4j.test

import java.io.File
import org.scalatest._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.grammar._
import org.omp4j.exception._
import org.omp4j.extractor._
import org.omp4j.preprocessor._

/** LoadedContext with TranslationListener */
class FLCELoadedContext(path: String) extends AbstractLoadedContext(path) {

	/** Return number of found directives or throw SyntaxErrorException */
	def firstLevetContinueCount = {
		val breaks = (new FirstLevelContinueExtractor ).visit(directives.head.ctx.forStatement.basicForStatement)
		breaks.size
	}

}

/** Unit test for DirectiveVisitor */
class FirstLevelContinueExtractorSpec extends AbstractSpec {

	// check count of found first level continues
	(new FLCELoadedContext("/firstLevelContinue/01.java")).firstLevetContinueCount should equal (0)
	(new FLCELoadedContext("/firstLevelContinue/02.java")).firstLevetContinueCount should equal (1)


}
