package org.omp4j.directive

import org.antlr.v4.runtime.{TokenStreamRewriter, Token}
import org.omp4j.Config
import org.omp4j.directive.DirectiveSchedule._
import org.omp4j.exception.SyntaxErrorException
import org.omp4j.grammar.Java8Parser
import org.omp4j.tree.{OMPClass, OMPVariable}

case class For(override val parent: Directive, override val publicVars: List[String], override val privateVars: List[String])(implicit ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, publicVars, privateVars)(DirectiveSchedule.Static, ctx, cmt, line, conf) with ForCycle {


	// validate existence of omp-parallel parent block
	override def validate() = {
		if (parentOmpParallel == null) throw new SyntaxErrorException("'omp for' in no 'omp parallel [for]' block.")
		super.validate()
	}

	// inherit all
	override lazy val threadCount = parentOmpParallel.threadCount
	override lazy val contextVar = parentOmpParallel.contextVar
	override lazy val contextClass = parentOmpParallel.contextClass
	override lazy val threadArr = parentOmpParallel.threadArr
	override lazy val iter = parentOmpParallel.iter
	override lazy val iter2 = parentOmpParallel.iter2
	override lazy val exceptionName = parentOmpParallel.exceptionName

	override protected def postTranslate(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		translateFor;
		wrap(rewriter)(captured, capturedThis, directiveClass)
	}

}
