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
import scala.collection.JavaConverters._
import scala.collection.mutable.Map

import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.extractor._
import org.omp4j.preprocessor.grammar._


case class StackClass (ctx: Java8Parser.ClassDeclarationContext)(implicit conf: Config) {
	lazy val name = ctx.normalClassDeclaration.Identifier.getText
	lazy val isLocal = if (ctx.getParent.isInstanceOf[Java8Parser.ClassMemberDeclarationContext] || ctx.getParent.getParent.isInstanceOf[Java8Parser.CompilationUnitContext]) false else true
	// println(s"-> $name ... $isLocal")
}

abstract class OMPBase(ctx: ParserRuleContext, parser: Java8Parser)(implicit conf: Config) {
	override def toString = ctx.toStringTree(parser)
}

/** File representation containing list of classes */
class OMPFile(ctx: Java8Parser.CompilationUnitContext, parser: Java8Parser)(implicit conf: Config) extends OMPBase(ctx, parser) {

	/** (ctx -> OMPClass) mapping*/
	implicit val classMap = Map[ParserRuleContext, OMPClass]()

	/** Classes in file */
	val classes = (new ClassExtractor ).visit(ctx).map(new OMPClass(_, null, parser))

	/** Get class by name */
	def getClass(name: String): OMPClass = {
		val filtered = classes.filter(_.name == name)
		filtered.size match {
			case 0 => throw new IllegalArgumentException("Class '" + name + "' not found (1)")
			case 1 => filtered.head
			case _ => throw new IllegalArgumentException("Class '" + name + "'  found multiple times")
		} 
	}

	def getClass(names: Stack[StackClass]): OMPClass = {

		def getClassRec(namesRev: List[String], clazz: OMPClass): OMPClass = {
			namesRev.size match {
				case 1 => clazz.getNestedClass(namesRev.head)
				case _ => getClassRec(namesRev.tail, clazz.getNestedClass(namesRev.head))
			} 
		}

		val revNames = names.reverse.toList.filter(! _.isLocal)
		revNames.size match {
			case 0 => throw new IllegalArgumentException("Empty stack passed")
			case 1 => getClass(revNames.head.name)
			case _ => getClassRec(revNames.map(_.name).tail, getClass(revNames.head.name))
		} 
	}
}

/** Class representation containing list of methods, fields and nested classes */
class OMPClass(ctx: Java8Parser.ClassDeclarationContext, parent: OMPClass, parser: Java8Parser)(implicit conf: Config, classMap: Map[ParserRuleContext, OMPClass]) extends OMPBase(ctx, parser) {

	// register itself in classMap
	classMap += (ctx -> this)

	/** String class name */
	lazy val name: String = ctx.normalClassDeclaration.Identifier.getText

	lazy val FQN: String = parent match {	// TODO package?
		case null => packageNamePrefix() + name
		case _    => parent.FQN + "$" + name
	}

	protected def cunit(pt: ParserRuleContext = ctx): Java8Parser.CompilationUnitContext = {
		try {
			val cunit = pt.asInstanceOf[Java8Parser.CompilationUnitContext]
			cunit
		} catch {
			case e: Exception => cunit(pt.getParent)
		}
	}

	def packageNamePrefix(pt: ParserRuleContext = ctx): String = {
		try {
			val compUnit: Java8Parser.CompilationUnitContext = cunit()
			compUnit.packageDeclaration.Identifier.asScala.map(_.getText).mkString(".") + "."
		} catch {
			case e: NullPointerException => ""
		}
	}

	/** List of nested classes */
	// TODO: what if enum?
	val nestedClasses = (new ClassExtractor ).visit(ctx.normalClassDeclaration.classBody).map(new OMPClass(_, this, parser))

	val localClasses = ctx.normalClassDeclaration.classBody.classBodyDeclaration.asScala
		.filter(d => d.classMemberDeclaration != null)
		.map(_.classMemberDeclaration)
		.filter(m => m.methodDeclaration != null)
		.map(_.methodDeclaration)	// methods
		.map((new ClassExtractor ).visit(_))
		.flatten
		.map(new OMPLocalClass(_, this, parser))

	/** List of methods directly implemented (or overriden) in the class */
	// lazy val implementedMethods = (new MethodExtractor ).visit(ctx.classBody).map(c => new OMPMethod(c, parser))

	/** List of all methods (including inherited ones) */
	// lazy val allMethods: Array[Method] = findAllMethods

	/** List of all fields directly implemented in the class */
	// lazy val implementedFields = (new FieldExtractor ).visit(ctx.classBody).map(c => new OMPVariable(c.`type`, c.variableDeclarators, parser))

	/** Set of declared and inherited fields */
	lazy val fields: Set[OMPVariable] = findAllFields.toSet

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
	// protected def findAllMethods: Array[Method] = {
	// 	/** Recursively build array of class methods
	// 	  * @param clazz Methods of this class are returned
	// 	  * @param firstRun If set to True, private methods will be included (but not parents ones)
	// 	  * @return Array of Methods
	// 	  */
	// 	def findAllMethodsRecursively(clazz: Class[_], firstRun: Boolean): Array[Method] = {
	// 		val superClazz = clazz.getSuperclass
	// 		val res = superClazz match {
	// 			case null => clazz.getDeclaredMethods
	// 			case _    => concat(clazz.getDeclaredMethods, findAllMethodsRecursively(superClazz, false))
	// 		}

	// 		if (firstRun) res
	// 		else res.filter(m => ! Modifier.isPrivate(m.getModifiers))
	// 	}

	// 	try {
	// 		val cls = conf.loader.classLoader.loadClass(FQN)
	// 		findAllMethodsRecursively(cls, true)
	// 	} catch {
	// 		case e: ClassNotFoundException => throw new ParseException("Class '" + name + "' (" + FQN + ") was not found in generated JAR even though it was found by ANTLR", e)
	// 	}
	// }

	/** Find all fields via reflection (only for field allFields)
	  * @param name String name of class
	  * @throws ParseException If class was found by ANTLR but not by reflection
	  * @throws SecurityException From Class.getDeclaredFields
	  * @return Array of Fields
	  */
	protected def findAllFields: Array[OMPVariable] = {
		try {
			val cls = conf.loader.loadByFQN(FQN)
			findAllFieldsRecursively(cls, true)
		} catch {
			case e: ClassNotFoundException => throw new ParseException("Class '" + name + "' (" + FQN + ") was not found in generated JAR even though it was found by ANTLR", e)
		}
	}

	/** Recursively build array of class fields
	  * @param clazz fields of this class are returned
	  * @param firstRun If set to True, private Fields will be included (but not parents ones)
	  * @return Array of Fields
	  */
	protected def findAllFieldsRecursively(clazz: Class[_], firstRun: Boolean): Array[OMPVariable] = {
		val superClazz = clazz.getSuperclass
		superClazz match {
			case null =>
				if (firstRun) clazz.getDeclaredFields.map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Class, Modifier.isPrivate(f.getModifiers)))
				else clazz.getDeclaredFields.filter(m => ! Modifier.isPrivate(m.getModifiers)).map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Class, false))
			case _    =>
				if (firstRun) concat(clazz.getDeclaredFields.map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Class, Modifier.isPrivate(f.getModifiers))), findAllFieldsRecursively(superClazz, false))
				else concat(clazz.getDeclaredFields.filter(m => ! Modifier.isPrivate(m.getModifiers)).map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Class, false)), findAllFieldsRecursively(superClazz, false))
		}

		// if (firstRun) res
		// else res.filter(m => ! Modifier.isPrivate(m.getModifiers))
	}

}

class OMPLocalClass(ctx: Java8Parser.ClassDeclarationContext, parent: OMPClass, parser: Java8Parser)(implicit conf: Config, classMap: Map[ParserRuleContext, OMPClass]) extends OMPClass(ctx, parent, parser) {

	// register itself in classMap
	// classMap += (ctx -> this)

	override lazy val FQN = name

	// TODO: use syntax!
	override protected def findAllFields: Array[OMPVariable] = {

		var res = Array[OMPVariable]()
		var inheritedFields = Array[OMPVariable]()
		try {
			// ctx.normalClassDeclaration.superclass
			res = ctx.normalClassDeclaration.classBody.classBodyDeclaration.asScala
				.filter(d => d.classMemberDeclaration != null)
				.map(_.classMemberDeclaration)
				.filter(f => f.fieldDeclaration != null)
				.map(_.fieldDeclaration)	// fields
				.map(f =>
					f.variableDeclaratorList.variableDeclarator.asScala
					 .map(g => new OMPVariable(g.variableDeclaratorId.Identifier.getText, f.unannType.getText, OMPVariableType.Class, ! f.fieldModifier.asScala.exists(m => m.getText == "public" || m.getText == "protected")))
				)
				.flatten
				.toArray


			try {
				val superName = ctx.normalClassDeclaration.superclass.classType.getText
				val visibleLocalClasses = Inheritor.getVisibleLocalClasses(ctx)
				val filteredClasses = visibleLocalClasses.filter(_.normalClassDeclaration != null).filter(_.normalClassDeclaration.Identifier.getText == superName)

				inheritedFields = filteredClasses.size match {
					case 0 =>
						try {
							// println(s"loading $superName")
							val cls = conf.loader.loadByFQN(superName)
							// println("loaded")
							findAllFieldsRecursively(cls, false)
							// (new OMPClass(filteredClasses.head, null, parser)).allFields
						} catch {
							case e: Exception => throw new ParseException("Class '" + name + "' (" + FQN + ") was not found in generated JAR even though it was found by ANTLR", e)
						}


					case _ => classMap.get(filteredClasses.head) match {
						case Some(x) => x.allFields.toArray
						case None    => throw new ParseException(s"Local class '$superName' not cached in OMPTree")
					}
					// case _ => (new OMPLocalClass(filteredClasses.head, null, parser)).allFields.toArray
				}
				// println("===> " + inheritedFields.size)

			} catch {
				case e: NullPointerException => ;
			}

		} catch {
			case e: Exception => println("err")	// TODO
		}

		// val ifArray: Array[OMPVariable] = inheritedFields.toArray.filter(! _.isPrivate)
		// println("return:\t" + (res ++ ifArray).size)
		res ++ inheritedFields.filter(! _.isPrivate)
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
case class OMPVariable(name: String, varType: String, meaning: OMPVariableType = OMPVariableType.Class, isPrivate: Boolean = false) {
	// lazy val name = _name
	// lazy val varType = _varType
	// lazy val meaning = _meaning

	override def toString = s"Variable '$name' of type '$varType' with meaning of '$meaning'"
}
