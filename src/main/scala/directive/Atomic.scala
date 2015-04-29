package org.omp4j.directive

import org.antlr.v4.runtime.{TokenStreamRewriter, Token}
import org.omp4j.Config
import org.omp4j.exception.SyntaxErrorException
import org.omp4j.grammar.Java8Parser
import org.omp4j.preprocessor.DirectiveVisitor
import org.omp4j.tree.{OMPClass, OMPVariable, OMPFile}

/** Atomic directive directly extending Critical directive*/
class Atomic(override val parent: Directive)(implicit ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Critical(parent, null) {}
