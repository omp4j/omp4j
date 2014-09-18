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
class FLBELoadedContext(path: String) extends AbstractLoadedContext(path) {

	/** Return number of found directives or throw SyntaxErrorException */
	def firstLevetBreakCount = {
		val breaks = (new FirstLevelBreakExtractor ).visit(directives.head.ctx.forStatement.basicForStatement)
		breaks.size
	}

}

/** Unit test for DirectiveVisitor */
class FirstLevelBreakExtractorSpec extends AbstractSpec {

	describe("Number of found first level breaks in file") {
		it("01.java should equal 0") {
			(new FLBELoadedContext("/firstLevelBreak/01.java")).firstLevetBreakCount should equal (0)
		}
		it("02.java should equal 1") {
			(new FLBELoadedContext("/firstLevelBreak/02.java")).firstLevetBreakCount should equal (1)
		}
	}

}
