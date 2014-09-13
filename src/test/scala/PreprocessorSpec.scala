package org.omp4j.test

import java.io.File
import java.io.FilenameFilter
import org.scalatest._

import org.omp4j.utils.FileTreeWalker
import org.omp4j.preprocessor.Preprocessor

/** Unit test for Preprocessor */
class PreprocessorSpec extends AbstractSpec {

	// test examples (valid compilation after sources are preprocessed)
	// TODO: add exmaple/ to resources
	val exDir = new File("example/")
	exDir.listFiles(null: FilenameFilter).foreach{ f =>
		(new Preprocessor(Array(f.getAbsolutePath))).run should equal (0)
	}
}
