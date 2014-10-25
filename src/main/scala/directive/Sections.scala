package org.omp4j.directive

import org.antlr.v4.runtime.Token
import org.omp4j.directive.DirectiveSchedule._
import org.omp4j.grammar.Java8Parser

import scala.collection.mutable.ListBuffer
import org.omp4j.Config

case class Sections(override val parent: Directive)(implicit schedule: DirectiveSchedule, ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, List(), List()) {
	private var secBuffer = ListBuffer[Section]()
	def registerSection(s: Section) = secBuffer += s
	def sections = secBuffer.toList
}
