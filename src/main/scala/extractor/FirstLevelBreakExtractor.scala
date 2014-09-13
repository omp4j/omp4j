package org.omp4j.extractor

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.grammar._

/** Extracts first-level classes from ANTLR4 ParseTree */
class FirstLevelBreakExtractor extends Java8BaseVisitor[List[Java8Parser.BreakStatementContext]] {

	/** Java8Parser.ClassDeclarationContext typedef */
	type BSC = Java8Parser.BreakStatementContext

	/** Ignore nested cycles */
	override def visitWhileStatement(wsc: Java8Parser.WhileStatementContext) = List[BSC]()
	override def visitForStatement(fsc: Java8Parser.ForStatementContext) = List[BSC]()

	/** Do not continue, so no nested classes included */
	override def visitBreakStatement(breakCtx: BSC) = List[BSC](breakCtx)
	override def defaultResult = List[BSC]()
	override def aggregateResult(a: List[BSC], b: List[BSC]) = a ::: b
}
