package org.omp4j.tree

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.extractor._
import org.omp4j.grammar._

trait Reflectable extends ClassTrait {

	override lazy val FQN: String = parent match {	// TODO package?
		case null => packageNamePrefix() + name
		case _    => parent.FQN + "$" + name
	}

	val innerClasses: List[OMPClass] = (new ClassExtractor ).visit(ctx.normalClassDeclaration.classBody).map(new InnerClass(_, THIS, parser)(conf, classMap))


	/** Find all fields via reflection (only for field allFields)
	  * @param name String name of class
	  * @throws ParseException If class was found by ANTLR but not by reflection
	  * @throws SecurityException From Class.getDeclaredFields
	  * @return Array of Fields
	  */
	def findAllFields: Array[OMPVariable] = {
		try {
			val cls = conf.loader.loadByFQN(FQN)
			findAllFieldsRecursively(cls, true)
		} catch {
			case e: ClassNotFoundException => throw new ParseException("Class '" + name + "' (" + FQN + ") was not found in generated JAR even though it was found by ANTLR", e)
		}
	}

}
