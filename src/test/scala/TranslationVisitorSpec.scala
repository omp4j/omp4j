package org.omp4j.test

import org.antlr.v4.runtime.TokenStreamRewriter
import org.omp4j.directive.Directive
import org.omp4j.extractor.FirstLevelContinueExtractor
import org.omp4j.preprocessor.TranslationVisitor

/** LoadedContext for TranslationVisitor */
class TVLoadedContext(path: String) extends AbstractLoadedContext(path) {


	/** Get captured variables */
	def capturedAsText(n: Int): Set[String] = {
		val d: Directive = directives.toList(n)._2
		val tv = new TranslationVisitor(new TokenStreamRewriter(tokens), ompFile, d)
		tv.visit(d.ctx)
		tv.getCaptured.map(varAsText)
	}


}

/** */
class TranslationVisitorSpec extends AbstractSpec {
//	val tv = (new TVLoadedContext("/translationVisitor/01.java"))
//	println(tv.t.toStringTree(tv.parser))

	describe("Captured in file...") {

		it("01.java should contain only...") {
			(new TVLoadedContext("/translationVisitor/01.java")).capturedAsText(0) should contain theSameElementsAs List("int supa_arr[][]", "AtomicInteger ra", "int nuta")
		}
	}




}
