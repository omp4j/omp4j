package org.omp4j.extractor

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.preprocessor.grammar._

/** Extract class fields from ANTLR4 ParseTree, stop at any ClassDeclaration */
class FieldExtractor extends Java8BaseVisitor[List[Java8Parser.FieldDeclarationContext]] {

	/** Java8Parser.FieldDeclarationContext typedef */
	type FDC = Java8Parser.FieldDeclarationContext

	/** Do not continue, so no nested classes included */
	override def visitClassDeclaration(classCtx: Java8Parser.ClassDeclarationContext) = List[FDC]()

	/** Add field context */
	override def visitFieldDeclaration(fieldCtx: FDC) = List[FDC](fieldCtx)
	override def defaultResult = List[FDC]()
	override def aggregateResult(a: List[FDC], b: List[FDC]) = a ::: b
}
