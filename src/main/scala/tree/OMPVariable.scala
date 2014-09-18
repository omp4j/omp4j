package org.omp4j.tree

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.grammar._
import org.omp4j.exception._

/** Variable meaning enum  */
object OMPVariableType extends Enumeration {
	type OMPVariableType = Value
	val Local = Value("local")
	val Field = Value("field")
	val Class = Value("class")
	val Liter = Value("liter")
	val Param = Value("param")
	val This  = Value("this")
}

import OMPVariableType._

/** Static OMPVariable constructor */
object OMPVariable {
	def apply(id: String, locals: Set[OMPVariable], params: Set[OMPVariable], ompFile: OMPFile, keyCtx: Java8Parser.ClassDeclarationContext) = {
		ompFile.classMap.get(keyCtx) match {
			case Some(clazz) =>
				(locals find (_.name == id)) match {
					case Some(v) => new OMPVariable(id, v.varType, OMPVariableType.Local)
					case None => (params find (_.name == id)) match {
						case Some(v) => new OMPVariable(id, v.varType, OMPVariableType.Param)
						case None => (clazz.allFields find (_.name == id)) match {
							case Some(v) => new OMPVariable(id, v.varType, OMPVariableType.Field)
							case None => throw new IllegalArgumentException(s"Variable '$id' not found in locals/params/fields")
						}	// field
					}	// params
				}	// locals

			case None => throw new ParseException("class not loaded")
		}	// get
	}
}

/** Variable representation */
case class OMPVariable(name: String, varType: String, meaning: OMPVariableType = OMPVariableType.Class, isPrivate: Boolean = false) {
	override def toString = s"Variable '$name' of type '$varType' with meaning of '$meaning'"
}
