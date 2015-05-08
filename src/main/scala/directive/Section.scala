package org.omp4j.directive

import org.antlr.v4.runtime.{TokenStreamRewriter, Token}
import org.omp4j.exception.SyntaxErrorException
import org.omp4j.grammar.Java8Parser
import org.omp4j.Config
import org.omp4j.preprocessor.DirectiveVisitor
import org.omp4j.tree.{OMPFile, OMPClass, OMPVariable}

/** Section directive */
case class Section(override val parent: Directive)(implicit ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, List(), List())(DirectiveSchedule.Dynamic, null, ctx, cmt, line, conf) {
	override def validate(directives: DirectiveVisitor.DirectiveMap) = parent match {
		case secPar: Sections => ;
		case _ => throw new SyntaxErrorException(s"Error in directive before line $line: 'omp section' must by located directly in 'omp sections' block.")
	}

	// inherit all
	override lazy val threadCount = parent.threadCount
	override lazy val contextVar = parent.contextVar
	override lazy val executor = parent.executor
	override lazy val contextClass = parent.contextClass
	override lazy val threadArr = parent.threadArr
	override lazy val iter = parent.iter
	override lazy val iter2 = parent.iter2
	override lazy val secondIter = parent.secondIter
	override lazy val exceptionName = parent.exceptionName
	override val executorClass = parent.executorClass

	override def translate(implicit rewriter: TokenStreamRewriter, ompFile: OMPFile, directives: DirectiveVisitor.DirectiveMap) = {
		throw new RuntimeException(s"Internal error in directive before line $line: translate can't be run on Section!")
	}

	override protected def preTranslate(implicit rewriter: TokenStreamRewriter, ompFile: OMPFile) = {
		throw new RuntimeException(s"Internal error in directive before line $line: preTranslate can't be run on Section!")
	}

	override protected def postTranslate(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		throw new RuntimeException(s"Internal error in directive before line $line: postTranslate can't be run on Section!")
	}

	def postTranslate(id: Int)(implicit rewriter: TokenStreamRewriter) = {
		rewriter.insertBefore(ctx.start, s"if ($iter2 == $id) {\n")
		if (id > 0) rewriter.insertBefore(ctx.start, "else ")
		rewriter.insertAfter(ctx.stop, "}\n")

		deleteCmt
	}
}
