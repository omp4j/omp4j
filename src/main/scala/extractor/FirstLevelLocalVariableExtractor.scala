package org.omp4j.extractor

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.preprocessor.grammar._
import org.omp4j.preprocessor._

/** Extracts all local variable declarations from ANTLR4 ParseTree with no statement pass */
class FirstLevelLocalVariableExtractor extends Java8BaseVisitor[Set[OMPVariable]] {

	/** Add local variable declaration context */
	override def visitLocalVariableDeclaration(variableCtx: Java8Parser.LocalVariableDeclarationContext) = Set[OMPVariable](new OMPVariable(variableCtx.variableDeclarators().variableDeclarator(0).variableDeclaratorId().getText(), variableCtx.`type`().getText()))

	/** Don't get into nested statements */
	override def visitStatement(ctx: Java8Parser.StatementContext) = Set[OMPVariable]()

	/** Don't get into other methods */
	override def visitMethodDeclaration(ctx: Java8Parser.MethodDeclarationContext) = Set[OMPVariable]()
	override def defaultResult() = Set[OMPVariable]()
	override def aggregateResult(a: Set[OMPVariable], b: Set[OMPVariable]) = a ++ b
}
