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
