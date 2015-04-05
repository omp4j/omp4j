package org.omp4j.preprocessor

import org.antlr.v4.runtime.{CommonTokenStream, TokenStreamRewriter, ParserRuleContext}
import org.omp4j.grammar.{Java8Parser, Java8BaseVisitor}

/**
 * Created by petr on 29.1.15.
 * TODO: doc
 * TODO: unit tests
 */
class ThreadIdRemover(origCtx: ParserRuleContext, tokens: CommonTokenStream) extends Java8BaseVisitor[Unit] {

	val rewriter = new TokenStreamRewriter(tokens)
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
