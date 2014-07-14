package org.omp4j.preprocessor

import org.omp4j.preprocessor.grammar._
import org.antlr.v4.runtime.Token

/** Directive structure */
case class Directive(
	cmt:       Token,
	ompCtx:    OMPParser.OmpUnitContext,
	ompParser: OMPParser,
	ctx:       Java8Parser.StatementContext,
	parser:    Java8Parser
) {
	override def toString() = {
		ompCtx.toStringTree(ompParser) +
		"\n" +
		ctx.toStringTree(parser)
	}
}
