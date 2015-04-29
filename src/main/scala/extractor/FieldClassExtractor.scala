package org.omp4j.extractor

import org.omp4j.grammar._

import scala.collection.JavaConverters._

/** Extract classes defined as field (nested ones) */
class FieldClassExtractor extends Java8BaseVisitor[List[Java8Parser.ClassDeclarationContext]] {

	/** Java8Parser.ClassDeclarationContext typedef */
	type CDC = Java8Parser.ClassDeclarationContext

	// Do not continue, so no nested classes included
	override def visitClassDeclaration(classCtx: CDC) = {
		var res = List[CDC]()
		try {
			val decls = classCtx.normalClassDeclaration.classBody.classBodyDeclaration.asScala
			decls.foreach{ d =>
				try {
					val clD = d.classMemberDeclaration.classDeclaration
					if (clD != null) res = clD +: res
				} catch {
					case e: Exception => res = List[CDC]()
				}
			}
		} catch {
			case e: Exception => res = List[CDC]()
		}
		res :+ classCtx
	}

	// Don't get into nested statements
	override def visitStatement(ctx: Java8Parser.StatementContext) = List[CDC]()

	override def defaultResult = List[CDC]()
	override def aggregateResult(a: List[CDC], b: List[CDC]) = a ::: b
}
