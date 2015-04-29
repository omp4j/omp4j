package org.omp4j.directive

import org.antlr.v4.runtime.TokenStreamRewriter
import org.antlr.v4.runtime.tree.SyntaxTree
import org.omp4j.exception.ParseException
import org.omp4j.extractor.{FirstLevelContinueExtractor, FirstLevelBreakExtractor}
import org.omp4j.grammar.Java8Parser
import scala.collection.JavaConverters._

/** Trait implementing for-loop operations */
trait ForCycle {

	/** Abstract uniqueName method */
	def uniqueName(baseName: String): String

	/** Apply the rewriter
	  *
	  * @param ctx AST context
	  * @param rewriter ANTLR rewriter
	  * @return text after rewriter is applied
	  */
	protected def getRewrittenText(ctx: SyntaxTree)(implicit rewriter: TokenStreamRewriter): String

	/** Extract basicForStatement or throw ParseException giving the reason of failure
	  *
	  * @param ctx statement
	  * @return basicForStatement
	  * @throws ParseException if basicForStatement couldn't be extracted
	 */
	def getBasicForStatement(ctx: Java8Parser.StatementContext) = {
		val forStatement = ctx.forStatement
		if (forStatement == null) throw new ParseException("For directive before non-for statement")
		val basicForStatement = forStatement.basicForStatement
		if (basicForStatement == null) throw new ParseException("For directive before enhanced for statement")

		basicForStatement
	}

	/** Check whether the statement is break/continue-free
	 *
	 * @param basicForStatement basicForStatement
	 * @throws ParseException if break or continue is presented
	 */
	def validateBasicForStatement(basicForStatement: Java8Parser.BasicForStatementContext) = {
		if ((new FirstLevelBreakExtractor ).visit(basicForStatement).size > 0) throw new ParseException("Break statements are not allowed")
		if ((new FirstLevelContinueExtractor ).visit(basicForStatement).size > 0) throw new ParseException("Continue statements are not allowed")
	}

	/** Extract init. part of the for-loop
	  *
	  * @param basicForStatement basicForStatement
	  * @param rewriter ANTLR rewriter
	  * @return tuple of iterator name and for-loop init. context
	  * @throws ParseException if init. couldn't be extracted
	  */
	def getInit(basicForStatement: Java8Parser.BasicForStatementContext)(implicit rewriter: TokenStreamRewriter) = {
		// INIT
		val forInit = basicForStatement.forInit
		if (forInit == null) throw new ParseException("For directive before enhanced for statement")
		val forList = forInit.localVariableDeclaration.variableDeclaratorList.variableDeclarator.asScala
		if (forList.size != 1) throw new ParseException("For initialization must containt exactly one variable")
		val iterName = getRewrittenText(forList.head.variableDeclaratorId.Identifier) //.getText	// var name
		val initExpr = forList.head.variableInitializer.expression	// right side of init assignment

		(iterName, initExpr)
	}

	/** Extract update part of the for-loop
	  *
	  * @param basicForStatement basicForStatement
	  * @return for-loop update context
	  * @throws ParseException if update context doesn't meet the requirements
	  */
	def getUpdate(basicForStatement: Java8Parser.BasicForStatementContext) = {
		val forUpdate = basicForStatement.forUpdate
		val updateList = forUpdate.statementExpressionList.statementExpression.asScala
		if (updateList.size != 1) throw new ParseException("For incrementation must containt exactly one statement")
		val update = updateList.head

		update
	}

	/** Translate the for-loop.
	  *
	  * Make for-loop run in parallel given the number of threads.
	  *
	  * @param iter2 thread-iterator variable name
	  * @param threadCount number of threads
	  * @param ctx ANTLR context
	  * @param rewriter ANTLR rewriter
	  * @throws ParseException if an error occurs
	  */
	def translateFor(iter2: String, threadCount: String)(implicit ctx: Java8Parser.StatementContext, rewriter: TokenStreamRewriter) = {

		val basicForStatement = getBasicForStatement(ctx)
		validateBasicForStatement(basicForStatement)

		// init
		val (iterName, initExpr) = getInit(basicForStatement)

		// cond
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
			case e: NullPointerException => throw new ParseException("Condition left side must contain only iter. variable", e)
		}

		// INC
		val update = getUpdate(basicForStatement)

		//postIncrementExpression
		var step: String = null
		var oper: String = null
		if (update.preIncrementExpression != null) {
			if (getRewrittenText(update.preIncrementExpression.unaryExpression) != iterName) throw new ParseException("Iter. variable must be modified")
			oper = "+="
			step = "1"
		} else if (update.postIncrementExpression != null) {
			if (getRewrittenText(update.postIncrementExpression.postfixExpression) != iterName) throw new ParseException("Iter. variable must be modified")
			oper = "+="
			step = "1"
		} else if (update.preDecrementExpression != null) {
			if (getRewrittenText(update.preDecrementExpression.unaryExpression) != iterName) throw new ParseException("Iter. variable must be modified")
			oper = "-="
			step = "1"
		} else if (update.postDecrementExpression != null) {
			if (getRewrittenText(update.postDecrementExpression.postfixExpression) != iterName) throw new ParseException("Iter. variable must be modified")
			oper = "-="
			step = "1"
		} else if (update.assignment != null) {	// assignment
			if (getRewrittenText(update.assignment.leftHandSide) != iterName) throw new ParseException("Iter. variable must be modified")

			// TODO: assignment?
			if (List("+=", "-=") contains getRewrittenText(update.assignment.assignmentOperator)) {
				oper = getRewrittenText(update.assignment.assignmentOperator)
			} else throw new ParseException("Unsupported for-update operation (+=, -=)")

			if (getRewrittenText(update.assignment.expression) contains iterName) throw new ParseException("For-update statement must not reference iterator")
			step = getRewrittenText(update.assignment.expression)
		} else throw new ParseException("Iter. variable must be modified")

		// get names of for-bounds vars
		val initVal     = uniqueName("ompForInit")
		val condVal     = uniqueName("ompForCond")
		val incVal      = uniqueName("ompForInc")
		val cycleLength = uniqueName("ompCycleLength")

		val forVars = "/* OMP for boundaries */\n" +
			s"final int $initVal = ${getRewrittenText(initExpr)};\n" +
			s"final int $condVal = ${getRewrittenText(cond)};\n" +
			s"final int $incVal = $step;\n" + // TODO
			s"final int $cycleLength = Math.abs($condVal - $initVal);\n"

		// TODO: inclusive condition?
		rewriter.insertBefore(ctx.start, forVars)
		rewriter.replace(initExpr.start, initExpr.stop, s"$initVal ${oper.head} (($iter2 == 0) ? 0 : (($incVal - 1 - (($iter2 * $cycleLength/$threadCount - 1) % $incVal)) + ($iter2 * $cycleLength/$threadCount)))")
		rewriter.replace(cond.start, cond.stop, s"$initVal ${oper.head} ($iter2 + 1) * $cycleLength/$threadCount")
		rewriter.replace(update.start, update.stop, s"$iterName $oper $incVal")

	}
}
