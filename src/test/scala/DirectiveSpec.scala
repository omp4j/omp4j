package org.omp4j.test

import scala.collection.JavaConverters._

/** LoadedContext with TranslationListener */
class DirectiveLoadedContext(path: String) extends AbstractLoadedContext(path) {
	tokens.getTokens.asScala.toList.foreach(t => conf.tokenSet.testAndSet(t.getText))
	def uniqueName(str: String) = directives.head._2.uniqueName(str)
}

/** Unit test for directive */
class DirectiveSpec extends AbstractSpec {
	val dlc1 = (new DirectiveLoadedContext("/directive/01.java"))

	describe("Used variable/class names should not be generated") {
		val varsAndClasses = List("one", "two", "three", "Four", "five", "six", "seven", "eight", "nine")
		varsAndClasses.foreach { v =>
			it(v) {
				dlc1.uniqueName(v) should not equal (v)
			}
		}
	}

	describe("Java 5,6,7,8 keywords should not be generated") {
		val keywords = List("abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while")
		keywords.foreach { v =>
			it(v) {
				dlc1.uniqueName(v) should not equal (v)
			}
		}
	}

	describe("Loadable classes should not be generated") {
		val loadables = List("ActionListener", "JButton")
		loadables.foreach { v =>
			it(v) {
				dlc1.uniqueName(v) should not equal (v)
			}
		}
	}

	describe("Unused variable names should be generated") {
		val unmodified = List("foo", "bar", "iterI")
		unmodified.foreach { v =>
			it(v) {
				dlc1.uniqueName(v) should equal(v)
			}
		}
	}

	dlc1.cleanup()
}
