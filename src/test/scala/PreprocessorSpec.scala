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
		exDir.listFiles(null: FilenameFilter).foreach{ f =>
			it(f.getName) {
				(new Preprocessor(Array(f.getAbsolutePath))).run should equal (0)
			}
		}
	}

}
