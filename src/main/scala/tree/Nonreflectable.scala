package org.omp4j.tree

import org.omp4j.exception._
import org.omp4j.extractor._

import scala.collection.JavaConverters._

/** The nonreflectable trait represents classes that can't be reflected. */
trait Nonreflectable extends ClassTrait {

	/** Placeholder for debug purposes */
	override lazy val FQN: String = s"[LOCAL] $name"

	/** Inner classes of type InnerInLocalClass */
	val innerClasses: List[OMPClass] = (new InnerClassExtractor ).visit(classBody).map(new InnerInLocalClass(_, THIS, parser)(conf, ompFile))

	/** Apply syntax analysis */
	def findFieldsSyntactically: Array[OMPVariable] = {
		var res = Array[OMPVariable]()
		var inheritedFields = Array[OMPVariable]()

		classBody.classBodyDeclaration.asScala
			.filter(d => d.classMemberDeclaration != null)
			.map(_.classMemberDeclaration)
			.filter(f => f.fieldDeclaration != null)
			.map(_.fieldDeclaration)	// fields
			.filter(f => f.fieldModifier == null || ! f.fieldModifier.asScala.map(_.getText).contains("final"))
			.map(f =>
				f.variableDeclaratorList.variableDeclarator.asScala
					.map(g => new OMPVariable(g.variableDeclaratorId.Identifier.getText, f.unannType.getText, OMPVariableType.Field, ! f.fieldModifier.asScala.exists(m => m.getText == "public" || m.getText == "protected")))
			)
			.flatten
			.toArray
	}

	/** Fetch all inherited fields.
	  *
	  * Firstly by using variable, secondly seeking implemented classes (local, other) and finally via reflection API.
	 * @param superName superclass name
	 * @return array of fields
	 */
	def findInheritedFields(superName: String) = {

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
					findAllFieldsRecursively(cls, firstRun = false)
				} catch {
					case e: Exception => throw new ParseException(s"Class '$name' ($FQN) was not found in generated JAR even though it was found by ANTLR", e)
				}

			// is local, take the first one
			case _ => candidates.head.allFields.toArray
		}
		
	}

	/** Find all fields syntactically (use only for allFields initialization)
	  *
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
				case e: NullPointerException => ;	// no superclass, it's ok to be here like this since other exceptions pass
			}

		} catch {
			case e: Exception => throw new ParseException(s"Unexpected exception during finding all fields in '$FQN'", e)
		}

		// return result
		res ++ inheritedFields.filter(! _.isPrivate)
	}
}
