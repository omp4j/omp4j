package org.omp4j.test

/** LoadedContext with TranslationListener */
class LoaderLoadedContext(path: String) extends AbstractLoadedContext(path) {}

/** Unit test for Loader */
class LoaderSpec extends AbstractSpec {

	describe("Loader (01.java)") {
		val llc1 = new LoaderLoadedContext("/loader/01.java")
		val loader1 = llc1.conf.loader
		val cunit1 = llc1.t

		it("should not equal null") {
			llc1.conf.loader should not equal (null)
		}

		it("should load File") {
			loader1.load("File", cunit1).getName should equal ("java.io.File")
		}

		it("should load ActionListener") {
			loader1.load("ActionListener", cunit1).getName should equal ("java.awt.event.ActionListener")
		}

		it("should load java.awt.Container") {
			loader1.load("java.awt.Container", cunit1).getName should equal ("java.awt.Container")
		}

		it("should load Simple") {
			loader1.load("Simple", cunit1).getName should equal ("org.pack.Simple")
		}

		it("should load Another") {
			loader1.load("Another", cunit1).getName should equal ("org.pack.Another")
		}

		it("should load org.pack.Another") {
			loader1.load("org.pack.Another", cunit1).getName should equal ("org.pack.Another")
		}

		it("should load org.pack.Another$Inner") {
			loader1.load("org.pack.Another$Inner", cunit1).getName should equal ("org.pack.Another$Inner")
		}

		it("should load Another$Inner") {
			loader1.load("Another$Inner", cunit1).getName should equal ("org.pack.Another$Inner")
		}

		it("should throw ClassNotFoundException while loading FileXYZ") {
			an [ClassNotFoundException] should be thrownBy loader1.load("FileXYZ", cunit1)
		}

		it("should throw ClassNotFoundException while loading Container") {
			an [ClassNotFoundException] should be thrownBy loader1.load("Container", cunit1)
		}

	}

}
