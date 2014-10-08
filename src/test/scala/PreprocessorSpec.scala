package org.omp4j.test

import java.io.File
import java.io.FilenameFilter
import org.scalatest._

import org.omp4j.utils.FileTreeWalker
import org.omp4j.preprocessor.Preprocessor

/** Unit test for Preprocessor */
class PreprocessorSpec extends AbstractSpec {

	// TODO: add exmaple/ to resources
	describe("Preprocessor should run example") {

		val exDir = new File("example/")
		val java8samples = List("12_lambda.java")
		exDir.listFiles(null: FilenameFilter).foreach{ f =>

			// all JDK versions			
			if (!(java8samples contains f.getName) || System.getProperty("java.version").startsWith("1.8.")) {
				it(f.getName) {
					(new Preprocessor(Array(f.getAbsolutePath))).run should equal (0)
				}
			} else {	// only for java8
				it(f.getName) {pending}
			}
		}

	}

	describe("Preprocessor should run extra sample") {

		it("01.java") {
			val f = new File(getClass.getResource("/preprocessor/01.java").toURI.getPath)
			(new Preprocessor(Array(f.getAbsolutePath))).run should equal (0)
		}
	}

}
