package org.omp4j.test

import java.io.{File, FilenameFilter}

import org.omp4j.Config
import org.omp4j.preprocessor.Preprocessor

/** Unit test for Preprocessor */
class PreprocessorSpec extends AbstractSpec {

	describe("Preprocessor should run example") {

		val exDir = new File("example/")
		val java8samples = List("12_lambda.java")
		exDir.listFiles(null: FilenameFilter).foreach{ f =>

			// all JDK versions			
			if (!(java8samples contains f.getName) || System.getProperty("java.version").startsWith("1.8.")) {
				it(f.getName) {
					(new Preprocessor()(new Config(Array(f.getAbsolutePath)))).run should equal (0)
				}
			} else {	// only for java8
				it(f.getName) {pending}
			}
		}

	}

	describe("Preprocessor should run extra sample") {

		it("01.java") {
			val f = new File(getClass.getResource("/preprocessor/01.java").toURI.getPath)
			(new Preprocessor()(new Config(Array(f.getAbsolutePath)))).run should equal (0)
		}
	}

}
