package org.omp4j.directive

import org.antlr.v4.runtime.Token
import org.omp4j.exception.SyntaxErrorException
import org.omp4j.grammar.Java8Parser
import org.omp4j.Config

case class Section(override val parent: Directive)(implicit ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, List(), List())(DirectiveSchedule.Static, ctx, cmt, line, conf) {
	override def validate = parent match {
		case secPar: Sections => secPar.registerSection(this)
		case _ => throw new SyntaxErrorException("'omp section' must by located directly in 'omp sections' block.")
	}
}
