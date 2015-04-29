package org.omp4j.preprocessor

import org.antlr.v4.runtime.{CommonTokenStream, TokenStreamRewriter, ParserRuleContext}
import org.omp4j.grammar.{Java8Parser, Java8BaseVisitor}

/** Tool for ThreadID removal.
  *
  * The original source is not modified.
  * TODO: unit tests
  *
  * @constructor setup new rewriter
  * @param origCtx
  * @param tokens
  */
class ThreadIdRemover(origCtx: ParserRuleContext, tokens: CommonTokenStream) extends Java8BaseVisitor[Unit] {

	/** Mutable rewriter */
	private val rewriter = new TokenStreamRewriter(tokens)

	/** Iterate through the AST via Visitor pattern, translating ThreadID macros to `1`
	  *
	  * @return ThreadID-less source code
	  */
	def removedIds() = {
		visit(origCtx)
		rewriter.getText
	}

	override def visitOmpThreadNum(ctx: Java8Parser.OmpThreadNumContext) = {
		super.visitOmpThreadNum(ctx)
		rewriter.replace(ctx.start, ctx.stop, "1")
	}

	override def visitOmpNumThreads(ctx: Java8Parser.OmpNumThreadsContext) = {
		super.visitOmpNumThreads(ctx)
		rewriter.replace(ctx.start, ctx.stop, "1")
	}

}
