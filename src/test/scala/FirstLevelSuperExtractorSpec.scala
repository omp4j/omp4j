package org.omp4j.test

import org.omp4j.extractor._

/** LoadedContext */
class FLSELoadedContext(path: String) extends AbstractLoadedContext(path) {

	/** Return number of found directives or throw SyntaxErrorException */
	def firstLevelSuperCount = {
		val supers = (new FirstLevelSuperExtractor ).visit(directives.head._2.ctx)
		cleanup()
		supers.size
	}

}

/** Unit test for FirstLevelSuperExtractor */
class FirstLevelSuperExtractorSpec extends AbstractSpec {

	describe("Number of found first level supers in file") {
		it("01.java should equal 1") {
			(new FLSELoadedContext("/super/01.java")).firstLevelSuperCount should equal (1)
		}
		it("02.java should equal 1") {
			(new FLSELoadedContext("/super/02.java")).firstLevelSuperCount should equal (1)
		}
		it("03.java should equal 0") {
			(new FLSELoadedContext("/super/03.java")).firstLevelSuperCount should equal (0)
		}
	}

}
