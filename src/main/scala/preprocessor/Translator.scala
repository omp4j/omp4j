package org.omp4j.preprocessor

import scala.io.Source

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.preprocessor.grammar._
import org.omp4j.preprocessor.exception._

/** Translate context given with respect to directives */
class Translator(directives: List[Directive], tokens: TokenStream, ctx: Java8Parser.CompilationUnitContext, tree: OMPFile) {

	/** Get translated source code */
	def translate(): String = {
		val walker = new ParseTreeWalker()
		val rl = new TranslationListener(directives, tokens, tree)
		walker.walk(rl, ctx)
		rl.rewriter.getText()
	}

	private class TranslationListener(directives: List[Directive], tokens: TokenStream, tree: OMPFile) extends Java8BaseListener {

		/** Java8Parser.FieldDeclarationContext typedef */
		type SC = Java8Parser.StatementContext
		
		/** Rewriter for directive expansions*/
		var rewriter = new TokenStreamRewriter(tokens)

		/** Filter statements with directive */
		override def enterStatement(ctx: SC) = {
			directives.find(d => d.ctx == ctx) match {
				case Some(d) => addDirectiveHead(ctx)
				case None => ;
			}
		}

		/** Filter statements with directive */
		override def exitStatement(ctx: SC) = {
			directives.find(d => d.ctx == ctx) match {
				case Some(d) => addDirectiveTail(ctx)
				case None => ;
			}
		}

		/** For now only add simple comment at the beginning -> insert context */
		private def addDirectiveHead(ctx: SC) = {
			val source = Source.fromURL(getClass.getResource("/head.in"))
			val head = source.getLines mkString "\n"

			rewriter.insertBefore(ctx.start, head);
		}

		/** For now only add simple comment at the end */
		private def addDirectiveTail(ctx: SC) = {
			val source = Source.fromURL(getClass.getResource("/tail.in"))
			val tail = source.getLines mkString "\n"

			rewriter.insertAfter(ctx.stop, tail);
		}
	}
}
