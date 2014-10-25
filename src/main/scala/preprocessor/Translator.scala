package org.omp4j.preprocessor

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree._
import org.omp4j.Config
import org.omp4j.directive._
import org.omp4j.exception._
import org.omp4j.extractor._
import org.omp4j.grammar._
import org.omp4j.tree._

import scala.collection.JavaConverters._

/** Translate context given with respect to directives */
class Translator(rewriter: TokenStreamRewriter, parser: Java8Parser, directives: DirectiveVisitor.DirectiveMap, ompFile: OMPFile)(implicit conf: Config) {

	/** Get tokens matching to context given
	  */
	def getContextTokens(ctx: SyntaxTree): List[Token] = {
		val interval = ctx.getSourceInterval
		val tokenStream = rewriter.getTokenStream

		val toks = for {i <- interval.a to interval.b} yield tokenStream.get(i)
		toks.toList
	}

	/** Wrapper of TokenStreamRewriter.getText(SyntaxTree) until it is officially supported */
	private def getRewrittenText(ctx: SyntaxTree) = rewriter.getText(ctx.getSourceInterval)

	/** Translate directive given using special methods for each directive type */
	def translate(directive: Directive, locals: Set[OMPVariable], params: Set[OMPVariable], captured: Set[OMPVariable], capturedThis: Boolean, currentClass: String) = {
		directive match {
			case Parallel(_,_,_) => translateParallel(new ContextContainer(directive, locals, params, captured, capturedThis, currentClass))
			case ParallelFor(_,_,_) => translateParallelFor(new ContextContainer(directive, locals, params, captured, capturedThis, currentClass))
			case For(_,_,_) => translateFor(new ContextContainer(directive, locals, params, captured, capturedThis, currentClass))
			case secs: Sections => translateSections(secs, new ContextContainer(directive, locals, params, captured, capturedThis, currentClass))
			case Section(_) => ;
			case _ => throw new IllegalArgumentException("Unsupported directive")
		}
		rewriter.replace(directive.cmt, "\n")
	}

	/** Translate "omp parallel" */
	private def translateParallel(cc: ContextContainer) = {
		cc.wrap(rewriter)
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
		val iterName = getRewrittenText(forList.head.variableDeclaratorId.Identifier) //.getText	// var name
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
		val initVal     = cc.directive.uniqueName("ompForInit")
		val condVal     = cc.directive.uniqueName("ompForCond")
		val incVal      = cc.directive.uniqueName("ompForInc")
		val cycleLength = cc.directive.uniqueName("ompCycleLength")

		val forVars = "/* OMP for boundaries */\n" +
			s"final int $initVal = ${getRewrittenText(initExpr)};\n" +
			s"final int $condVal = ${getRewrittenText(cond)};\n" +
			s"final int $incVal = $step;\n" + // TODO
			s"final int $cycleLength = Math.abs($condVal - $initVal);\n"

		// TODO: inclusive condition?
		rewriter.insertBefore(cc.ctx.start, forVars)
		rewriter.replace(initExpr.start, initExpr.stop, s"$initVal ${oper.head} ((${cc.iter2} == 0) ? 0 : (($incVal - 1 - ((${cc.iter2} * $cycleLength/${cc.threadCount} - 1) % $incVal)) + (${cc.iter2} * $cycleLength/${cc.threadCount})))")
		rewriter.replace(cond.start, cond.stop, s"$initVal ${oper.head} (${cc.iter2} + 1) * $cycleLength/${cc.threadCount}")
		rewriter.replace(update.start, update.stop, s"$iterName $oper $incVal")
		cc.wrap(rewriter)
	}

	/** Translate "omp sections" */
	private def translateSections(secs: Sections, cc: ContextContainer) = {

		def translateSection(id: Int, s: Section): Unit = {
			rewriter.insertBefore(s.ctx.start, s"if (${cc.iter2} == $id) {\n")
			if (id > 0) rewriter.insertBefore(s.ctx.start, "else ")
			rewriter.insertAfter(s.ctx.stop, "}\n")

			rewriter.delete(s.cmt)
		}

		val sections = secs.sections
		for {i <- 0 until sections.size} {
			translateSection(i, sections(i))
		}
		cc.wrap(rewriter)
	}


	/** Translate "omp for" */
	private def translateFor(cc: ContextContainer) = {
		// TODO: omp for
	}
}
