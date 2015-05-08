package org.omp4j.extractor

import org.omp4j.grammar.Java8BaseVisitor
import org.omp4j.grammar.Java8Parser
import org.omp4j.grammar._

/** Extracts first-level classes from ANTLR4 ParseTree */
class FirstLevelReturnStatement extends Java8BaseVisitor[List[Java8Parser.ReturnStatementContext]] {

	/** Java8Parser.ClassDeclarationContext typedef */
	type RSC = Java8Parser.ReturnStatementContext

	// Ignore nested cycles
	override def visitClassDeclaration(ctx: Java8Parser.ClassDeclarationContext) = List[RSC]()
	override def visitClassMemberDeclaration(ctx: Java8Parser.ClassMemberDeclarationContext) = List[RSC]()
	override def visitInterfaceMemberDeclaration(ctx: Java8Parser.InterfaceMemberDeclarationContext) = List[RSC]()
	override def visitLambdaExpression(ctx: Java8Parser.LambdaExpressionContext) = List[RSC]()


	// Do not continue, so no nested classes included
	override def visitReturnStatement(returnCtx: RSC) = List[RSC](returnCtx)
	override def defaultResult = List[RSC]()
	override def aggregateResult(a: List[RSC], b: List[RSC]) = a ::: b
}
