package org.omp4j.test

import java.io.File
import org.scalatest._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.preprocessor._
import org.omp4j.preprocessor.grammar._

/** Loads given file */
abstract class AbstractLoadedContext(path: String) {
	val file = new File(getClass.getResource(path).toURI().getPath())
	lazy val lexer = new Java8Lexer(new ANTLRFileStream(file.getPath()))
	lazy val tokens = new CommonTokenStream(lexer)
	lazy val parser = new Java8Parser(tokens)
	lazy val t = parser.compilationUnit()
	lazy val directives = (new DirectiveVisitor(tokens, parser)).visit(t)
}


