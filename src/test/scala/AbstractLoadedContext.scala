package org.omp4j.test

import java.io.File

import org.antlr.v4.runtime._
import org.omp4j.Config
import org.omp4j.grammar._
import org.omp4j.preprocessor._
import org.omp4j.utils.FileTreeWalker

/** Loads given file */
abstract class AbstractLoadedContext(path: String) {
	val file = new File(getClass.getResource(path).toURI.getPath)
	implicit val conf: Config = new Config(Array(file.getAbsolutePath))
	val lexer = new Java8Lexer(new ANTLRFileStream(file.getPath))
	val tokens = new CommonTokenStream(lexer)
	val parser = new Java8Parser(tokens)
	val t = parser.compilationUnit
	val directives = (new DirectiveVisitor(tokens, parser)).visit(t)
	conf.init

	// TODO: delete really all
	override protected def finalize() = FileTreeWalker.recursiveDelete(conf.workDir)
}
