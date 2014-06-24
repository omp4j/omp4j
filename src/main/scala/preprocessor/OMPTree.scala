package org.omp4j.preprocessor

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import scala.collection.mutable.ListBuffer

import org.omp4j.preprocessor.grammar._

// TODO: pass `this`

/** File representation containing list of classes */
class OMPFile(ctx: Java8Parser.CompilationUnitContext, parser: Java8Parser) {

	/** Classes in file */
	val classes = (new ClassExtractor ).visit(ctx).map(c => new OMPClass(c, parser))
	override def toString() = ctx.toStringTree(parser)
}

/** Class representation containing list of methods, fields and nested classes */
class OMPClass(ctx: Java8Parser.ClassDeclarationContext, parser: Java8Parser) {
	val nestedClasses = (new ClassExtractor ).visit(ctx.classBody()).map(c => new OMPClass(c, parser))
	val methods = (new MethodExtractor ).visit(ctx.classBody()).map(c => new OMPMethod(c, parser))
	val fields = (new FieldExtractor ).visit(ctx.classBody()).map(c => new OMPVariable(c.`type`(), c.variableDeclarators(), parser))
	override def toString() = ctx.toStringTree(parser)

	// methods.foreach(m => println(m + "\n\n"))
	// nestedClasses.foreach(nc => println(nc + "\n\n"))
}


// TODO: constructor
/** Method representation */
class OMPMethod(ctx: Java8Parser.MethodDeclarationContext, parser: Java8Parser) {
	// val tree: OMPTree
	// val variables: List[OMPVariable] // TODO

	override def toString() = ctx.toStringTree(parser)
}

class OMPTree() {}

/** Variable representation */
class OMPVariable(varType: Java8Parser.TypeContext, ctx: Java8Parser.VariableDeclaratorsContext, parser: Java8Parser) {
}
