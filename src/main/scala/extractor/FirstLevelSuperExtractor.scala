package org.omp4j.extractor

import org.omp4j.grammar._

/** Extracts first-level supers from ANTLR4 ParseTree */
class FirstLevelSuperExtractor extends Java8BaseVisitor[List[Java8Parser.SuperRuleContext]] {

	/** Java8Parser.SuperRuleContext typedef */
	type SRC = Java8Parser.SuperRuleContext

	// Ignore nested classes
	override def visitClassInstanceCreationExpression(wsc: Java8Parser.ClassInstanceCreationExpressionContext) = List[SRC]()
	override def visitClassInstanceCreationExpression_lf_primary(wsc: Java8Parser.ClassInstanceCreationExpression_lf_primaryContext) = List[SRC]()
	override def visitClassInstanceCreationExpression_lfno_primary(wsc: Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext) = List[SRC]()

	override def visitSuperRule(s: Java8Parser.SuperRuleContext) = List[SRC](s)

	override def defaultResult = List[SRC]()
	override def aggregateResult(a: List[SRC], b: List[SRC]) = a ::: b

}
