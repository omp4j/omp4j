package org.omp4j.tree

import org.antlr.v4.runtime._
import org.omp4j.Config
import org.omp4j.extractor._
import org.omp4j.exception._
import org.omp4j.grammar._

import scala.collection.JavaConverters._

/** Class model companion object */
object OMPClass {
	type EitherCtx = Either[Java8Parser.ClassDeclarationContext, Java8Parser.ClassBodyContext]
}

/** The abstract class representation
  *
  * @constructor register itself to class map
  * @param ec context either
  * @param parent parent class
  * @param parser Java8 ANTLR parser
  * @param conf configuration context
  * @param ompFile hierarchy model root
  */
abstract class OMPClass(ec: OMPClass.EitherCtx, parent: OMPClass, parser: Java8Parser)(implicit val conf: Config, val ompFile: OMPFile) extends Findable{

	/** classMap key */
	lazy val key: ParserRuleContext = ctx

	/* constructor */
	// register itself in classMap
	ompFile.classMap += (key -> this)
	/* /constructor */

	/** Accessing this in traits */
	lazy final val THIS: OMPClass = this

	/** Main context (may be null if AnonymousClass used) */
	lazy val ctx: Java8Parser.ClassDeclarationContext = ec match {
		case Left(x)  => x
		case Right(_) => null
	}
	
	/** Class body declaration (always non-null) */
	lazy val classBody: Java8Parser.ClassBodyContext = ec match {
		case Left(x)  => x.normalClassDeclaration.classBody
		case Right(y) => y
	}


	/** String class name */
	lazy val name: String = ctx.normalClassDeclaration.Identifier.getText

	/** Fully Qualified Name */
	val FQN: String

	/** Compilation unit (root of parsetree) */
	lazy val cunit: Java8Parser.CompilationUnitContext = parent.cunit

	/** Get package prefix for FQN
	  *
	  * @param pt code context where to seek
	  * @return package prefix
	  */
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

	/** List of anonymous classes (first level only) */
	val anonymousClasses: List[OMPClass] = (new AnonymousClassExtractor ).visit(classBody).map(ac => new AnonymousClass(ac, this, parser))

	/** Set of declared and inherited fields */
	lazy val fields: Set[OMPVariable] = findAllFields.toSet

	/** Set of all fields referable from this class context */
	lazy val allFields: Set[OMPVariable] = parent match {
		case null => fields
		case _    => fields ++ parent.allFields
	}

	/** Find all fields via reflection (only for field allFields)
	  *
	  * @throws ParseException If class was found by ANTLR but not by reflection
	  * @throws SecurityException From Class.getDeclaredFields
	  * @return Array of Fields
	  */
	protected def findAllFields: Array[OMPVariable]

	/** Find class based on (almost) FQN subsequence
	  *
	  * @param chunks array of strings where to seek
	  * @return found class
	  * @throws IllegalArgumentException if class not found
	  */
	def findClass(chunks: Array[String]): OMPClass = {
		chunks match {
			case Array() => this
			case _       =>
				val filtered = innerClasses.filter(_.name == chunks.head)
				filtered.size match {
					case 0 => throw new IllegalArgumentException("(findClass) Class not found")
					case _ => filtered.head.findClass(chunks.tail)
				}
		}
	}
}
