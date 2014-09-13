package org.omp4j.tree

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.Config
import org.omp4j.grammar._

case class StackClass (ctx: Java8Parser.ClassDeclarationContext)(implicit conf: Config) {
	lazy val name = ctx.normalClassDeclaration.Identifier.getText
	lazy val isLocal = if (ctx.getParent.isInstanceOf[Java8Parser.ClassMemberDeclarationContext] || ctx.getParent.getParent.isInstanceOf[Java8Parser.CompilationUnitContext]) false else true
	// println(s"-> $name ... $isLocal")
}
