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
	/** Find variable using information given */
	// TODO: IllegalArgEx -> NoSuchElEx
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
	def find(id: String, set: Set[OMPVariable]): OMPVariable = {
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
case class OMPVariable(name: String, _varType: String, meaning: OMPVariableType = OMPVariableType.Class, isPrivate: Boolean = false) {
	override def toString = s"Variable '$name' of type '$varType' with meaning of '$meaning'"

	/** Number of array dimensions or 0 if not array */
	val dims = if (_varType.startsWith("[")) 1 + _varType.lastIndexOf('[') else 0

	/** rewritten name (without []) */
	lazy val fullName = s"${meaning}_$arrayLessName"

	val varType = if (dims == 0) _varType else {
		val typeChar = _varType.charAt(dims)
		typeChar match {
			case 'Z' => "bool"
			case 'B' => "byte"
			case 'C' => "char"
			case 'D' => "double"
			case 'F' => "float"
			case 'I' => "int"
			case 'J' => "long"
			case 'S' => "short"
			case 'L' => _varType.substring(dims+1, _varType.length-1)
		}
	}

	val bigVarType = varType match {
		case "bool" => "Boolean"
		case "byte" => "Byte"
		case "char" => "Character"
		case "double" => "Double"
		case "float" => "Float"
		case "int" => "Integer"
		case "long" => "Long"
		case "short" => "Short"
		case x => x
	}

	def defaultValue = {
		if (varType == bigVarType) ""
		else if (varType == "bool") "false"
		else "0"
	}

	private def bracks = {
		val brackets = new StringBuilder
		for (i <- 1 to dims) brackets append "[]"
		brackets.toString()
	}

	/** rewritten name (with []) */
	def fullNameWithBrackets = {
		s"${meaning}_$name$bracks"
	}

	/** declaration of variable in context (with []) */
	def declaration(asArr: Boolean = false) = {
		val extension = if (asArr) "[]" else ""
		s"public $varType $fullNameWithBrackets$extension;"
	}

	/** original name (without []) */
	lazy val arrayLessName =
		"^[^\\[]*".r findFirstIn name match {
			case Some(x) => x
			case None    => name
		}
}
