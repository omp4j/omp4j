package org.omp4j.test

import org.omp4j.tree._

/** LoadedContext with TranslationListener */
class OMPTreeLoadedContext(path: String) extends AbstractLoadedContext(path) {

	/** Total number of all (registred) classes*/
	def totalClassCount = ompFile.classMap.size

	/** Return n-th first-level class */
	def topClass(n: Int) = ompFile.classes(n)

	/** Get n-th top-class field names */
	def fields(n: Int) = topClass(n).allFields.map(_.name).toSet

	/** Get field names of the m-th inner class of the n-th top class */
	def localClassFields(n: Int, m: Int) = topClass(n).localClasses(m).allFields.map(_.name).toSet

	/** Get m-th inner class of the n-th top class */
	def innerClass(n: Int, m: Int) = topClass(n).innerClasses(m)

	/** Get local classes of the n-th top class */
	def localClasses(n: Int) = topClass(n).localClasses.map(_.name).toSet

	/** Get m-th local class of the n-th top class */
	def localClassClasses(n: Int, m: Int) = topClass(n).localClasses(m).localClasses.map(_.name).toSet
}

/** Unit test for OMPTree */
class OMPTreeSpec extends AbstractSpec {

	val ompT1  = new OMPTreeLoadedContext("/ompTree/01.java")
	val ompT2  = new OMPTreeLoadedContext("/ompTree/02.java")
	val ompT3  = new OMPTreeLoadedContext("/ompTree/03.java")
	val ompT4  = new OMPTreeLoadedContext("/ompTree/04.java")
	val ompT5  = new OMPTreeLoadedContext("/ompTree/05.java")
	val ompT6  = new OMPTreeLoadedContext("/ompTree/06.java")
	val ompT7  = new OMPTreeLoadedContext("/ompTree/07.java")
	val ompT8  = new OMPTreeLoadedContext("/ompTree/08.java")
	val ompT9  = new OMPTreeLoadedContext("/ompTree/09.java")
	val ompT10 = new OMPTreeLoadedContext("/ompTree/10.java")
	val ompT11 = new OMPTreeLoadedContext("/ompTree/11.java")

	describe("Total class count in context") {
		
		it("ompT1 should be 3") {
			ompT1.totalClassCount should equal (3)
		}
		
		it("ompT2 should be 13") {
			ompT2.totalClassCount should equal (13)
		}
		
		it("ompT3 should be 7") {
			ompT3.totalClassCount should equal (7)
		}
		
		it("ompT4 should be 7") {
			ompT4.totalClassCount should equal (7)
		}
		
		it("ompT5 should be 3") {
			ompT5.totalClassCount should equal (3)
		}
		
		it("ompT6 should be 2") {
			ompT6.totalClassCount should equal (2)
		}
		
		it("ompT7 should be 4") {
			ompT7.totalClassCount should equal (4)
		}
		
		it("ompT8 should be 7") {
			ompT8.totalClassCount should equal (7)
		}
		
		it("ompT9 should be 22") {
			ompT9.totalClassCount should equal (22)
		}

		it("ompT10 should be 2") {
			ompT10.totalClassCount should equal (2)
		}

		it("ompT11 should be 3") {
			ompT11.totalClassCount should equal (3)
		}

	}

	describe("Fields of ompT1 class") {
		
		it("#0 should contain only...") {
			ompT1.fields(0) should contain only ("publicSuperInheritedField", "protectedSuperInheritedField", "privateSuperInheritedField")
		}
	
		it("#1 should contain only...") {
			ompT1.fields(1) should contain only ("publicSuperInheritedField", "protectedSuperInheritedField", "publicInheritedField", "protectedInheritedField", "privateInheritedField")
		}

		it("#2 should contain only...") {
			ompT1.fields(2) should contain only ("publicSuperInheritedField", "protectedSuperInheritedField", "publicInheritedField", "protectedInheritedField", "publicNewField", "protectedNewField", "privateNewField")
		}
		
	}

	describe("Inner class of") {
		
		it("ompt2#0,0 should equal Middle1") {
			ompT2.innerClass(0,0).name should equal ("Middle1")
		}

		it("ompt2#0,1 should equal Middle2") {
			ompT2.innerClass(0,1).name should equal ("Middle2")
		}

		it("ompt2#0,2 should equal Middle3") {
			ompT2.innerClass(0,2).name should equal ("Middle3")
		}

		it("ompt8#0,1 should equal Nested1") {
			ompT8.innerClass(0,1).name should equal ("Nested1")
		}

		it("ompt8#0,1#1 should equal Bottom22") {
			ompT2.innerClass(0,1).innerClasses(1).name should equal ("Bottom22")
		}

	}

	describe("Find class applied to") {
		
		it("ompt2#top(0) should equal Top") {
			ompT2.topClass(0).findClass(Array()).name should equal ("Top")
		}

		it("ompt2#top(0) should equal Middle1") {
			ompT2.topClass(0).findClass(Array("Middle1")).name should equal ("Middle1")
		}

		it("ompt2#top(0) should equal Bottom12") {
			ompT2.topClass(0).findClass(Array("Middle1", "Bottom12")).name should equal ("Bottom12")
		}

		it("ompt2#top(0) (array) should equal Bottom12") {
			ompT2.topClass(0).findClass("Middle1.Bottom12".split("\\.")).name should equal ("Bottom12")
		}

		it("ompt2#ompFile should equal Top") {
			ompT2.ompFile.findClass(Array("Top")).name should equal ("Top")
		}

		it("ompt2#ompFile should equal Bottom12") {
			ompT2.ompFile.findClass("Top.Middle1.Bottom12".split("\\.")).name should equal ("Bottom12")
		}

	}

	describe("Find class should throw IllegalArgumentException when applying") {
		
		it("Middle1.Bottom32 to ompt2#top(0)") {
			an [IllegalArgumentException] should be thrownBy ompT2.topClass(0).findClass("Middle1.Bottom32".split("\\."))
		}
		
		it("Middle.Bottom12 to ompt2#top(0)") {
			an [IllegalArgumentException] should be thrownBy ompT2.topClass(0).findClass("Middle.Bottom12".split("\\."))
		}
		
		it("Tox to ompt2#ompFile") {
			an [IllegalArgumentException] should be thrownBy ompT2.ompFile.findClass("Tox".split("\\."))
		}
		
		it("Top.Middle1.Bottom145 to ompt2#ompFile") {
			an [IllegalArgumentException] should be thrownBy ompT2.ompFile.findClass("Top.Middle1.Bottom145".split("\\."))
		}

	}

	describe("Local classes of") {
		
		it("ompt3#0 should contain only...") {
			ompT3.localClasses(0) should contain only ("Local01", "Local02", "Local03", "Local04")
		}

		it("ompt3#0,1 should contain...") {
			ompT3.localClassClasses(0,1) should contain only ("NLocal01")
		}

		it("ompt3#0,3 should contain...") {
			ompT3.localClassClasses(0,3) should contain only ("NLocal02")
		}

	}

	describe("Fields of local classes of") {
		
		it("ompt4#0,0 should contain only...") {
			ompT4.localClassFields(0,0) should contain only ("publicLocal01Field", "protectedLocal01Field", "privateLocal01Field")
		}

		it("ompt5#0,0 should contain only...") {
			ompT5.localClassFields(0,0) should contain only ("publicLocal01Field", "protectedLocal01Field", "privateLocal01Field")
		}

		it("ompt5#0,1 should contain only...") {
			ompT5.localClassFields(0,1) should contain only ("publicLocal01Field", "protectedLocal01Field", "publicLocal02Field", "protectedLocal02Field", "privateLocal02Field")
		}

		it("ompt6#0,0 should contain all of...") {
			ompT6.localClassFields(0,0) should contain allOf ("publicLocal01Field", "protectedLocal01Field", "privateLocal01Field") // "separator" is final
		}

		it("ompt9#0,0 should contain all of...") {
			ompT9.localClassFields(0,0) should contain allOf ("local1Public", "local1Protected", "local1Private", "inner1Public", "inner1Protected")
		}

		it("ompt9#0,1 should contain all of...") {
			ompT9.localClassFields(0,1) should contain allOf ("local2Public", "local2Protected", "local2Private", "x", "y")
		}

		it("ompt9#0,2 should contain all of...") {
			ompT9.localClassFields(0,2) should contain allOf ("local2Public", "local2Protected", "local3Public", "local3Protected", "local3Private", "x", "y")
		}

		it("ompt9#0,3 should contain all of...") {
			ompT9.localClassFields(0,3) should contain allOf ("abcPublic", "abcProtected")
		}

		it("ompt9#0,4 should contain bububu") {
			ompT9.localClassFields(0,4) should contain       ("bububu")
		}

	}

	describe("Package prefix of") {
		
		it("ompt7 should equal org.domain.test.") {
			ompT7.topClass(0).packageNamePrefix() should equal ("org.domain.test.")
		}

	}
	
	describe("FQN of") {
		
		it("ompt7#0 should equal org.domain.test.First") {
			ompT7.topClass(0).FQN should equal ("org.domain.test.First")
		}

		it("ompt7#iiner(2,0) should equal org.domain.test.Third$Inner") {
			ompT7.innerClass(2,0).FQN should equal ("org.domain.test.Third$Inner")
		}

	}

	describe("Advanced inner classes size of") {

		it("ompT8#top(0) should equal 2") {
			ompT8.topClass(0).innerClasses.size should equal (2)
		}

		it("ompT8#inner(0,1) should equal 0") {
			ompT8.innerClass(0,1).innerClasses.size should equal (0)
		}

	}

	describe("Advanced local classes size of") {

		it("ompT8#inner(0,1) should equal 1") {
			ompT8.innerClass(0,1).localClasses.size should equal (1)
		}

		it("ompT9#top(0) should equal 5") {
			ompT9.topClass(0).localClasses.size should equal (5)
		}

	}

	describe("Final fields should be ignored in") {
		it("01.java") {
			(new OMPTreeLoadedContext("/finalFieldIgnorance/01.java")).fields(0) should contain only ("publicSuperInheritedField", "protectedSuperInheritedField", "privateSuperInheritedField")
		}
		it("02.java") {
			(new OMPTreeLoadedContext("/finalFieldIgnorance/02.java")).localClassFields(0,0) should contain allOf ("publicLocal01Field", "protectedLocal01Field", "privateLocal01Field")
		}

	}

}
