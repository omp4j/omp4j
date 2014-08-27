package org.omp4j.preprocessor

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import java.lang.reflect.Method
import java.lang.reflect.Field
import java.lang.reflect.Modifier

import Array._
import scala.language.existentials
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack

import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.extractor._
import org.omp4j.preprocessor.grammar._

abstract class OMPBase(ctx: ParserRuleContext, parser: Java8Parser)(implicit conf: Config) {
	override def toString = ctx.toStringTree(parser)
}

/** File representation containing list of classes */
class OMPFile(ctx: Java8Parser.CompilationUnitContext, parser: Java8Parser)(implicit conf: Config) extends OMPBase(ctx, parser) {

	/** Classes in file */
	lazy val classes = (new ClassExtractor ).visit(ctx).map(c => new OMPClass(c, null, parser))

	def getClass(name: String): OMPClass = {
		val filtered = classes.filter(_.name == name)
		filtered.size match {
			case 0 => throw new IllegalArgumentException("Class '" + name + "' not found (1)")
			case 1 => filtered.head
			case _ => throw new IllegalArgumentException("Class '" + name + "'  found multiple times")
		} 
	}

	def getClass(names: Stack[String]): OMPClass = {

		def getClassRec(namesRev: List[String], clazz: OMPClass): OMPClass = {
			namesRev.size match {
				case 1 => clazz.getNestedClass(namesRev.head)
				case _ => getClassRec(namesRev.tail, clazz.getNestedClass(namesRev.head))
			} 
		}

		val revNames = names.reverse
		revNames.size match {
			case 0 => throw new IllegalArgumentException("Empty stack passed")
			case 1 => getClass(revNames.head)
			case _ => getClassRec(revNames.tail.toList, getClass(revNames.head))
		} 
	}

}

/** Class representation containing list of methods, fields and nested classes */
class OMPClass(ctx: Java8Parser.ClassDeclarationContext, parent: OMPClass, parser: Java8Parser)(implicit conf: Config) extends OMPBase(ctx, parser) {
	/** String class name */
	lazy val name: String = ctx.normalClassDeclaration.Identifier.getText

	lazy val FQN: String = parent match {	// TODO package?
		case null => name
		case _    => parent.FQN + "$" + name
	}

	/** List of nested classes */
	lazy val nestedClasses = (new ClassExtractor ).visit(ctx.normalClassDeclaration.classBody).map(c => new OMPClass(c, this, parser))

	/** List of methods directly implemented (or overriden) in the class */
	// lazy val implementedMethods = (new MethodExtractor ).visit(ctx.classBody).map(c => new OMPMethod(c, parser))

	/** List of all methods (including inherited ones) */
	lazy val allMethods: Array[Method] = findAllMethods

	/** List of all fields directly implemented in the class */
	// lazy val implementedFields = (new FieldExtractor ).visit(ctx.classBody).map(c => new OMPVariable(c.`type`, c.variableDeclarators, parser))

	/** Set of declared and inherited fields */
	lazy val fields: Set[OMPVariable] = findAllFields.map(f => new OMPVariable(f.getName, f.getType.getName)).toSet

	/** Set of all fields referable from this class context */
	lazy val allFields: Set[OMPVariable] = parent match {
		case null => fields
		case _    => fields ++ parent.allFields
	}

	/** Find nested class by simple name
	  * @param name String name of class
	  * @throws IllegalArgumentException If class was found more than once or none class found
	  * @return OMPClass object
	  */
	def getNestedClass(name: String) = {
		val filtered = nestedClasses.filter(_.name == name)
		filtered.size match {
			case 0 => throw new IllegalArgumentException("Class '" + name + "' not found (2)")
			case 1 => filtered.head
			case _ => throw new IllegalArgumentException("Class '" + name + "'  found multiple times")
		} 
	}

	/** Find all methods via reflection (only for field allMethods)
	  * @param name String name of class
	  * @throws ParseException If class was found by ANTLR but not by reflection
	  * @throws SecurityException From Class.getDeclaredMethods
	  * @return Array of Methods
	  */
	protected def findAllMethods: Array[Method] = {
		/** Recursively build array of class methods
		  * @param clazz Methods of this class are returned
		  * @param firstRun If set to True, private methods will be included (but not parents ones)
		  * @return Array of Methods
		  */
		def findAllMethodsRecursively(clazz: Class[_], firstRun: Boolean): Array[Method] = {
			val superClazz = clazz.getSuperclass
			val res = superClazz match {
				case null => clazz.getDeclaredMethods
				case _    => concat(clazz.getDeclaredMethods, findAllMethodsRecursively(superClazz, false))
			}

			if (firstRun) res
			else res.filter(m => ! Modifier.isPrivate(m.getModifiers))
		}

		try {
			val cls = conf.classLoader.loadClass(FQN)
			findAllMethodsRecursively(cls, true)
		} catch {
			case e: ClassNotFoundException => throw new ParseException("Class '" + name + "' (" + FQN + ") was not found in generated JAR even though it was found by ANTLR", e)
		}
	}

	/** Find all fields via reflection (only for field allFields)
	  * @param name String name of class
	  * @throws ParseException If class was found by ANTLR but not by reflection
	  * @throws SecurityException From Class.getDeclaredFields
	  * @return Array of Fields
	  */
	protected def findAllFields: Array[Field] = {
		/** Recursively build array of class fields
		  * @param clazz fields of this class are returned
		  * @param firstRun If set to True, private Fields will be included (but not parents ones)
		  * @return Array of Fields
		  */
		def findAllFieldsRecursively(clazz: Class[_], firstRun: Boolean): Array[Field] = {
			val superClazz = clazz.getSuperclass
			val res = superClazz match {
				case null => clazz.getDeclaredFields
				case _    => concat(clazz.getDeclaredFields, findAllFieldsRecursively(superClazz, false))
			}

			if (firstRun) res
			else res.filter(m => ! Modifier.isPrivate(m.getModifiers))
		}

		try {
			val cls = conf.classLoader.loadClass(FQN)
			findAllFieldsRecursively(cls, true)
		} catch {
			case e: ClassNotFoundException => throw new ParseException("Class '" + name + "' (" + FQN + ") was not found in generated JAR even though it was found by ANTLR", e)
		}
	}
}


// TODO: constructor
/** Method representation */
// class OMPMethod(ctx: Java8Parser.MethodDeclarationContext, parser: Java8Parser)(implicit conf: Config) {
// 	// val tree: OMPTree
// 	// val variables: List[OMPVariable] // TODO

// 	override def toString = ctx.toStringTree(parser)
// }

// class OMPTree(implicit conf: Config) {}

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

/** Variable representation */
class OMPVariable(_name: String, _varType: String, _meaning: OMPVariableType = OMPVariableType.Class) {
	lazy val name = _name
	lazy val varType = _varType
	lazy val meaning = _meaning

	override def toString = s"Variable '$name' of type '$varType' with meaning of '$meaning'"
}
