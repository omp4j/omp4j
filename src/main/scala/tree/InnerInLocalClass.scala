package org.omp4j.tree

import org.omp4j.Config
import org.omp4j.grammar._

/** The inner class representation (already inside some local class) */
case class InnerInLocalClass(ec: Java8Parser.ClassDeclarationContext, parent: OMPClass, parser: Java8Parser)(implicit conf: Config, val ompFile: OMPFile) extends OMPClass(Left(ec), parent, parser) with Nonreflectable
