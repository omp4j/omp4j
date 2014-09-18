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
	val innerClasses: List[OMPClass] = (new InnerClassExtractor ).visit(ctx.normalClassDeclaration.classBody).map(new InnerInLocalClass(_, THIS, parser)(conf, ompFile))

	/** Find all fields syntactically (use only for allFields initialization)
	  * @return Array of OMPVariable
	  */
	def findAllFields: Array[OMPVariable] = {

		var res = Array[OMPVariable]()
		var inheritedFields = Array[OMPVariable]()
		try {
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

			try {	// try to load superclass
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

				val superName = ctx.normalClassDeclaration.superclass.classType.getText
				val chunks = superName.split("\\.")
				val visibleLocalClasses = Inheritor.getVisibleLocalClasses(ctx, ompFile)
				val visibleNonLocalClasses = Inheritor.getVisibleNonLocalClasses(ctx, ompFile)

				val filteredLocalClasses = visibleLocalClasses.filter(_.name == chunks.head)
				val filteredNonLocalClasses = visibleNonLocalClasses.filter(_.name == chunks.head)
				val candidates = getCandidates(filteredLocalClasses ::: filteredNonLocalClasses, chunks.tail)
				// val filteredClasses = visibleLocalClasses.filter(_.name == superName)

				inheritedFields = candidates.size match {
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
			} catch {
				case e: NullPointerException => ;	// no superclass, it's ok to be as other exceptions pass
			}

		} catch {
			case e: Exception => throw new ParseException(s"Unexpected exception during finding all fields in '$FQN'", e)
		}

		res ++ inheritedFields.filter(! _.isPrivate)
	}
}
