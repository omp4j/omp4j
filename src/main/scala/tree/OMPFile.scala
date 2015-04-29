package org.omp4j.tree

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree.ParseTree
import org.omp4j.Config
import org.omp4j.extractor._
import org.omp4j.grammar._

/** OMPFile companion object */
object OMPFile {

	/** Type alias for classMap*/
	type ClassMap = scala.collection.mutable.Map[ParseTree, OMPClass]
}

/** File representation containing list of classes
  *
  * This is basically the root of the class hierarchy model
  *
  * @constructor Recursively build whole model by searching for TopClasses
  * @param ctx compilation unit context
  * @param parser Java8 ANTLR parser
  * @param conf configuration context
  */
class OMPFile(ctx: Java8Parser.CompilationUnitContext, parser: Java8Parser)(implicit conf: Config) extends Findable {

	/** (ctx -> OMPClass) mapping */
	implicit val classMap = scala.collection.mutable.Map[ParseTree, OMPClass]()

	/** Classes in file */
	val classes = (new ClassExtractor ).visit(ctx).map(new TopClass(_, null, parser)(conf, this))

	/** Find class based on (almost) FQN subsequence
	  *
	  * @param chunks array of strings where to seek
	  * @return found class
	  * @throws IllegalArgumentException if class not found or empty chunks passed
	  */
	def findClass(chunks: Array[String]): OMPClass = {
		chunks match {
			case Array() => throw new IllegalArgumentException("(findClass) Empty array passed")
			case _       =>
				val filtered = classes.filter(_.name == chunks.head)
				filtered.size match {
					case 0 => throw new IllegalArgumentException("(findClass) Class not found")
					case _ => filtered.head.findClass(chunks.tail)
				}
		}
	}
}
