package org.omp4j.extractor

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.preprocessor.grammar._

/** Extracts all local variable declarations from ANTLR4 ParseTree */
class LocalVariableExtractor extends Java8BaseVisitor[Set[Java8Parser.LocalVariableDeclarationContext]] {

	/** Java8Parser.LocalVariableDeclarationContext typedef */
	type LVDC = Java8Parser.LocalVariableDeclarationContext

	/** Add local variable declaration context */
	override def visitLocalVariableDeclaration(variableCtx: LVDC) = Set[LVDC](variableCtx)

	/** Don't get into nested statements */
	// override def visitStatement(c: Java8Parser.StatementContext) = Set[LVDC]()
	// override def visitBlockStatement(c: Java8Parser.BlockStatementContext) = Set[LVDC]()

	override def defaultResult = Set[LVDC]()
	override def aggregateResult(a: Set[LVDC], b: Set[LVDC]) = a ++ b
}
