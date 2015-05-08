package org.omp4j.directive

import org.antlr.v4.runtime.{TokenStreamRewriter, Token}
import org.omp4j.Config
import org.omp4j.exception.SyntaxErrorException
import org.omp4j.grammar.{OMPParser, Java8Parser}
import org.omp4j.preprocessor.DirectiveVisitor
import org.omp4j.tree.{OMPClass, OMPVariable, OMPFile}

/** Critical directive */
class Critical(override val parent: Directive, syncVarCtx: OMPParser.OmpVarContext)(implicit ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, List(), List())(DirectiveSchedule.Static, null, ctx, cmt, line, conf) {

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

	/** If synchonization variable is provided, its existence is presumed. Additionally, it must be marked final */
	val syncVar = syncVarCtx match {
		case null => contextVar
		case _    => syncVarCtx.getText
	}

	override def validate(directives: DirectiveVisitor.DirectiveMap) = {
		if (parentOmpParallel == null) throw new SyntaxErrorException(s"Error in directive before line $line: 'omp for' in no 'omp parallel [for]' block.")
		super.validate(directives)
	}

	override def translate(implicit rewriter: TokenStreamRewriter, ompFile: OMPFile, directives: DirectiveVisitor.DirectiveMap) = {
		throw new RuntimeException(s"Internal error in directive before line $line: translate can't be run on Critical!")
	}

	override protected def preTranslate(implicit rewriter: TokenStreamRewriter, ompFile: OMPFile) = {
		throw new RuntimeException(s"Internal error in directive before line $line: preTranslate can't be run on Critical!")
	}

	override protected def postTranslate(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		throw new RuntimeException(s"Internal error in directive before line $line: postTranslate can't be run on Critical!")
	}

	def postTranslate(implicit rewriter: TokenStreamRewriter) = {
		rewriter.insertBefore(ctx.start, s"synchronized($syncVar) {\n")
		rewriter.insertAfter(ctx.stop, "}\n")
		deleteCmt
	}

}
