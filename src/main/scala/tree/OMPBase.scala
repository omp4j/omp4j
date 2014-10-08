package org.omp4j.tree

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.grammar._
import org.omp4j.Config

abstract class OMPBase(ec: OMPClass.EitherCtx, parser: Java8Parser)(implicit conf: Config) {
	override def toString = ec match {
		case Left(x)  => x.toStringTree(parser)
		case Right(x) => x.toStringTree(parser)
	}
}
