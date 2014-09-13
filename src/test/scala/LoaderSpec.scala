package org.omp4j.test

import java.io.File
import org.scalatest._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.grammar._
import org.omp4j.exception._
import org.omp4j.preprocessor._

/** LoadedContext with TranslationListener */
class LoaderLoadedContext(path: String) extends AbstractLoadedContext(path) {}

/** Unit test for Loader */
class LoaderSpec extends AbstractSpec {

	// create loader
	val llc1 = new LoaderLoadedContext("/loader/01.java")
	val loader1 = llc1.conf.loader
	val cunit1 = llc1.t

	// test existence
	llc1.conf.loader should not equal (null)

	// simple properties
	loader1.load("File", cunit1).getName should equal ("java.io.File")
	an [ClassNotFoundException] should be thrownBy loader1.load("FileXYZ", cunit1)
	loader1.load("java.awt.Container", cunit1).getName should equal ("java.awt.Container")
	an [ClassNotFoundException] should be thrownBy loader1.load("Container", cunit1)

	// test packaging
	loader1.load("Simple", cunit1).getName should equal ("org.pack.Simple")
	loader1.load("Another", cunit1).getName should equal ("org.pack.Another")
	loader1.load("org.pack.Another", cunit1).getName should equal ("org.pack.Another")
	loader1.load("org.pack.Another$Inner", cunit1).getName should equal ("org.pack.Another$Inner")
	loader1.load("Another$Inner", cunit1).getName should equal ("org.pack.Another$Inner")
}
