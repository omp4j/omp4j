package org.omp4j.directive

import org.antlr.v4.runtime.{TokenStreamRewriter, Token}
import org.omp4j.Config
import org.omp4j.exception.SyntaxErrorException
import org.omp4j.grammar.{OMPParser, Java8Parser}
import org.omp4j.preprocessor.DirectiveVisitor
import org.omp4j.tree.{OMPClass, OMPVariable, OMPFile}

class Critical(override val parent: Directive, syncVarCtx: OMPParser.OmpVarContext)(implicit ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, List(), List())(DirectiveSchedule.Static, ctx, cmt, line, conf) {

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

	// TODO: check variable existence
	val syncVar = syncVarCtx match {
		case null => contextVar
		case _    => syncVarCtx.getText
	}

	override def validate(directives: DirectiveVisitor.DirectiveMap) = {
		if (parentOmpParallel == null) throw new SyntaxErrorException("'omp for' in no 'omp parallel [for]' block.")
		super.validate(directives)
	}

	override def translate(implicit rewriter: TokenStreamRewriter, ompFile: OMPFile, directives: DirectiveVisitor.DirectiveMap) = {
		throw new RuntimeException("translate can't be run on Critical!")
	}

	override protected def preTranslate(implicit rewriter: TokenStreamRewriter, ompFile: OMPFile) = {
		throw new RuntimeException("preTranslate can't be run on Critical!")
	}

	override protected def postTranslate(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		throw new RuntimeException("postTranslate can't be run on Critical!")
	}

	def postTranslate(implicit rewriter: TokenStreamRewriter) = {
		rewriter.insertBefore(ctx.start, s"synchronized($syncVar) {\n")
		rewriter.insertAfter(ctx.start, "}\n")
	}

}
