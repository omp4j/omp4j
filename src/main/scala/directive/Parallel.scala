package org.omp4j.directive

import org.antlr.v4.runtime.{ParserRuleContext, TokenStreamRewriter, Token}
import org.omp4j.Config
import org.omp4j.directive.DirectiveSchedule._
import org.omp4j.grammar.Java8Parser
import org.omp4j.tree.{OMPFile, OMPClass, OMPVariable}

case class Parallel(override val parent: Directive, override val publicVars: List[String], override val privateVars: List[String])(implicit schedule: DirectiveSchedule, threadNum: String, ctx: ParserRuleContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, publicVars, privateVars) with LockMemory {
	override val parentOmpParallel = this
	override def addAtomicBool(baseName: String) = super[LockMemory].addAtomicBool(baseName)
	override lazy val secondIter = childrenOfType[For].size > 0

	/** Translate directives of type Master, Single */
	override protected def translateChildren(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		super.translateChildren(captured, capturedThis, directiveClass)
		childrenOfType[Master].foreach{_.postTranslate}
		childrenOfType[Single].foreach{_.postTranslate}
		childrenOfType[For].foreach{_.postTranslate(captured, capturedThis, directiveClass)}
	}

	override protected def postTranslate(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		wrap(rewriter)(captured, capturedThis, directiveClass)
	}
}
