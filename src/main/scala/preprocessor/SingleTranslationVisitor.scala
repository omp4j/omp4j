package org.omp4j.preprocessor

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.TokenStreamRewriter
import org.antlr.v4.runtime.tree.{TerminalNode, ParseTree}

import org.omp4j.Config
import org.omp4j.grammar.Java8BaseVisitor
import org.omp4j.grammar.Java8Parser


/** Simple visitor that walks through the directive and translate `oldName` into `newName`.
  *
  * Usually used when single variable is required to be translated (for-loop iterator etc.)
  *
  * @param rewriter rewriter object to whom modifications are presented
  * @param oldName variable name that may be modified
  * @param newName new variable name
  * @param conf configuration context
  */
class SingleTranslationVisitor(rewriter: TokenStreamRewriter, oldName: String, newName: String)(implicit conf: Config) extends Java8BaseVisitor[Unit] {

	/**
	 * Tries the variable modification if the variable is named as `oldName`.
	 * @param ctx what to try
	 */
	private def tryReplacement(ctx: ParserRuleContext) = {
		try {

			val id = ctx.getText
			if (oldName == id) rewriter.replace(ctx.start, ctx.stop, newName)
		} catch {
			// This should never happen
			case e: IllegalArgumentException => conf.logger.log(s"IAE: ${e.getMessage}")
		}
	}

	override def visit(ctx: ParseTree) = {
		if (ctx != null) super.visit(ctx)
	}

	/** Capture variables/fields */
	override def visitExpressionName(ctx: Java8Parser.ExpressionNameContext) = {
		tryReplacement(ctx)
		super.visitExpressionName(ctx)
	}

	/** Handle primary if no-array expression occures */
	override def visitPrimary(ctx: Java8Parser.PrimaryContext) = {
		tryReplacement(ctx)
		super.visitPrimary(ctx)
	}

	override def visitVariableDeclaratorId(ctx: Java8Parser.VariableDeclaratorIdContext) = {
		tryReplacement(ctx)
		super.visitVariableDeclaratorId(ctx)
	}
}
