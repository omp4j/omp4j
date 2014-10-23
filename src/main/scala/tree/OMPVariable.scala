package org.omp4j.tree

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

import org.omp4j.tree.OMPVariableType._

/** Static OMPVariable locator */
object OMPVariable {
	def apply(id: String, locals: Set[OMPVariable], params: Set[OMPVariable], ompClass: OMPClass) = {

		try {
			find(id, locals)
		} catch { case e: IllegalArgumentException => try {
			find(id, params)
		} catch { case e: IllegalArgumentException => try {
			findField(id, ompClass)
		}}}
	}

	// TODO: IllegalArgEx -> NoSuchElEx
	def findField(id: String, ompClass: OMPClass): OMPVariable = {
		(ompClass.allFields find (_.name == id)) match {
			case Some(v) => v
//			case None => throw new NoSuchElementException(s"variable '$id' not found")
			case None => throw new IllegalArgumentException(s"variable '$id' not found")
		}
	}

	private def find(id: String, set: Set[OMPVariable]): OMPVariable = {
		(set find (_.name == id)) match {
			case Some(v) => v
//			case None => throw new NoSuchElementException(s"variable '$id' not found")
			case None => throw new IllegalArgumentException(s"variable '$id' not found")
		}
	}
}

/** Variable representation */
case class OMPVariable(name: String, varType: String, meaning: OMPVariableType = OMPVariableType.Class, isPrivate: Boolean = false) {
	override def toString = s"Variable '$name' of type '$varType' with meaning of '$meaning'"

	lazy val fullName = s"${meaning}_$name"
	lazy val declaration = s"public $varType $fullName;"
}
