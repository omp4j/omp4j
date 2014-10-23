package org.omp4j.tree

import org.antlr.v4.runtime._
import org.omp4j.Config
import org.omp4j.extractor._
import org.omp4j.grammar._

import scala.collection.mutable.Map

object OMPFile {
	/** Type alias for classMap*/
	type ClassMap = Map[ParserRuleContext, OMPClass]
}

/** File representation containing list of classes */
class OMPFile(ctx: Java8Parser.CompilationUnitContext, parser: Java8Parser)(implicit conf: Config) extends Findable {

	/** (ctx -> OMPClass) mapping*/
	implicit val classMap = Map[ParserRuleContext, OMPClass]()

	/** Classes in file */
	val classes = (new ClassExtractor ).visit(ctx).map(new TopClass(_, null, parser)(conf, this))

	/** Find class based on (almost) FQN subsequence */
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
