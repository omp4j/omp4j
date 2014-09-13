package org.omp4j.tree

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import java.lang.reflect.Modifier

import Array._

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
	def packageNamePrefix(pt: ParserRuleContext = ctx): String

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
