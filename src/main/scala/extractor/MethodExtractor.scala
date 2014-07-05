package org.omp4j.extractor

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.preprocessor.grammar._

/** Extract class methods from ANTLR4 ParseTree, stop at any ClassDeclaration */
class MethodExtractor extends Java8BaseVisitor[List[Java8Parser.MethodDeclarationContext]] {

	/** Java8Parser.MethodDeclarationContext typedef */
	type MDC = Java8Parser.MethodDeclarationContext

	/** Do not continue, so no nested classes included */
	override def visitClassDeclaration(classCtx: Java8Parser.ClassDeclarationContext) = List[MDC]()

	/** Add method context */
	override def visitMethodDeclaration(methodCtx: MDC) = List[MDC](methodCtx)
	override def defaultResult() = List[MDC]()
	override def aggregateResult(a: List[MDC], b: List[MDC]) = a ::: b
}
