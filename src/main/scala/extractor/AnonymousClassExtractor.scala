package org.omp4j.extractor

import org.omp4j.grammar._

/** Extracts first-level classes from ANTLR4 ParseTree */
class AnonymousClassExtractor extends Java8BaseVisitor[List[Java8Parser.ClassBodyContext]] {

	/** Java8Parser.ClassBodyContext typedef */
	type CBC = Java8Parser.ClassBodyContext

	/** Do not continue, so no nested classes included */
	override def visitClassDeclaration(classCtx: Java8Parser.ClassDeclarationContext) = List[CBC]()
	override def visitClassInstanceCreationExpression(classCtx: Java8Parser.ClassInstanceCreationExpressionContext) = if (classCtx.classBody != null) List[CBC](classCtx.classBody) else List[CBC]()
	override def visitClassInstanceCreationExpression_lf_primary(classCtx: Java8Parser.ClassInstanceCreationExpression_lf_primaryContext) = if (classCtx.classBody != null) List[CBC](classCtx.classBody) else List[CBC]()
	override def visitClassInstanceCreationExpression_lfno_primary(classCtx: Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext) = if (classCtx.classBody != null) List[CBC](classCtx.classBody) else List[CBC]()
	override def defaultResult = List[CBC]()
	override def aggregateResult(a: List[CBC], b: List[CBC]) = a ::: b
}
