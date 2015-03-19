package org.omp4j.test

import org.omp4j.Config
import org.omp4j.exception.SyntaxErrorException
import org.omp4j.preprocessor.Preprocessor

/** SectionsContext */
class SectionsLoadedContext(path: String) extends AbstractLoadedContext(path) {
	def getFile = Array(file.getAbsolutePath)
}

/** Unit test for sections directive */
class SectionsSpec extends AbstractSpec {
	describe("Sections children in file...") {

		it("01.java should not contain other statements") {
			an [SyntaxErrorException] should be thrownBy new Preprocessor()(new Config(new SectionsLoadedContext("/sections/01.java").getFile)).run()
		}

		it("02.java should not contain other statements") {
			an [SyntaxErrorException] should be thrownBy new Preprocessor()(new Config(new SectionsLoadedContext("/sections/02.java").getFile)).run()
		}
	}

	describe("Sections must be before {...} statement - in file...") {

		it("03.java") {
			an [SyntaxErrorException] should be thrownBy new Preprocessor()(new Config(new SectionsLoadedContext("/sections/03.java").getFile)).run()
		}

		it("04.java") {
			an [SyntaxErrorException] should be thrownBy new Preprocessor()(new Config(new SectionsLoadedContext("/sections/04.java").getFile)).run()
		}
	}
}
