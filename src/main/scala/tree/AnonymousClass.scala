package org.omp4j.tree

import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.grammar._

import scala.collection.JavaConverters._

/** The anonymous class representation */
case class AnonymousClass(_clb: Java8Parser.ClassBodyContext, parent: OMPClass, parser: Java8Parser)(implicit conf: Config, val ompFile: OMPFile) extends OMPClass(Right(_clb), parent, parser) with Nonreflectable {

	override lazy val classBody = _clb
	override lazy val name = getAnonName
	override lazy val FQN = name	// for now we can't possibly know nothing more
	override lazy val key = classBody

	private def getAnonName: String = {
		try {
			val par = classBody.parent.asInstanceOf[Java8Parser.ClassInstanceCreationExpression_lf_primaryContext]
			par.Identifier.getText
		} catch { case e: ClassCastException => try {
			val par = classBody.parent.asInstanceOf[Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext]
			par.Identifier.asScala.mkString(".")
		} catch { case e: ClassCastException => try {
			val par = classBody.parent.asInstanceOf[Java8Parser.ClassInstanceCreationExpressionContext]
			par.Identifier.asScala.mkString(".")
		} catch {
			case e: ClassCastException => throw new ParseException("Anonymous class not parsed", e)
		}}}
	}

	override def findAllFields = findFieldsSyntactically ++ findInheritedFields(name)
}
