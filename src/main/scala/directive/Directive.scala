package org.omp4j.directive

import org.antlr.v4.runtime.{ParserRuleContext, Token}
import org.omp4j.directive.DirectiveSchedule._
import org.omp4j.exception._
import org.omp4j.grammar._

import scala.collection.JavaConverters._

/** Abstract omp directive class; implemented by several case classes */
abstract class Directive(val parent: Directive, val publicVars: List[String], val privateVars: List[String])(implicit val schedule: DirectiveSchedule, val ctx: Java8Parser.StatementContext, val cmt: Token, val line: Int) {
	validate()

	/** Validation of parent */
	def validate(): Unit = parent match {
		case _: Sections => throw new SyntaxErrorException("In block 'omp sections' only 'omp section' blocks are allowed.")
		case _ => ;
	}

	override def toString =	s"Before line $line: ${cmt.getText}"
}

/** Static directive procedures */
object Directive {

	/** Directive constructor */
	def apply(parent: Directive, ompCtx: OMPParser.OmpUnitContext, cmt: Token, ctx: Java8Parser.StatementContext): Directive = {
		if (ompCtx == null) throw new SyntaxErrorException("null OMP context")

		val parallel = ompCtx.ompParallel
		val parallelFor = ompCtx.ompParallelFor
		val nonParFor = ompCtx.ompFor
		val sections = ompCtx.ompSections
		val section = ompCtx.ompSection

		if (parallel != null) {
			val (pub, pri) = separate(parallel.ompModifier.asScala.toList)
			new Parallel(parent, pub, pri)(DirectiveSchedule(parallel.ompSchedule), ctx, cmt, getLine(ctx))
		} else if (parallelFor != null) {
			val (pub, pri) = separate(parallelFor.ompModifier.asScala.toList)
			new ParallelFor(parent, pub, pri)(DirectiveSchedule(parallelFor.ompSchedule), ctx, cmt, getLine(ctx))
		} else if (nonParFor != null) {
			val (pub, pri) = separate(nonParFor.ompModifier.asScala.toList)
			new For(parent, pub, pri)(DirectiveSchedule(nonParFor.ompSchedule), ctx, cmt, getLine(ctx))
		} else if (sections != null) {
			new Sections(parent)(DirectiveSchedule(sections.ompSchedule), ctx, cmt, getLine(ctx))
		} else if (section != null) {
			new Section(parent)(ctx, cmt, getLine(ctx))
		} else {
			throw new SyntaxErrorException("Invalid directive")
		}
	}

	/** Get approximate line number */
	private def getLine(ctx: ParserRuleContext) = {
		if (ctx == null || ctx.start == null) -1
		else ctx.start.getLine
	}

	/**
	  * Separate public and private variables
	  * @return tuple of (Public, Private)
	  */
	private def separate(list: List[OMPParser.OmpModifierContext]): (List[String], List[String]) = {

		/** Extracts variables from public/private statement */
		def getVars(vars: OMPParser.OmpVarsContext): List[String] = {
			if (vars == null) List()
			else {
				val v = vars.ompVar
				if (v == null) List()
				else v.asScala.map(_.VAR.getText).toList
			}

		}

		if (list == null || list.size == 0) (List(), List())
		else {
			val head = list.head
			val (resPub, resPri) = separate(list.tail)

			if (head.PUBLIC != null) (getVars(head.ompVars) ++ resPub, resPri)
			else if (head.PRIVATE != null) (resPub, getVars(head.ompVars) ++ resPri)
			else throw new ParseException("Unexpected variable modifier")
		}
	}
}
