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
	def apply(arrayLessId: String, locals: Set[OMPVariable], params: Set[OMPVariable], ompClass: OMPClass) = {

		try {
			find(arrayLessId, locals)
		} catch { case e: IllegalArgumentException => try {
			find(arrayLessId, params)
		} catch { case e: IllegalArgumentException => try {
			findField(arrayLessId, ompClass)
		}}}
	}

	// TODO: IllegalArgEx -> NoSuchElEx
	def findField(id: String, ompClass: OMPClass): OMPVariable = {
		(ompClass.allFields find (_.arrayLessName == id)) match {
			case Some(v) => v
//			case None => throw new NoSuchElementException(s"variable '$id' not found")
			case None => throw new IllegalArgumentException(s"variable '$id' not found")
		}
	}

	// TODO: IllegalArgEx -> NoSuchElEx
	private def find(id: String, set: Set[OMPVariable]): OMPVariable = {
		(set find (_.arrayLessName == id)) match {
			case Some(v) => v
//			case None => throw new NoSuchElementException(s"variable '$id' not found")
			case None => throw new IllegalArgumentException(s"variable '$id' not found")
		}
	}
}

/** Variable representation
  * @param name original variable name (may include [])
  * */
case class OMPVariable(name: String, varType: String, meaning: OMPVariableType = OMPVariableType.Class, isPrivate: Boolean = false) {
	override def toString = s"Variable '$name' of type '$varType' with meaning of '$meaning'"

	/** rewritten name (without []) */
	lazy val fullName = s"${meaning}_$arrayLessName"

	/** rewritten name (with []) */
	lazy val fullNameWithBrackets = s"${meaning}_$name"

	/** declaration of variable in context (with []) */
	lazy val declaration = s"public $varType $fullNameWithBrackets;"

	/** original name (without []) */
	lazy val arrayLessName =
		"^[^\\[]*".r findFirstIn name match {
			case Some(x) => x
			case None    => name
		}
}
