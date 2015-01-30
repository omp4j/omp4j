package org.omp4j.directive

import org.antlr.v4.runtime.{TokenStreamRewriter, Token}
import org.omp4j.Config
import org.omp4j.directive.DirectiveSchedule._
import org.omp4j.grammar.Java8Parser
import org.omp4j.tree.{OMPClass, OMPVariable}

case class ParallelFor(override val parent: Directive, override val publicVars: List[String], override val privateVars: List[String])(implicit schedule: DirectiveSchedule, ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, publicVars, privateVars) with ForCycle with LockMemory {
	override val parentOmpParallel = this
	override lazy val secondIter = true
	override def addAtomicBool(baseName: String) = super[LockMemory].addAtomicBool(baseName)

	/** Translate directives of type Master, Single */
	override protected def translateChildren(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		super.translateChildren(captured, capturedThis, directiveClass)
		childrenOfType[Master].foreach{_.postTranslate}
		childrenOfType[Single].foreach{_.postTranslate}
	}

	override protected def postTranslate(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		translateFor
		wrap(rewriter)(captured, capturedThis, directiveClass)
	}

}
