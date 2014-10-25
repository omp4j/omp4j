package org.omp4j.directive

import org.antlr.v4.runtime.Token
import org.omp4j.Config
import org.omp4j.directive.DirectiveSchedule._
import org.omp4j.grammar.Java8Parser

case class For(override val parent: Directive, override val publicVars: List[String], override val privateVars: List[String])(implicit schedule: DirectiveSchedule, ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, publicVars, privateVars)
