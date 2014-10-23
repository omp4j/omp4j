package org.omp4j.directive

import org.antlr.v4.runtime.Token
import org.omp4j.exception.SyntaxErrorException
import org.omp4j.grammar.Java8Parser

case class Section(override val parent: Directive)(implicit ctx: Java8Parser.StatementContext, cmt: Token, line: Int) extends Directive(parent, List(), List())(DirectiveSchedule.Static, ctx, cmt, line) {
	override def validate = parent match {
		case _: Sections => ;
		case _ => throw new SyntaxErrorException("'omp section' must by located directly in 'omp sections' block.")
	}
}
