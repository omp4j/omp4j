package org.omp4j.test

import java.io.File
import org.scalatest._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.tree._
import org.omp4j.grammar._
import org.omp4j.exception._
import org.omp4j.preprocessor._
import org.omp4j.extractor.Inheritor

/** LoadedContext with TranslationListener */
class InheritorLoadedContext(path: String) extends AbstractLoadedContext(path) {

	val ompFile = new OMPFile(t, parser)

	/** Variable string in format: "<type> <identifier>" e.g. "int ok1" etc. */
	private def varAsText(v: OMPVariable) = s"${v.varType} ${v.name}"

	/** Return set of possible inherited local variables as formated strings */
	def localsAsText = Inheritor.getPossiblyInheritedLocals(directives.head.ctx).map(varAsText)

	/** Return set of possible inherited params as formated strings */
	def paramsAsText = Inheritor.getPossiblyInheritedParams(directives.head.ctx).map(varAsText)

	/** Return size of parent-list */
	def getParentListSize = Inheritor.getParentList(directives.head.ctx).size

	/** Return set of possible inherited local classes as formated strings */
	def localClassesAsText = Inheritor.getVisibleLocalClasses(directives.head.ctx, ompFile).map(_.name)

	/** Return set of directly visible non-local classes */
	def nonlocalClassesAsText = Inheritor.getVisibleNonLocalClasses(directives.head.ctx, ompFile).map(_.name)
}

/** Unit test for Iheritor */
class InheritorSpec extends AbstractSpec {

	describe("Inherited local variables in file") {
		it("01.java should contain only...") {
			(new InheritorLoadedContext("/inheritedLocals/01.java")).localsAsText should contain only ("int ok1", "int ok2", "int ok3")
		}
		it("02.java should contain only...") {
			(new InheritorLoadedContext("/inheritedLocals/02.java")).localsAsText should contain only ("int ok1", "int ok2", "int ok3", "String ok4", "int ok5", "float ok6", "int ok7")
		}
		it("03.java should contain only...") {
			(new InheritorLoadedContext("/inheritedLocals/03.java")).localsAsText should contain only ("int ok1", "int ok2")
		}
		it("04.java should contain only...") {
			(new InheritorLoadedContext("/inheritedLocals/04.java")).localsAsText should contain only ("int ok1", "int ok2")
		}
		it("05.java should contain only...") {
			(new InheritorLoadedContext("/inheritedLocals/05.java")).localsAsText should contain only ("int ok1", "int ok2", "JButton button")
		}
		it("06.java should contain only...") {
			(new InheritorLoadedContext("/inheritedLocals/06.java")).localsAsText should contain only ("int x")
		}
		it("07.java should contain only...") {
			(new InheritorLoadedContext("/inheritedLocals/07.java")).localsAsText should contain only ("int[] arr1", "int[] arr2", "int[] arr3")
		}
	}

	describe("Inherited parameters in file") {
		it("01.java should contain only...") {
			(new InheritorLoadedContext("/inheritedParams/01.java")).paramsAsText should contain only ("String[] args", "int a", "String b", "float c")
		}
		it("08.java should contain only...") {
			(new InheritorLoadedContext("/inheritedParams/08.java")).paramsAsText should contain only ("ActionEvent e")
		}
		it("09.java should contain only...") {
			(new InheritorLoadedContext("/inheritedParams/09.java")).paramsAsText should contain only ("String[] param")
		}
	}

	// Java8 specific code
	if (System.getProperty("java.version").startsWith("1.8.")) {
		describe("Inherited parameters (including lambdas) in file") {
			it("02.java should contain only...") {
				(new InheritorLoadedContext("/inheritedParams/02.java")).paramsAsText should contain only ("String[] args", "int a", "String b", "float c")
			}
			it("03.java should contain only...") {
				(new InheritorLoadedContext("/inheritedParams/03.java")).paramsAsText should contain only ("String[] args", "int a", "String b", "float c", "int d")
			}
			it("04.java should contain only...") {
				(new InheritorLoadedContext("/inheritedParams/04.java")).paramsAsText should contain only ("String[] args", "int a", "String b", "float c", "int d", "int e", "int f")
			}
		}
	} else {	// others
		describe("Inherited parameters (including lambdas) in file") {
			it ("for Java8 only") {pending}
		}
	}

	// Java8 specific code
	if (System.getProperty("java.version").startsWith("1.8.")) {
		describe("Inherited invalid parameters (including lambdas) in file") {
			it("05.java should throw ParseException") {
				an [ParseException] should be thrownBy (new InheritorLoadedContext("/inheritedParams/05.java")).paramsAsText
			}
			it("06.java should throw ParseException") {
				an [ParseException] should be thrownBy (new InheritorLoadedContext("/inheritedParams/06.java")).paramsAsText
			}
			it("07.java should throw ParseException") {
				an [ParseException] should be thrownBy (new InheritorLoadedContext("/inheritedParams/07.java")).paramsAsText
			}
		}
	} else {	// others
		describe("Inherited invalid parameters (including lambdas) in file") {
			it ("for Java8 only") {pending}
		}
	}

	describe("Parent list size in file") {
		it("01.java should equal 33") {
			(new InheritorLoadedContext("/parentListSize/01.java")).getParentListSize should equal (33)
		}
		it("02.java should equal 46") {
			(new InheritorLoadedContext("/parentListSize/02.java")).getParentListSize should equal (46)
		}
	}

	describe("Visible (first directive) local classes in file") {
		it("01.java should contain only...") {
			(new InheritorLoadedContext("/visibleLocalClasses/01.java")).localClassesAsText should contain only ("A", "P", "B", "C", "D", "E", "F", "G")
		}
	}

	describe("Visible (first directive) non-local classes in file") {
		it("01.java should contain only...") {
			(new InheritorLoadedContext("/visibleLocalClasses/01.java")).nonlocalClassesAsText should contain only ("H", "I", "J", "K", "L", "M", "N", "O")
		}
		it("02.java should contain only...") {
			(new InheritorLoadedContext("/visibleLocalClasses/02.java")).nonlocalClassesAsText should contain allOf ("java", "notjava")
		}
	}

}
