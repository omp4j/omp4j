package org.omp4j.tree

import org.omp4j.exception._
import org.omp4j.extractor._

/** The reflextable class representation. This class can be reflected. */
trait Reflectable extends ClassTrait {

	/** Get FQN using parent's name */
	override lazy val FQN: String = parent match {	// TODO package?
		case null => packageNamePrefix() + name
		case _    => parent.FQN + "$" + name
	}

	/** Inner classes of type InnerClass */
	val innerClasses: List[OMPClass] = (new InnerClassExtractor ).visit(classBody).map(new InnerClass(_, THIS, parser)(conf, ompFile))

	/** Find all fields using reflection (use only for allFields initialization)
	  * @throws ParseException If class was found by ANTLR but not by reflection
	  * @throws SecurityException From Class.getDeclaredFields
	  * @return Array of OMPVariable
	  */
	def findAllFields: Array[OMPVariable] = {
		try {
			val cls = conf.loader.loadByFQN(FQN)
			findAllFieldsRecursively(cls, true)
		} catch {
			case e: ClassNotFoundException => throw new ParseException(s"Class '$name' ($FQN) was not found in generated JAR (${conf.jar.getAbsolutePath}}) even though it was found by ANTLR", e)
		}
	}

}
