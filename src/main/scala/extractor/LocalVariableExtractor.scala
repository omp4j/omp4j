package org.omp4j.extractor

import org.omp4j.grammar._

/** Extracts all local variable declarations from ANTLR4 ParseTree */
class LocalVariableExtractor extends Java8BaseVisitor[Set[Java8Parser.LocalVariableDeclarationContext]] {

	/** Java8Parser.LocalVariableDeclarationContext typedef */
	type LVDC = Java8Parser.LocalVariableDeclarationContext

	/** Add local variable declaration context */
	override def visitLocalVariableDeclaration(variableCtx: LVDC) = Set[LVDC](variableCtx)

	override def defaultResult = Set[LVDC]()
	override def aggregateResult(a: Set[LVDC], b: Set[LVDC]) = a ++ b
}
