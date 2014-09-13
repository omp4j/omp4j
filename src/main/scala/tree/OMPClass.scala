package org.omp4j.tree

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import scala.collection.JavaConverters._

import org.omp4j.Config
import org.omp4j.extractor._
import org.omp4j.grammar._

// TODO: package doc

abstract class OMPClass(ctx: Java8Parser.ClassDeclarationContext, parent: OMPClass, parser: Java8Parser)(implicit val conf: Config, val classMap: OMPFile.ClassMap) extends OMPBase(ctx, parser) {

	lazy final val THIS: OMPClass = this

	// register itself in classMap
	classMap += (ctx -> this)

	/** String class name */
	lazy val name: String = ctx.normalClassDeclaration.Identifier.getText

	/** Fully Qualified Name */
	val FQN: String

	/** Compilation unit (root of parsetree) */
	lazy val cunit: Java8Parser.CompilationUnitContext = parent.cunit

	def packageNamePrefix(pt: ParserRuleContext = ctx): String = {
		try {
			// val compUnit: Java8Parser.CompilationUnitContext = cunit()
			cunit.packageDeclaration.Identifier.asScala.map(_.getText).mkString(".") + "."
		} catch {
			case e: NullPointerException => ""
		}
	}

	/** List of nested classes */
	// TODO: what if enum?
	val innerClasses: List[OMPClass]

	val localClasses: List[OMPClass] = ctx.normalClassDeclaration.classBody.classBodyDeclaration.asScala
		.filter(d => d.classMemberDeclaration != null)
		.map(_.classMemberDeclaration)
		.filter(m => m.methodDeclaration != null)
		.map(_.methodDeclaration)	// methods
		.map((new ClassExtractor ).visit(_))
		.flatten
		.map(new LocalClass(_, this, parser))
		.toList

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
		val filtered = innerClasses.filter(_.name == name)
		filtered.size match {
			case 0 => throw new IllegalArgumentException("Class '" + name + "' not found (2)")
			case 1 => filtered.head
			case _ => throw new IllegalArgumentException("Class '" + name + "'  found multiple times")
		} 
	}

	/** Find all fields via reflection (only for field allFields)
	  * @param name String name of class
	  * @throws ParseException If class was found by ANTLR but not by reflection
	  * @throws SecurityException From Class.getDeclaredFields
	  * @return Array of Fields
	  */
	protected def findAllFields: Array[OMPVariable]
	// protected def findAllFields: Array[OMPVariable] = {
	// 	try {
	// 		val cls = conf.loader.loadByFQN(FQN)
	// 		findAllFieldsRecursively(cls, true)
	// 	} catch {
	// 		case e: ClassNotFoundException => throw new ParseException("Class '" + name + "' (" + FQN + ") was not found in generated JAR even though it was found by ANTLR", e)
	// 	}
	// }

	/** Recursively build array of class fields
	  * @param clazz fields of this class are returned
	  * @param firstRun If set to True, private Fields will be included (but not parents ones)
	  * @return Array of Fields
	  */
	// protected def findAllFieldsRecursively(clazz: Class[_], firstRun: Boolean): Array[OMPVariable] = {
	// 	val superClazz = clazz.getSuperclass
	// 	superClazz match {
	// 		case null =>
	// 			if (firstRun) clazz.getDeclaredFields.map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Class, Modifier.isPrivate(f.getModifiers)))
	// 			else clazz.getDeclaredFields.filter(f => ! Modifier.isPrivate(f.getModifiers)).map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Class, false))
	// 		case _    =>
	// 			if (firstRun) concat(clazz.getDeclaredFields.map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Class, Modifier.isPrivate(f.getModifiers))), findAllFieldsRecursively(superClazz, false))
	// 			else concat(clazz.getDeclaredFields.filter(f => ! Modifier.isPrivate(f.getModifiers)).map(f => new OMPVariable(f.getName, f.getType.getName, OMPVariableType.Class, false)), findAllFieldsRecursively(superClazz, false))
	// 	}
	// }

}
