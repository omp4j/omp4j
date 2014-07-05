package org.omp4j.preprocessor

import scala.io.Source
import scala.util.control.Breaks._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.Config
import org.omp4j.preprocessor.grammar._
import org.omp4j.preprocessor.exception._

/** Translate context given with respect to directives */
class Translator(directives: List[Directive], tokens: TokenStream, ctx: Java8Parser.CompilationUnitContext, tree: OMPFile, parser: Java8Parser)(implicit conf: Config) {

	/** Get translated source code */
	def translate: String = {
		val walker = new ParseTreeWalker()
		val rl = new TranslationListener(directives, tokens, tree, parser)
		walker.walk(rl, ctx)
		rl.rewriter.getText()
	}
}
