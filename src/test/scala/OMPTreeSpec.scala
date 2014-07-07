package org.omp4j.test

import java.io.File
import org.scalatest._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j._
import org.omp4j.exception._
import org.omp4j.preprocessor._
import org.omp4j.preprocessor.grammar._

/** LoadedContext with TranslationListener */
class OMPTreeLoadedContext(path: String) extends AbstractLoadedContext(path) {

	lazy val ompFile = new OMPFile(t, parser)(conf)
	lazy val conf = Config(Array[String](file.getAbsolutePath()))
	lazy val objectMethods = (new java.lang.Object ).getClass().getDeclaredMethods().map(_.getName()).toSet

	/** Get method names (without general Object methods) */
	def methods(n: Int) = {
		ompFile.classes(n).allMethods.map(_.getName()).toSet -- objectMethods
	}

	/** Get field names */
	def fields(n: Int) = {
		ompFile.classes(n).allFields.map(_.getName()).toSet
	}

}

/** Unit test for OMPTree */
class OMPTreeSpec extends AbstractSpec {

	val ompT1 = new OMPTreeLoadedContext("/ompTree/01.java")

	// methods
	ompT1.methods(0) should contain only ("publicSuperInherited", "protectedSuperInherited", "privateSuperInherited")
	ompT1.methods(1) should contain only ("publicSuperInherited", "protectedSuperInherited", "publicInherited", "protectedInherited", "privateInherited")
	ompT1.methods(2) should contain only ("publicSuperInherited", "protectedSuperInherited", "publicInherited", "protectedInherited", "publicNewMethod", "protectedNewMethod", "privateNewMethod")

	// fields
	ompT1.fields(0) should contain only ("publicSuperInheritedField", "protectedSuperInheritedField", "privateSuperInheritedField")
	ompT1.fields(1) should contain only ("publicSuperInheritedField", "protectedSuperInheritedField", "publicInheritedField", "protectedInheritedField", "privateInheritedField")
	ompT1.fields(2) should contain only ("publicSuperInheritedField", "protectedSuperInheritedField", "publicInheritedField", "protectedInheritedField", "publicNewField", "protectedNewField", "privateNewField")

}
