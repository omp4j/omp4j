package org.omp4j.preprocessor

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.preprocessor.grammar._


/** Extracts first-level classes from ANTLR4 ParseTree */
class ClassExtractor extends Java8BaseVisitor[List[Java8Parser.ClassDeclarationContext]] {

	/** Java8Parser.ClassDeclarationContext typedef */
	type CDC = Java8Parser.ClassDeclarationContext

	/** Do not continue, so no nested classes included */
	override def visitClassDeclaration(classCtx: CDC) = List[CDC](classCtx)
	override def defaultResult() = List[CDC]()
	override def aggregateResult(a: List[CDC], b: List[CDC]) = a ::: b
}

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

/** Extract class fields from ANTLR4 ParseTree, stop at any ClassDeclaration */
class FieldExtractor extends Java8BaseVisitor[List[Java8Parser.FieldDeclarationContext]] {

	/** Java8Parser.FieldDeclarationContext typedef */
	type FDC = Java8Parser.FieldDeclarationContext

	/** Do not continue, so no nested classes included */
	override def visitClassDeclaration(classCtx: Java8Parser.ClassDeclarationContext) = List[FDC]()

	/** Add field context */
	override def visitFieldDeclaration(fieldCtx: FDC) = List[FDC](fieldCtx)
	override def defaultResult() = List[FDC]()
	override def aggregateResult(a: List[FDC], b: List[FDC]) = a ::: b
}

/** Extracts all local variable declarations from ANTLR4 ParseTree with no statement pass */
class FirstLevelLocalVariableExtractor extends Java8BaseVisitor[Set[Java8Parser.LocalVariableDeclarationContext]] {

	/** Java8Parser.LocalVariableDeclarationContext typedef */
	type LVDC = Java8Parser.LocalVariableDeclarationContext

	/** Add local variable declaration context */
	override def visitLocalVariableDeclaration(variableCtx: LVDC) = Set[LVDC](variableCtx)

	/** Don't get into nested statements */
	override def visitStatement(c: Java8Parser.StatementContext) = Set[LVDC]()
	// override def visitBlockStatement(c: Java8Parser.BlockStatementContext) = Set[LVDC]()

	override def defaultResult() = Set[LVDC]()
	override def aggregateResult(a: Set[LVDC], b: Set[LVDC]) = a ++ b
}

/** Extracts all local variable declarations from ANTLR4 ParseTree */
class LocalVariableExtractor extends Java8BaseVisitor[Set[Java8Parser.LocalVariableDeclarationContext]] {

	/** Java8Parser.LocalVariableDeclarationContext typedef */
	type LVDC = Java8Parser.LocalVariableDeclarationContext

	/** Add local variable declaration context */
	override def visitLocalVariableDeclaration(variableCtx: LVDC) = Set[LVDC](variableCtx)

	/** Don't get into nested statements */
	// override def visitStatement(c: Java8Parser.StatementContext) = Set[LVDC]()
	// override def visitBlockStatement(c: Java8Parser.BlockStatementContext) = Set[LVDC]()

	override def defaultResult() = Set[LVDC]()
	override def aggregateResult(a: Set[LVDC], b: Set[LVDC]) = a ++ b
}
