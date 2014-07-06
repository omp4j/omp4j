package org.omp4j.preprocessor

import scala.io.Source
import scala.util.control.Breaks._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.preprocessor.grammar._

/** Listener for directive application */
class TranslationVisitor(tokens: CommonTokenStream, parser: Java8Parser, tree: Java8Parser.CompilationUnitContext)(implicit conf: Config) extends Java8BaseVisitor[Unit] {

	/** Reflected file structure */
	private lazy val ompFile = new OMPFile(tree, parser)

	/** List of directives */
	private lazy val directives = (new DirectiveVisitor(tokens, parser)).visit(tree)

	/** Directive translator */
	private lazy val translator = new Translator(tokens, parser, directives, ompFile)

	/** Rewriter for directive expansions*/
	private lazy val rewriter = new TokenStreamRewriter(tokens)

	// TODO: OMPFile test
	// ompFile.classes.foreach{ c =>
	// 	println(c.name)
	// 	c.allMethods.foreach{ m =>
	// 		println("\t" + m.getName())
	// 	}
	// }

	// TODO: doc
	def translate: String = {
		visit(tree)
		rewriter.getText()
	}
	
	/** Translate statements having directive */
	override def visitStatement(ctx: Java8Parser.StatementContext) = {
		directives.find(d => d.ctx == ctx) match {	// TODO!!! 
			case Some(d) => rewriter.replace(d.ctx.start, d.ctx.stop, translator.translate(d));
			case None => ;
		}
		super.visitStatement(ctx)	// continue visiting
	}
}

