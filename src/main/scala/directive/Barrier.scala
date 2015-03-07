package org.omp4j.directive

import org.antlr.v4.runtime.{TokenStreamRewriter, Token}
import org.omp4j.Config
import org.omp4j.exception.SyntaxErrorException
import org.omp4j.grammar.Java8Parser
import org.omp4j.preprocessor.DirectiveVisitor
import org.omp4j.tree.{OMPClass, OMPVariable, OMPFile}

case class Barrier(override val parent: Directive)(ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, List(), List())(DirectiveSchedule.Static, null, ctx, cmt, line, conf) {

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
	val barrierVar = uniqueName("barrier")

	// validate existence of some omp parent block
	override def validate(directives: DirectiveVisitor.DirectiveMap) = {
		if (parent == null) throw new SyntaxErrorException("'omp barrier' must be nested in some other omp block.")
		// TODO: test condition
		if (!parent.isInstanceOf[Parallel] && !parent.isInstanceOf[ParallelFor] && !parent.isInstanceOf[For]) throw new SyntaxErrorException("'omp barrier' may be located only in 'omp [parallel] [for]'.")
		super.validate(directives)
	}

	override def translate(implicit rewriter: TokenStreamRewriter, ompFile: OMPFile, directives: DirectiveVisitor.DirectiveMap) = {
		throw new RuntimeException("translate can't be run on Barrier!")
	}

	override protected def preTranslate(implicit rewriter: TokenStreamRewriter, ompFile: OMPFile) = {
		throw new RuntimeException("preTranslate can't be run on Barrier!")
	}

	override protected def postTranslate(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		throw new RuntimeException("postTranslate can't be run on Barrier!")
	}

	def postTranslate(implicit rewriter: TokenStreamRewriter) = {
		rewriter.insertBefore(ctx.start, s"""$executor.hitBarrier("$barrierVar");\n""")
		deleteCmt
	}

}
