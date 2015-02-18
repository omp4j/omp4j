package org.omp4j.directive

import org.antlr.v4.runtime.{TokenStreamRewriter, Token}
import org.omp4j.directive.DirectiveSchedule._
import org.omp4j.exception.SyntaxErrorException
import org.omp4j.grammar.Java8Parser
import org.omp4j.preprocessor.DirectiveVisitor
import org.omp4j.tree.{OMPClass, OMPVariable}
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import org.omp4j.Config

case class Sections(override val parent: Directive)(implicit schedule: DirectiveSchedule, threadNum: String, ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, List(), List()) {
	override lazy val secondIter = true

	/** Check whether 'sections' directive consist only of 'section' directives */
	override def validate(directives: DirectiveVisitor.DirectiveMap) = {
		super.validate(directives)

		try {
			// extract substatements and fail whenever expected structure is not found (hence some other appeared)
			val stmtCtx = ctx.asInstanceOf[Java8Parser.StatementContext]
			val swts = stmtCtx.statementWithoutTrailingSubstatement
			val block = swts.block
			val blockStmts = block.blockStatements
			val children = blockStmts.blockStatement.asScala.map(_.statement)

			children.foreach{ child =>
				if (child == null) throw new SyntaxErrorException("In 'omp sections' only 'omp section' directives are allowed (null).")
				directives.find(_._1 == child) match {
					case Some((_, dir)) =>
						if (! dir.isInstanceOf[Section]) throw new SyntaxErrorException("The only directive allowed in 'omp sections' is 'omp section'.")
					case None => throw new SyntaxErrorException("In 'omp sections' only 'omp section' directives are allowed.")
				}

			}
		} catch {
			case e: ClassCastException   => throw new SyntaxErrorException("'omp sections' may be only before {...} statement.")
			case e: NullPointerException => throw new SyntaxErrorException("Corrupted 'omp sections' structure.")
		}
	}

	/** Translate directives of type Section */
	override protected def translateChildren(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		super.translateChildren(captured, capturedThis, directiveClass)
		childrenOfType[Section].zipWithIndex.foreach{ case (s, i) =>
			s.postTranslate(i)
		}
	}

	override protected def postTranslate(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		wrap(rewriter)(captured, capturedThis, directiveClass)
	}

}

