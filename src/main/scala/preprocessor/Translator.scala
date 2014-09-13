package org.omp4j.preprocessor

import scala.io.Source
import scala.util.control.Breaks._
import scala.collection.JavaConverters._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.Config
import org.omp4j.tree._
import org.omp4j.exception._
import org.omp4j.extractor._
import org.omp4j.grammar._

/** Translate context given with respect to directives */
class Translator(tokens: CommonTokenStream, parser: Java8Parser, directives: List[Directive], ompFile: OMPFile)(implicit conf: Config) {

	/** Get tokens matching to context given
	  * Must use this construct as Java8Parser has no logical hierarchy
	  */
	def getContextTokens[T <: {
			def getStart(): Token;
			def getStop(): Token
		}](ctx: T) = {

		val startId = ctx.getStart.getTokenIndex
		val stopId = ctx.getStop.getTokenIndex
		for {token <- tokens.getTokens.asScala; i = token.getTokenIndex; if (i >= startId); if(i <= stopId)} yield token
	}

	/** Translate directive given using special methods for each directive type */
	def translate(directive: Directive, rewriter: TokenStreamRewriter, locals: Set[OMPVariable], params: Set[OMPVariable], captured: Set[OMPVariable], capturedThis: Boolean, currentClass: String) = {
		if      (directive.ompCtx.ompParallel    != null) translateParallel(new ContextContainer(directive.ompCtx.ompParallel, directive.ctx, rewriter, locals, params, captured, capturedThis, currentClass, false))
		else if (directive.ompCtx.ompParallelFor != null) translateParallelFor(new ContextContainer(directive.ompCtx.ompParallel, directive.ctx, rewriter, locals, params, captured, capturedThis, currentClass, true))
		else if (directive.ompCtx.ompSections    != null) translateSections(directive.ompCtx.ompSections, directive.ctx, rewriter, locals, params, captured, capturedThis, currentClass)
		else if (directive.ompCtx.ompFor         != null) translateFor(directive.ompCtx.ompFor, directive.ctx, rewriter, locals, params, captured, capturedThis, currentClass)
		else throw new IllegalArgumentException("Unsupported directive")
		rewriter.replace(directive.cmt, "\n")
	}

	/** Translate "omp parallel" */
	private def translateParallel(cc: ContextContainer) = {
		cc.wrap
	}

	/** Translate "omp parallel for" */
	private def translateParallelFor(cc: ContextContainer) = {

		val forStatement = cc.ctx.forStatement
		if (forStatement == null) throw new ParseException("For directive before non-for statement")
		val basicForStatement = forStatement.basicForStatement
		if (basicForStatement == null) throw new ParseException("For directive before enhanced for statement")

		// banish break/continue
		if ((new FirstLevelBreakExtractor ).visit(basicForStatement).size > 0) throw new ParseException("Break statements are not allowed")
		if ((new FirstLevelContinueExtractor ).visit(basicForStatement).size > 0) throw new ParseException("Continue statements are not allowed")

		// INIT
		val forInit = basicForStatement.forInit
		if (forInit == null) throw new ParseException("For directive before enhanced for statement")
		val forList = forInit.localVariableDeclaration.variableDeclaratorList.variableDeclarator.asScala
		if (forList.size != 1) throw new ParseException("For initialization must containt exactly one variable")
		val iterName = forList.head.variableDeclaratorId.Identifier.getText	// var name
		val initExpr = forList.head.variableInitializer.expression	// right side of init assignment

		// COND
		val limitExpr = basicForStatement.expression
		var cond: Java8Parser.ShiftExpressionContext = null	// for-cycle condition limit (right side)

		// validate form "<iter> [<, <=, >, >=] <condition>"
		try {
			
			val conditionalExpression = limitExpr.assignmentExpression.conditionalExpression
			if (conditionalExpression.expression != null || conditionalExpression.conditionalExpression != null) throw new NullPointerException
			
			val conditionalOrExpression = conditionalExpression.conditionalOrExpression
			if (conditionalOrExpression.conditionalOrExpression != null) throw new NullPointerException
			
			val conditionalAndExpression = conditionalOrExpression.conditionalAndExpression
			if (conditionalAndExpression.conditionalAndExpression != null) throw new NullPointerException
			
			val inclusiveOrExpression = conditionalAndExpression.inclusiveOrExpression
			if (inclusiveOrExpression.inclusiveOrExpression != null) throw new NullPointerException

			val exclusiveOrExpression = inclusiveOrExpression.exclusiveOrExpression
			if (exclusiveOrExpression.exclusiveOrExpression != null) throw new NullPointerException

			val andExpression = exclusiveOrExpression.andExpression
			if (andExpression.andExpression != null) throw new NullPointerException

			val equalityExpression = andExpression.equalityExpression
			if (equalityExpression.equalityExpression != null) throw new NullPointerException

			val relationalExpression = equalityExpression.relationalExpression
			// if (relationalExpression.relationalExpression == null) throw new NullPointerException

			val leftSide = relationalExpression.relationalExpression
			if (leftSide == null) throw new NullPointerException
			cond = relationalExpression.shiftExpression
			if (cond == null) throw new NullPointerException

			if (leftSide.getText != iterName) throw new NullPointerException
		} catch {
			case e: NullPointerException => throw new ParseException("Condition left side must contain only iter. variable")
		}

		// INC
		val forUpdate = basicForStatement.forUpdate
		val updateList = forUpdate.statementExpressionList.statementExpression.asScala
		if (updateList.size != 1) throw new ParseException("For incrementation must containt exactly one statement")
		val update = updateList.head

		//postIncrementExpression
		var step: String = null
		var oper: String = null
		if (update.preIncrementExpression != null) {
			if (update.preIncrementExpression.unaryExpression.getText != iterName) throw new ParseException("Iter. variable must be modified")
			oper = "+="
			step = "1"
		} else if (update.postIncrementExpression != null) {
			if (update.postIncrementExpression.postfixExpression.getText != iterName) throw new ParseException("Iter. variable must be modified")
			oper = "+="
			step = "1"
		} else if (update.preDecrementExpression != null) {
			if (update.preDecrementExpression.unaryExpression.getText != iterName) throw new ParseException("Iter. variable must be modified")
			oper = "-="
			step = "1"
		} else if (update.postDecrementExpression != null) {
			if (update.postDecrementExpression.postfixExpression.getText != iterName) throw new ParseException("Iter. variable must be modified")
			oper = "-="
			step = "1"
		} else if (update.assignment != null) {	// assignment
			if (update.assignment.leftHandSide.getText != iterName) throw new ParseException("Iter. variable must be modified")

			// TODO: assignment?
			if (List("+=", "-=") contains update.assignment.assignmentOperator.getText) {
				oper = update.assignment.assignmentOperator.getText
			} else throw new ParseException("Unsupported for-update operation (+=, -=)")

			if (getContextTokens(update.assignment.expression).map(_.getText) contains iterName) throw new ParseException("For-update statement must not reference iterator")
			step = update.assignment.expression.getText
		} else throw new ParseException("Iter. variable must be modified")

		// get names of for-bounds vars
		val initVal     = cc.uniqueName("ompForInit")
		val condVal     = cc.uniqueName("ompForCond")
		val incVal      = cc.uniqueName("ompForInc")
		val cycleLength = cc.uniqueName("ompCycleLength")

		val forVars = "/* OMP for boundaries */\n" +
			s"final int $initVal = ${initExpr.getText};\n" +
			s"final int $condVal = ${cond.getText};\n" +
			s"final int $incVal = $step;\n" + // TODO
			s"final int $cycleLength = Math.abs($condVal - $initVal);\n"

		// TODO: inclusive condition?
		cc.rewriter.insertBefore(cc.ctx.start, forVars)
		cc.rewriter.replace(initExpr.start, initExpr.stop, s"$initVal ${oper.head} ((${cc.iter2} == 0) ? 0 : (($incVal - 1 - ((${cc.iter2} * $cycleLength/${cc.threadCount} - 1) % $incVal)) + (${cc.iter2} * $cycleLength/${cc.threadCount})))")
		cc.rewriter.replace(cond.start, cond.stop, s"$initVal ${oper.head} (${cc.iter2} + 1) * $cycleLength/${cc.threadCount}")
		cc.rewriter.replace(update.start, update.stop, s"$iterName $oper $incVal")
		cc.wrap
	}

	/** Translate "omp sections" */
	private def translateSections(ompCtx: OMPParser.OmpSectionsContext, ctx: Java8Parser.StatementContext, rewriter: TokenStreamRewriter, locals: Set[OMPVariable], params: Set[OMPVariable], captured: Set[OMPVariable], capturedThis: Boolean, currentClass: String) = {
		// TODO
	}

	/** Translate "omp for" */
	private def translateFor(ompCtx: OMPParser.OmpForContext, ctx: Java8Parser.StatementContext, rewriter: TokenStreamRewriter, locals: Set[OMPVariable], params: Set[OMPVariable], captured: Set[OMPVariable], capturedThis: Boolean, currentClass: String) = {
		// TODO
	}
}
