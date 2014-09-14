package org.omp4j.test

import java.io.File
import scala.collection.mutable.Stack

import org.scalatest._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j._
import org.omp4j.tree._
import org.omp4j.grammar._
import org.omp4j.exception._
import org.omp4j.preprocessor._

/** LoadedContext with TranslationListener */
class OMPTreeLoadedContext(path: String) extends AbstractLoadedContext(path) {

	/** Tested OMPFile*/
	lazy val ompFile = new OMPFile(t, parser)

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

	// fields
	val ompT1 = new OMPTreeLoadedContext("/ompTree/01.java")
	ompT1.totalClassCount should equal (3)
	ompT1.fields(0) should contain only ("publicSuperInheritedField", "protectedSuperInheritedField", "privateSuperInheritedField")
	ompT1.fields(1) should contain only ("publicSuperInheritedField", "protectedSuperInheritedField", "publicInheritedField", "protectedInheritedField", "privateInheritedField")
	ompT1.fields(2) should contain only ("publicSuperInheritedField", "protectedSuperInheritedField", "publicInheritedField", "protectedInheritedField", "publicNewField", "protectedNewField", "privateNewField")

	// inner classes
	val ompT2 = new OMPTreeLoadedContext("/ompTree/02.java")
	ompT2.totalClassCount should equal (13)
	ompT2.innerClass(0,0).name should equal ("Middle1")
	ompT2.innerClass(0,1).name should equal ("Middle2")
	ompT2.innerClass(0,2).name should equal ("Middle3")
	ompT2.innerClass(0,1).innerClasses(1).name should equal ("Bottom22")

	// local classes
	val ompT3 = new OMPTreeLoadedContext("/ompTree/03.java")
	ompT3.totalClassCount should equal (7)
	ompT3.localClasses(0) should contain only ("Local01", "Local02", "Local03", "Local04")
	ompT3.localClassClasses(0,1) should contain only ("NLocal01")
	ompT3.localClassClasses(0,3) should contain only ("NLocal02")

	// fields of local classes
	val ompT4 = new OMPTreeLoadedContext("/ompTree/04.java")
	ompT4.totalClassCount should equal (7)
	ompT4.localClassFields(0,0) should contain only ("publicLocal01Field", "protectedLocal01Field", "privateLocal01Field")

	val ompT5 = new OMPTreeLoadedContext("/ompTree/05.java")
	ompT5.totalClassCount should equal (3)
	ompT5.localClassFields(0,0) should contain only ("publicLocal01Field", "protectedLocal01Field", "privateLocal01Field")
	ompT5.localClassFields(0,1) should contain only ("publicLocal01Field", "protectedLocal01Field", "publicLocal02Field", "protectedLocal02Field", "privateLocal02Field")

	val ompT6 = new OMPTreeLoadedContext("/ompTree/06.java")
	ompT6.totalClassCount should equal (2)
	ompT6.localClassFields(0,0) should contain allOf ("publicLocal01Field", "protectedLocal01Field", "privateLocal01Field", "separator")
	
	// packages (FQN, name)
	val ompT7 = new OMPTreeLoadedContext("/ompTree/07.java")
	ompT7.totalClassCount should equal (4)
	ompT7.topClass(0).FQN should equal ("org.domain.test.First")
	ompT7.topClass(0).packageNamePrefix() should equal ("org.domain.test.")
	ompT7.innerClass(2,0).FQN should equal ("org.domain.test.Third$Inner")

	// advanced nesting
	val ompT8 = new OMPTreeLoadedContext("/ompTree/08.java")
	ompT8.innerClass(0,1).name should equal ("Nested1")
	ompT8.topClass(0).innerClasses.size should equal (2)
	ompT8.innerClass(0,1).innerClasses.size should equal (0)
	ompT8.innerClass(0,1).localClasses.size should equal (1)
}
