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
}
