package org.omp4j.tree

/** Variable-meaning enum  */
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

	/** Find variable using information given
	  *
	  * @param arrayLessId id of the variable
	  * @param locals currently visible local variables
	  * @param params currently visible parameters
	  * @param ompClass model of the current class
	  * @return found variable
	  * @throws NoSuchElementException if variable not found
	  */
	def apply(arrayLessId: String, locals: Set[OMPVariable], params: Set[OMPVariable], ompClass: OMPClass) = {

		try {
			find(arrayLessId, locals)
		} catch { case e: NoSuchElementException => try {
			find(arrayLessId, params)
		} catch { case e: NoSuchElementException => try {
			findField(arrayLessId, ompClass)
		}}}
	}

	/** Find field name using information given
	  *
	  * @param id id of the variable
	  * @param ompClass model of the current class
	  * @return found variable
	  * @throws NoSuchElementException if variable not found
	  */
	def findField(id: String, ompClass: OMPClass): OMPVariable = {
		ompClass.allFields find (_.arrayLessName == id) match {
			case Some(v) => v
			case None => throw new NoSuchElementException(s"variable '$id' not found")
		}
	}

	/**
	  *
	  * @param id id of the variable
	  * @param set set of variable in which the search should be invokec
 	  * @return found variable
	  * @throws NoSuchElementException if variable not found
	  */
	def find(id: String, set: Set[OMPVariable]): OMPVariable = {
		set find (_.arrayLessName == id) match {
			case Some(v) => v
			case None => throw new NoSuchElementException(s"variable '$id' not found")
		}
	}
}

/** Variable representation
  *
  * @constructor fetch all important information
  * @param name original variable name (may include [])
  * @param _varType Java type of the variable
  * @param meaning meaning of the variable
  * @param isPrivate is this variable private (if meaning == field)
  */
 case class OMPVariable(name: String, _varType: String, meaning: OMPVariableType = OMPVariableType.Class, isPrivate: Boolean = false) {
	override def toString = s"Variable '$name' of type '$varType' with meaning of '$meaning'"

	/** Number of array dimensions or 0 if not array */
	val dims = if (_varType.startsWith("[")) 1 + _varType.lastIndexOf('[') else 0

	/** The rewritten name (without []) */
	lazy val fullName = s"${meaning}_$arrayLessName"

	/** Variable type*/
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

	/** Boxed variable type */
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

	/** Default value depending on the type */
	def defaultValue = {
		if (varType == bigVarType) ""
		else if (varType == "bool") "false"
		else "0"
	}

	/** Brackets useable for variable init. */
	private def bracks = {
		val brackets = new StringBuilder
		for (i <- 1 to dims) brackets append "[]"
		brackets.toString()
	}

	/** Rewritten name (with []) */
	def fullNameWithBrackets = {
		s"${meaning}_$name$bracks"
	}

	/** Declaration of variable in context (with []) */
	def declaration(asArr: Boolean = false) = {
		val extension = if (asArr) "[]" else ""
		s"public $varType $fullNameWithBrackets$extension;"
	}

	/** Original name (without []) */
	lazy val arrayLessName =
		"^[^\\[]*".r findFirstIn name match {
			case Some(x) => x
			case None    => name
		}
}
