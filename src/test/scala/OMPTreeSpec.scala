package org.omp4j.test

import java.io.File
import scala.collection.mutable.Stack

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
		ompFile.classes(n).allFields.map(_.name).toSet
	}

	def nestedClassName(n: Int, m: Int) = {
		ompFile.classes(n).nestedClasses(m)
	}

	def getClass(name: String) = {
		ompFile.getClass(name)
	}

	def getClass(names: Stack[String]) = {
		ompFile.getClass(names)
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

	val ompT2 = new OMPTreeLoadedContext("/ompTree/02.java")
	// nested classes
	ompT2.nestedClassName(0, 0).name should equal ("Middle1")
	ompT2.nestedClassName(0, 1).name should equal ("Middle2")
	ompT2.nestedClassName(0, 2).name should equal ("Middle3")
	ompT2.nestedClassName(0, 1).nestedClasses(1).name should equal ("Bottom22")

	ompT2.getClass("Top").name should equal ("Top")
	an [IllegalArgumentException] should be thrownBy ompT2.getClass("Middle1")
	ompT2.getClass(Stack[String]("Middle3", "Top")).name should equal ("Middle3")
	ompT2.getClass(Stack[String]("Bottom31", "Middle3", "Top")).name should equal ("Bottom31")
	an [IllegalArgumentException] should be thrownBy ompT2.getClass(Stack[String]("Top", "Middle3"))

	// FQN
	ompT2.getClass(Stack[String]("Bottom31", "Middle3", "Top")).FQN should equal ("Top$Middle3$Bottom31")

}
