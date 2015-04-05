package org.omp4j.test

import org.omp4j.extractor._
import org.omp4j.grammar.Java8Parser

/** LoadedContext with TranslationListener */
class FLBELoadedContext(path: String) extends AbstractLoadedContext(path) {

	/** Return number of found directives or throw SyntaxErrorException */
	def firstLevetBreakCount = {
		val breaks = (new FirstLevelBreakExtractor ).visit(directives.head._2.ctx.asInstanceOf[Java8Parser.StatementContext].forStatement.basicForStatement)
		cleanup()
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
		it("03.java should equal 1 (label)") {
			(new FLBELoadedContext("/firstLevelBreak/03.java")).firstLevetBreakCount should equal (1)
		}
	}

}
