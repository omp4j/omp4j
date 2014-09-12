package org.omp4j.test

import java.io.File
import org.scalatest._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.Config
import org.omp4j.preprocessor._
import org.omp4j.utils.FileTreeWalker
import org.omp4j.preprocessor.grammar._

/** Loads given file */
abstract class AbstractLoadedContext(path: String) {
	val file = new File(getClass.getResource(path).toURI.getPath)
	val lexer = new Java8Lexer(new ANTLRFileStream(file.getPath))
	val tokens = new CommonTokenStream(lexer)
	val parser = new Java8Parser(tokens)
	val t = parser.compilationUnit
	val directives = (new DirectiveVisitor(tokens, parser)).visit(t)
	implicit val conf: Config = new Config(Array(file.getAbsolutePath))
	conf.init

	// TODO: delete really all
	override protected def finalize: Unit = FileTreeWalker.recursiveDelete(conf.workDir)
}
