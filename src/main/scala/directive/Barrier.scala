package org.omp4j.directive

import org.antlr.v4.runtime.Token
import org.omp4j.Config
import org.omp4j.exception.SyntaxErrorException
import org.omp4j.grammar.Java8Parser

case class Barrier(override val parent: Directive)(ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, List(), List())(DirectiveSchedule.Static, ctx, cmt, line, conf) {

	// validate existence of some omp parent block
	override def validate() = {
		if (parent == null) throw new SyntaxErrorException("'omp barrier' must be nested in some other omp block.")
		super.validate()
	}
}
