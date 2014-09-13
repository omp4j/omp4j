package org.omp4j.tree

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import scala.collection.mutable.Stack
import scala.collection.mutable.Map

import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.extractor._
import org.omp4j.grammar._

object OMPFile {
	type ClassMap = Map[ParserRuleContext, OMPClass]
}

/** File representation containing list of classes */
class OMPFile(ctx: Java8Parser.CompilationUnitContext, parser: Java8Parser)(implicit conf: Config) extends OMPBase(ctx, parser) {

	/** (ctx -> OMPClass) mapping*/
	implicit val classMap = Map[ParserRuleContext, OMPClass]()

	/** Classes in file */
	val classes = (new ClassExtractor ).visit(ctx).map(new TopClass(_, null, parser))

	/** Get class by name */
	def getClass(name: String): OMPClass = {
		val filtered = classes.filter(_.name == name)
		filtered.size match {
			case 0 => throw new IllegalArgumentException("Class '" + name + "' not found (1)")
			case 1 => filtered.head
			case _ => throw new IllegalArgumentException("Class '" + name + "'  found multiple times")
		} 
	}

	def getClass(names: Stack[StackClass]): OMPClass = {

		def getClassRec(namesRev: List[String], clazz: OMPClass): OMPClass = {
			namesRev.size match {
				case 1 => clazz.getNestedClass(namesRev.head)
				case _ => getClassRec(namesRev.tail, clazz.getNestedClass(namesRev.head))
			} 
		}

		val revNames = names.reverse.toList.filter(! _.isLocal)
		revNames.size match {
			case 0 => throw new IllegalArgumentException("Empty stack passed")
			case 1 => getClass(revNames.head.name)
			case _ => getClassRec(revNames.map(_.name).tail, getClass(revNames.head.name))
		} 
	}
}
