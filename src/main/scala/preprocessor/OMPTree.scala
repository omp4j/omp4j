package org.omp4j.preprocessor

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import java.lang.reflect.Method
import java.lang.reflect.Field
import java.lang.reflect.Modifier

import scala.language.existentials
import scala.collection.mutable.ListBuffer
import Array._

import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.extractor._
import org.omp4j.preprocessor.grammar._

// TODO: pass `this`

abstract class OMPBase(ctx: ParserRuleContext, parser: Java8Parser)(implicit conf: Config) {
	override def toString() = ctx.toStringTree(parser)
}

/** File representation containing list of classes */
class OMPFile(ctx: Java8Parser.CompilationUnitContext, parser: Java8Parser)(implicit conf: Config) extends OMPBase(ctx, parser) {

	/** Classes in file */
	lazy val classes = (new ClassExtractor ).visit(ctx).map(c => new OMPClass(c, parser))
}

/** Class representation containing list of methods, fields and nested classes */
class OMPClass(ctx: Java8Parser.ClassDeclarationContext, parser: Java8Parser)(implicit conf: Config) extends OMPBase(ctx, parser) {
	/** String class name */
	lazy val name = ctx.Identifier().getText()

	/** List of nested classes */
	lazy val nestedClasses = (new ClassExtractor ).visit(ctx.classBody()).map(c => new OMPClass(c, parser))

	/** List of methods directly implemented (or overriden) in the class */
	lazy val implementedMethods = (new MethodExtractor ).visit(ctx.classBody()).map(c => new OMPMethod(c, parser))

	/** List of all methods (including inherited ones) */
	lazy val allMethods: Array[Method] = findAllMethods

	/** List of all fields directly implemented in the class */
	lazy val implementedFields = (new FieldExtractor ).visit(ctx.classBody()).map(c => new OMPVariable(c.`type`(), c.variableDeclarators(), parser))

	/** List of all fields (including inherited ones) */
	lazy val allFields = findAllFields

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
			val superClazz = clazz.getSuperclass()
			val res = 
				if (superClazz == null) clazz.getDeclaredMethods()
				else concat(clazz.getDeclaredMethods(), findAllMethodsRecursively(superClazz, false))

			if (firstRun) res
			else res.filter(m => ! Modifier.isPrivate(m.getModifiers()))
		}

		try {
			val cls = conf.classLoader.loadClass(name)
			findAllMethodsRecursively(cls, true)
		} catch {
			case e: ClassNotFoundException => throw new ParseException("Class '" + name + "' was not found in generated JAR even though it was found by ANTLR", e)
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
			val superClazz = clazz.getSuperclass()
			val res = 
				if (superClazz == null) clazz.getDeclaredFields()
				else concat(clazz.getDeclaredFields(), findAllFieldsRecursively(superClazz, false))

			if (firstRun) res
			else res.filter(m => ! Modifier.isPrivate(m.getModifiers()))
		}

		try {
			val cls = conf.classLoader.loadClass(name)
			findAllFieldsRecursively(cls, true)
		} catch {
			case e: ClassNotFoundException => throw new ParseException("Class '" + name + "' was not found in generated JAR even though it was found by ANTLR", e)
		}
	}
}


// TODO: constructor
/** Method representation */
class OMPMethod(ctx: Java8Parser.MethodDeclarationContext, parser: Java8Parser)(implicit conf: Config) {
	// val tree: OMPTree
	// val variables: List[OMPVariable] // TODO

	override def toString() = ctx.toStringTree(parser)
}

class OMPTree(implicit conf: Config) {}

/** Variable representation */
class OMPVariable(varType: Java8Parser.TypeContext, ctx: Java8Parser.VariableDeclaratorsContext, parser: Java8Parser)(implicit conf: Config) {
}
