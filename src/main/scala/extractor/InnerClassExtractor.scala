package org.omp4j.extractor

import org.omp4j.grammar._

/** Extracts first-level classes from ANTLR4 ParseTree */
class InnerClassExtractor extends Java8BaseVisitor[List[Java8Parser.ClassDeclarationContext]] {

	/** Java8Parser.ClassDeclarationContext typedef */
	type CDC = Java8Parser.ClassDeclarationContext

	// Do not continue, so no nested classes included
	override def visitClassDeclaration(classCtx: CDC) = List[CDC](classCtx)
	override def visitMethodDeclaration(methodCtx: Java8Parser.MethodDeclarationContext) = List[CDC]()
	override def defaultResult = List[CDC]()
	override def aggregateResult(a: List[CDC], b: List[CDC]) = a ::: b
}
