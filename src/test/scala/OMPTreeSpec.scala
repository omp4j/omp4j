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

	/** Return n-th first-level class */
	def topClass(n: Int) = ompFile.classes(n)

	//////

	// lazy val objectMethods = (new java.lang.Object ).getClass.getDeclaredMethods.map(_.getName).toSet

	/** Total number of all (registred) classes*/
	def totalClassCount = ompFile.classMap.size

	/** Get method names (without general Object methods) */
	// def methods(n: Int) = ompFile.classes(n).allMethods.map(_.getName).toSet -- objectMethods

	/** Get field names */
	def fields(n: Int) = ompFile.classes(n).allFields.map(_.name).toSet
	def localClassFields(n: Int, m: Int) = ompFile.classes(n).localClasses(m).allFields.map(_.name).toSet

	def innerClass(n: Int, m: Int) = ompFile.classes(n).innerClasses(m)
	// def innerClassCount(n: Int) = ompFile.classes(n).size
	def getClass(name: String) = ompFile.getClass(name)

	// TODO:
	// def getClass(names: Stack[String]) = {
	// 	ompFile.getClass(names.map(new StackClass))
	// }

	def localClasses(n: Int) = ompFile.classes(n).localClasses.map(_.name).toSet
	def localClassClasses(n: Int, m: Int) = ompFile.classes(n).localClasses(m).localClasses.map(_.name).toSet
}

/** Unit test for OMPTree */
class OMPTreeSpec extends AbstractSpec {

	val ompT1 = new OMPTreeLoadedContext("/ompTree/01.java")
	ompT1.totalClassCount should equal (3)

	// methods
	// ompT1.methods(0) should contain only ("publicSuperInherited", "protectedSuperInherited", "privateSuperInherited")
	// ompT1.methods(1) should contain only ("publicSuperInherited", "protectedSuperInherited", "publicInherited", "protectedInherited", "privateInherited")
	// ompT1.methods(2) should contain only ("publicSuperInherited", "protectedSuperInherited", "publicInherited", "protectedInherited", "publicNewMethod", "protectedNewMethod", "privateNewMethod")

	// fields
	ompT1.fields(0) should contain only ("publicSuperInheritedField", "protectedSuperInheritedField", "privateSuperInheritedField")
	ompT1.fields(1) should contain only ("publicSuperInheritedField", "protectedSuperInheritedField", "publicInheritedField", "protectedInheritedField", "privateInheritedField")
	ompT1.fields(2) should contain only ("publicSuperInheritedField", "protectedSuperInheritedField", "publicInheritedField", "protectedInheritedField", "publicNewField", "protectedNewField", "privateNewField")

	val ompT2 = new OMPTreeLoadedContext("/ompTree/02.java")
	ompT2.totalClassCount should equal (13)

	// nested classes
	ompT2.innerClass(0, 0).name should equal ("Middle1")
	ompT2.innerClass(0, 1).name should equal ("Middle2")
	ompT2.innerClass(0, 2).name should equal ("Middle3")
	ompT2.innerClass(0, 1).innerClasses(1).name should equal ("Bottom22")

	ompT2.getClass("Top").name should equal ("Top")
	an [IllegalArgumentException] should be thrownBy ompT2.getClass("Middle1")

	// ompT2.getClass(Stack("Middle3", "Top")).name should equal ("Middle3")
	// ompT2.getClass(Stack("Bottom31", "Middle3", "Top")).name should equal ("Bottom31")
	// an [IllegalArgumentException] should be thrownBy ompT2.getClass(Stack("Top", "Middle3"))

	// FQN
	// ompT2.getClass(Stack("Bottom31", "Middle3", "Top")).FQN should equal ("Top$Middle3$Bottom31")

	val ompT3 = new OMPTreeLoadedContext("/ompTree/03.java")
	ompT3.totalClassCount should equal (7)

	// local classes
	ompT3.localClasses(0) should contain only ("Local01", "Local02", "Local03", "Local04")
	ompT3.localClassClasses(0,1) should contain only ("NLocal01")
	ompT3.localClassClasses(0,3) should contain only ("NLocal02")


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
	
	val ompT7 = new OMPTreeLoadedContext("/ompTree/07.java")
	ompT7.totalClassCount should equal (4)
	ompT7.getClass("First").FQN should equal ("org.domain.test.First")
	ompT7.getClass("First").packageNamePrefix() should equal ("org.domain.test.")
	ompT7.innerClass(2,0).FQN should equal ("org.domain.test.Third$Inner")

	val ompT8 = new OMPTreeLoadedContext("/ompTree/08.java")
	ompT8.topClass(0).innerClasses(1).name should equal ("Nested1")
	ompT8.topClass(0).innerClasses.size should equal (2)
	ompT8.topClass(0).innerClasses(1).innerClasses.size should equal (0)
	ompT8.topClass(0).innerClasses(1).localClasses.size should equal (1)

}
