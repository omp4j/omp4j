package org.omp4j.tree

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import scala.collection.JavaConverters._

import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.extractor._
import org.omp4j.grammar._

/** The nonreflextable class representation. This class can't be reflected. */
trait Nonreflectable extends ClassTrait {

	/** Placeholder for debug purposes */
	override lazy val FQN: String = s"[LOCAL] $name"

	/** Inner classes of type InnerInLocalClass */
	val innerClasses: List[OMPClass] = (new InnerClassExtractor ).visit(classBody).map(new InnerInLocalClass(_, THIS, parser)(conf, ompFile))

	def findFieldsSyntactically: Array[OMPVariable] = {
		var res = Array[OMPVariable]()
		var inheritedFields = Array[OMPVariable]()

		classBody.classBodyDeclaration.asScala
			.filter(d => d.classMemberDeclaration != null)
			.map(_.classMemberDeclaration)
			.filter(f => f.fieldDeclaration != null)
			.map(_.fieldDeclaration)	// fields
			.map(f =>
				f.variableDeclaratorList.variableDeclarator.asScala
				 .map(g => new OMPVariable(g.variableDeclaratorId.Identifier.getText, f.unannType.getText, OMPVariableType.Field, ! f.fieldModifier.asScala.exists(m => m.getText == "public" || m.getText == "protected")))
			)
			.flatten
			.toArray
	}

	def findInheritedFields(superName: String) = {
		/*
		  - using variable
		  - implemented classes (local, other)
		  - reflection
		*/

		def getCandidates(objs: List[Findable], chunks: Array[String]): List[OMPClass] = {
			objs.size match {
				case 0 => List()
				case _ => 
					try {
						objs.head.findClass(chunks) :: getCandidates(objs.tail, chunks)
					} catch {
						case e: IllegalArgumentException => getCandidates(objs.tail, chunks)
					}
			}
		}

//		val superName = ctx.normalClassDeclaration.superclass.classType.getText
		val chunks = superName.split("\\.")
		val visibleLocalClasses = ctx match {
			case null => Inheritor.getVisibleLocalClasses(classBody, ompFile)
			case _    => Inheritor.getVisibleLocalClasses(ctx, ompFile)
		}
		val visibleNonLocalClasses = ctx match {
			case null => Inheritor.getVisibleNonLocalClasses(classBody, ompFile)
			case _    => Inheritor.getVisibleNonLocalClasses(ctx, ompFile)
		}

		val filteredLocalClasses = visibleLocalClasses.filter(_.name == chunks.head)
		val filteredNonLocalClasses = visibleNonLocalClasses.filter(_.name == chunks.head)
		val candidates = getCandidates(filteredLocalClasses ::: filteredNonLocalClasses, chunks.tail)

		candidates.size match {
			// is not local
			case 0 =>
				try {
					val cls = conf.loader.load(superName, cunit)
					findAllFieldsRecursively(cls, false)
				} catch {
					case e: Exception => throw new ParseException(s"Class '$name' ($FQN) was not found in generated JAR even though it was found by ANTLR", e)
				}

			// is local, take the first one
			case _ => candidates.head.allFields.toArray
		}
		
	}

	/** Find all fields syntactically (use only for allFields initialization)
	  * @return Array of OMPVariable
	  */
	def findAllFields: Array[OMPVariable] = {

		var res = Array[OMPVariable]()
		var inheritedFields = Array[OMPVariable]()
		try {
			res = findFieldsSyntactically

			try {	// try to load superclass
				val superName = ctx.normalClassDeclaration.superclass.classType.getText
				inheritedFields = findInheritedFields(superName)
			} catch {
				case e: NullPointerException => ;	// no superclass, it's ok to be as other exceptions pass
			}

		} catch {
			case e: Exception => throw new ParseException(s"Unexpected exception during finding all fields in '$FQN'", e)
		}

		// return result
		res ++ inheritedFields.filter(! _.isPrivate)
	}
}
