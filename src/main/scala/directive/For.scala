package org.omp4j.directive

import org.antlr.v4.runtime.{TokenStreamRewriter, Token}
import org.omp4j.Config
import org.omp4j.directive.DirectiveSchedule._
import org.omp4j.exception.{ParseException, SyntaxErrorException}
import org.omp4j.extractor.Inheritor
import org.omp4j.grammar.Java8Parser
import org.omp4j.preprocessor.{SingleTranslationVisitor, DirectiveVisitor, TranslationVisitor}
import org.omp4j.tree.{OMPClass, OMPVariable}

/** For */
case class For(override val parent: Directive, override val privateVars: List[String], override val firstPrivateVars: List[String])(implicit threadNum: String, ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, privateVars, firstPrivateVars)(DirectiveSchedule.Dynamic, threadNum, ctx, cmt, line, conf) with ForCycle {

	// inherit all
	override lazy val threadCount = if (parentOmpParallel == null) "" else parentOmpParallel.threadCount
	override lazy val executor = if (parentOmpParallel == null) "" else parentOmpParallel.executor
	override lazy val threadArr = if (parentOmpParallel == null) "" else parentOmpParallel.threadArr
	override lazy val iter = if (parentOmpParallel == null) "" else parentOmpParallel.iter
	override lazy val iter2 = if (parentOmpParallel == null) "" else parentOmpParallel.iter2
	override lazy val secondIter = true
	override lazy val exceptionName = if (parentOmpParallel == null) "" else parentOmpParallel.exceptionName
	override val executorClass = if (parentOmpParallel == null) "" else parentOmpParallel.executorClass

	/** Translate directives of type Master, Single */
	override protected def translateChildren(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		super.translateChildren(captured, capturedThis, directiveClass)
		childrenOfType[Master].foreach{_.postTranslate}
		childrenOfType[Single].foreach{_.postTranslate}
		childrenOfType[For].foreach{_.postTranslate(captured, capturedThis, directiveClass)}
	}

	override def postTranslate(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {

		if(parentOmpParallel != null) {

			val basicForStatement = getBasicForStatement(ctx)
			validateBasicForStatement(basicForStatement)
			val (iterName, _) = getInit(basicForStatement)
			val finalIterName = uniqueName(iterName)
			val statement = basicForStatement.statement
			if (statement == null) throw new ParseException(s"Error in directive before line $line: Empty for-cycle body.")

			val stv = new SingleTranslationVisitor(rewriter, iterName, finalIterName)
			stv.visit(basicForStatement.forInit)
			stv.visit(basicForStatement.expression)
			stv.visit(basicForStatement.forUpdate)

			val tv = new TranslationVisitor(rewriter, directiveClass.ompFile, this, parent.contextVar, parent.captured)
			tv.visit(statement)
			val captured = tv.getCaptured
			val capturedThis = tv.getCapturedThis

			val initString = classDeclar(captured, capturedThis, directiveClass) +
					 instance +
					 thatInit(capturedThis) +
					 init(captured, capturedThis)

			val barrierName = uniqueName("forBarrier")
			val execName = uniqueName("forExecutor")

			val preForStart = initString +
					  s"if ($executor.getThreadNum() == 0) {\n" +
					  s"$executorClass $execName = new $executorClass($threadCount);\n"

			val postForStart = s"\tfinal int $iterName = $finalIterName;\n" +
					   s"\t$execName.execute(new Runnable(){\n" +
				           "\t\t@Override\n" +
					   "\t\tpublic void run() {\n"

			val preForEnd = "\t}});\n"

			val postForEnd = s"$execName.waitForExecution();\n" +
					 "} /* if */\n" +
			                 s"""$executor.hitBarrier("$barrierName");\n"""

			rewriter.insertBefore(basicForStatement.start, preForStart)
			rewriter.insertAfter(statement.start, postForStart)
			rewriter.insertBefore(statement.stop, preForEnd)
			rewriter.insertAfter(basicForStatement.stop, postForEnd)

			translateChildren(captured, capturedThis, directiveClass)
		}       // endif

		deleteCmt
	}
}
