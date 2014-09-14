package org.omp4j.tree

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import scala.collection.JavaConverters._

import org.omp4j.Config
import org.omp4j.extractor._
import org.omp4j.grammar._

/** The abstract class representation */
abstract class OMPClass(ctx: Java8Parser.ClassDeclarationContext, parent: OMPClass, parser: Java8Parser)(implicit val conf: Config, val classMap: OMPFile.ClassMap) extends OMPBase(ctx, parser) {

	// register itself in classMap
	classMap += (ctx -> this)

	/** Accessing this in traits */
	lazy final val THIS: OMPClass = this

	/** String class name */
	lazy val name: String = ctx.normalClassDeclaration.Identifier.getText

	/** Fully Qualified Name */
	val FQN: String

	/** Compilation unit (root of parsetree) */
	lazy val cunit: Java8Parser.CompilationUnitContext = parent.cunit

	/** Get package prefix for FQN */
	def packageNamePrefix(pt: ParserRuleContext = ctx): String = {
		try {
			cunit.packageDeclaration.Identifier.asScala.map(_.getText).mkString(".") + "."
		} catch {
			case e: NullPointerException => ""
		}
	}

	/** List of nested classes */
	val innerClasses: List[OMPClass]	// TODO: what if enum?
	
	/** List of local classes (first level only) */
	val localClasses: List[OMPClass]

	/** Set of declared and inherited fields */
	lazy val fields: Set[OMPVariable] = findAllFields.toSet

	/** Set of all fields referable from this class context */
	lazy val allFields: Set[OMPVariable] = parent match {
		case null => fields
		case _    => fields ++ parent.allFields
	}

	/** Find all fields via reflection (only for field allFields)
	  * @param name String name of class
	  * @throws ParseException If class was found by ANTLR but not by reflection
	  * @throws SecurityException From Class.getDeclaredFields
	  * @return Array of Fields
	  */
	protected def findAllFields: Array[OMPVariable]
}
