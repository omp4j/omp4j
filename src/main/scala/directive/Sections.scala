package org.omp4j.directive

import org.antlr.v4.runtime.Token
import org.omp4j.directive.DirectiveSchedule._
import org.omp4j.grammar.Java8Parser

case class Sections(override val parent: Directive)(implicit schedule: DirectiveSchedule, ctx: Java8Parser.StatementContext, cmt: Token, line: Int) extends Directive(parent, List(), List())
