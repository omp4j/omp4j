package org.omp4j.tree

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import java.lang.reflect.Modifier

import Array._
import scala.collection.JavaConverters._

import org.omp4j.tree._
import org.omp4j.Config
import org.omp4j.extractor._
import org.omp4j.grammar._

trait ClassTrait {
	val THIS: OMPClass
	val name: String
	val FQN: String
	val ctx: Java8Parser.ClassDeclarationContext
	val parent: OMPClass
	val parser: Java8Parser
	val conf: Config
	val classMap: OMPFile.ClassMap
	val innerClasses: List[OMPClass]

	def packageNamePrefix(pt: ParserRuleContext = ctx): String

	val localClasses: List[OMPClass] = ctx.normalClassDeclaration.classBody.classBodyDeclaration.asScala
		.filter(d => d.classMemberDeclaration != null)
		.map(_.classMemberDeclaration)
		.filter(m => m.methodDeclaration != null)
		.map(_.methodDeclaration)	// methods
		.map((new ClassExtractor ).visit(_))
		.flatten
		.map(new LocalClass(_, THIS, parser)(conf, classMap))
		.toList

	// if (parent == null) println(s"$name\t(parent = TOP) type\t${this.getClass.getName}\t(${innerClasses.size}|${localClasses.size})")
	// else println(s"$name\t(parent = ${parent.name}) type\t${this.getClass.getName}\t(${innerClasses.size}|${localClasses.size})")


	/** Recursively build array of class fields
	  * @param clazz fields of this class are returned
	  * @param firstRun If set to True, private Fields will be included (but not parents ones)
	  * @return Array of Fields
	  */
	def findAllFieldsRecursively(clazz: Class[_], firstRun: Boolean): Array[OMPVariable] = {
		val superClazz = clazz.getSuperclass
		superClazz match {
			case null =>
				if (firstRun) clazz.getDeclaredFields.map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Class, Modifier.isPrivate(f.getModifiers)))
				else clazz.getDeclaredFields.filter(f => ! Modifier.isPrivate(f.getModifiers)).map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Class, false))
			case _    =>
				if (firstRun) concat(clazz.getDeclaredFields.map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Class, Modifier.isPrivate(f.getModifiers))), findAllFieldsRecursively(superClazz, false))
				else concat(clazz.getDeclaredFields.filter(f => ! Modifier.isPrivate(f.getModifiers)).map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Class, false)), findAllFieldsRecursively(superClazz, false))
		}
	}

}
