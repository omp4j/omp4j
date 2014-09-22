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
class ACELoadedContext(path: String) extends AbstractLoadedContext(path) {

	/** Return number of found anonymous classes */
	def firstAnonClassCount = {
		val breaks = (new AnonymousClassExtractor ).visit(directives.head.ctx)
		breaks.size
	}

}

/** Unit test for AnonymousClassExtractor */
class AnonymousClassExtractorSpec extends AbstractSpec {

	describe("Number of found first level anon. classes in file") {

		it("01.java should equal 1") {
			(new ACELoadedContext("/anonClass/01.java")).firstAnonClassCount should equal (1)
		}

		it("02.java should equal 1") {
			(new ACELoadedContext("/anonClass/02.java")).firstAnonClassCount should equal (1)
		}

		it("03.java should equal 1") {
			(new ACELoadedContext("/anonClass/03.java")).firstAnonClassCount should equal (1)
		}

	}

}
