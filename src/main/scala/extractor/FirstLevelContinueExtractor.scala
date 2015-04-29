package org.omp4j.extractor

import org.omp4j.grammar._

/** Extracts first-level classes from ANTLR4 ParseTree */
class FirstLevelContinueExtractor extends Java8BaseVisitor[List[Java8Parser.ContinueStatementContext]] {

	/** Java8Parser.ClassDeclarationContext typedef */
	type BSC = Java8Parser.ContinueStatementContext

	// Ignore nested cycles
	override def visitWhileStatement(wsc: Java8Parser.WhileStatementContext) = List[BSC]()
	override def visitForStatement(fsc: Java8Parser.ForStatementContext) = List[BSC]()

	// Do not continue, so no nested classes included
	override def visitContinueStatement(continueCtx: BSC) = List[BSC](continueCtx)
	override def defaultResult = List[BSC]()
	override def aggregateResult(a: List[BSC], b: List[BSC]) = a ::: b
}
