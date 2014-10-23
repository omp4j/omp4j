package org.omp4j.tree

import org.omp4j.Config
import org.omp4j.grammar._

abstract class OMPBase(ec: OMPClass.EitherCtx, parser: Java8Parser)(implicit conf: Config) {
	override def toString = ec match {
		case Left(x)  => x.toStringTree(parser)
		case Right(x) => x.toStringTree(parser)
	}
}
