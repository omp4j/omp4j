package org.omp4j.test

import org.omp4j.exception._
import org.omp4j.preprocessor._

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

	describe("Number of found directives in file") {
		it("01.java should equal 1") {
			(new DCLoadedContext("/directiveCount/01.java")).directiveCount should equal (1)
		}
		it("02.java should equal 3") {
			(new DCLoadedContext("/directiveCount/02.java")).directiveCount should equal (3)
		}
		it("03.java should equal 4") {
			(new DCLoadedContext("/directiveCount/03.java")).directiveCount should equal (4)
		}
		it("07.java should equal 4") {
			(new DCLoadedContext("/directiveCount/07.java")).directiveCount should equal (4)
		}
	}

	describe("SyntaxErrorException should be through from file") {
		it("04.java") {
			an [SyntaxErrorException] should be thrownBy (new DCLoadedContext("/directiveCount/04.java")).directiveCount
		}
		it("05.java") {
			an [SyntaxErrorException] should be thrownBy (new DCLoadedContext("/directiveCount/05.java")).directiveCount
		}
		it("06.java") {
			an [SyntaxErrorException] should be thrownBy (new DCLoadedContext("/directiveCount/06.java")).directiveCount
		}
	}
	// check count of found valid directives

	// check throwing SyntaxErrorException while processing invalid directives

}
