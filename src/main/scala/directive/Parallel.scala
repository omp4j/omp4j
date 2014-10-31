package org.omp4j.directive

import org.antlr.v4.runtime.{TokenStreamRewriter, Token}
import org.omp4j.Config
import org.omp4j.directive.DirectiveSchedule._
import org.omp4j.grammar.Java8Parser
import org.omp4j.tree.{OMPFile, OMPClass, OMPVariable}

case class Parallel(override val parent: Directive, override val publicVars: List[String], override val privateVars: List[String])(implicit schedule: DirectiveSchedule, ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, publicVars, privateVars) {
	override val parentOmpParallel = this

	override protected def postTranslate(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		wrap(rewriter)(captured, capturedThis, directiveClass)
	}
}
