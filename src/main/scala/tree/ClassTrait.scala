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

/** The basic class trait. Reflectable and Nonreflectable traits inherits from this trait. */
trait ClassTrait {
	/** Abstract fields and vars that are mapped to real fields and (implicit) params */
	val THIS: OMPClass
	val name: String
	val FQN: String
	val ctx: Java8Parser.ClassDeclarationContext
	val classBody: Java8Parser.ClassBodyContext
	val parent: OMPClass
	val parser: Java8Parser
	val conf: Config
	val ompFile: OMPFile
	val innerClasses: List[OMPClass]
	val cunit: Java8Parser.CompilationUnitContext
	
	def packageNamePrefix(pt: ParserRuleContext = ctx): String

	/** List of local classes (first level only) */
	val localClasses: List[OMPClass] = classBody.classBodyDeclaration.asScala
		.filter(d => d.classMemberDeclaration != null)
		.map(_.classMemberDeclaration)
		.filter(m => m.methodDeclaration != null)
		.map(_.methodDeclaration)	// methods
		.map((new ClassExtractor ).visit(_))
		.flatten
		.map(new LocalClass(_, THIS, parser)(conf, ompFile))
		.toList

	/** Recursively build array of class fields
	  * @param clazz fields of this class are returned
	  * @param firstRun If set to True, private Fields will be included (but not parents ones)
	  * @return Array of Fields
	  */
	protected def findAllFieldsRecursively(clazz: Class[_], firstRun: Boolean): Array[OMPVariable] = {
		val superClazz = clazz.getSuperclass
		superClazz match {
			case null =>
				if (firstRun) {
					clazz.getDeclaredFields
						.filter(f => ! Modifier.isFinal(f.getModifiers))
						.map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Field, Modifier.isPrivate(f.getModifiers)))
				} else {
					clazz.getDeclaredFields
						.filter(f => ! Modifier.isPrivate(f.getModifiers))
						.filter(f => ! Modifier.isFinal(f.getModifiers))
						.map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Field, false))
				}
			case _    =>
				if (firstRun) {
					(clazz.getDeclaredFields
						.filter(f => ! Modifier.isFinal(f.getModifiers))
						.map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Field, Modifier.isPrivate(f.getModifiers)))
					) ++ findAllFieldsRecursively(superClazz, false)
				} else {
					(clazz.getDeclaredFields
						.filter(f => ! Modifier.isPrivate(f.getModifiers))
						.filter(f => ! Modifier.isFinal(f.getModifiers))
						.map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Field, false))
					) ++ findAllFieldsRecursively(superClazz, false)
				}
		}
	}

}
