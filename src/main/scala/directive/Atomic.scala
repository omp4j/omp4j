package org.omp4j.directive

import org.antlr.v4.runtime.{TokenStreamRewriter, Token}
import org.omp4j.Config
import org.omp4j.exception.SyntaxErrorException
import org.omp4j.grammar.Java8Parser
import org.omp4j.preprocessor.DirectiveVisitor
import org.omp4j.tree.{OMPClass, OMPVariable, OMPFile}

class Atomic(override val parent: Directive)(implicit ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Critical(parent, null) {

	// inherit all
//	override lazy val threadCount = parent.threadCount
//	override lazy val contextVar = parent.contextVar
//	override lazy val executor = parent.executor
//	override lazy val contextClass = parent.contextClass
//	override lazy val threadArr = parent.threadArr
//	override lazy val iter = parent.iter
//	override lazy val iter2 = parent.iter2
//	override lazy val secondIter = parent.secondIter
//	override lazy val exceptionName = parent.exceptionName
//	override val executorClass = parent.executorClass
}
