package org.omp4j.tree

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import scala.collection.JavaConverters._

import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.extractor._
import org.omp4j.grammar._

trait Nonreflectable extends ClassTrait {

	override lazy val FQN: String = s"[LOCAL] $name"

	val innerClasses: List[OMPClass] = (new InnerClassExtractor ).visit(ctx.normalClassDeclaration.classBody).map(new InnerInLocalClass(_, THIS, parser)(conf, classMap))

	/** Find all fields via reflection (only for field allFields)
	  * @param name String name of class
	  * @throws ParseException If class was found by ANTLR but not by reflection
	  * @throws SecurityException From Class.getDeclaredFields
	  * @return Array of Fields
	  */
	def findAllFields: Array[OMPVariable] = {

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
			case e: Exception => ; //println("err")	// TODO
		}

		// val ifArray: Array[OMPVariable] = inheritedFields.toArray.filter(! _.isPrivate)
		// println("return:\t" + (res ++ ifArray).size)
		res ++ inheritedFields.filter(! _.isPrivate)
	}

}
