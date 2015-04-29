package org.omp4j.directive

import org.antlr.v4.runtime.{TokenStreamRewriter, Token}
import org.omp4j.Config
import org.omp4j.directive.DirectiveSchedule._
import org.omp4j.exception.ParseException
import org.omp4j.grammar.Java8Parser
import org.omp4j.preprocessor.{TranslationVisitor, SingleTranslationVisitor}
import org.omp4j.tree.{OMPClass, OMPVariable}

/** Parallel-for directive */
case class ParallelFor(override val parent: Directive, override val privateVars: List[String], override val firstPrivateVars: List[String])(implicit schedule: DirectiveSchedule, threadNum: String, ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, privateVars, firstPrivateVars) with ForCycle with LockMemory {
	override val parentOmpParallel = this
	override lazy val secondIter = true
	override def addAtomicBool(baseName: String) = super[LockMemory].addAtomicBool(baseName)

	/** Translate directives of type Master, Single */
	override protected def translateChildren(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		super.translateChildren(captured, capturedThis, directiveClass)
		childrenOfType[Master].foreach{_.postTranslate}
		childrenOfType[Single].foreach{_.postTranslate}
		childrenOfType[For].foreach{_.postTranslate(captured, capturedThis, directiveClass)}
	}

	override protected def postTranslate(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {

		// TODO: privates

		val basicForStatement = getBasicForStatement(ctx)
		validateBasicForStatement(basicForStatement)
		val (iterName, _) = getInit(basicForStatement)
		val finalIterName = uniqueName(iterName)
		val statement = basicForStatement.statement
		if (statement == null) throw new ParseException("Empty for-cycle body.")

		val stv = new SingleTranslationVisitor(rewriter, iterName, finalIterName)
		stv.visit(basicForStatement.forInit)
		stv.visit(basicForStatement.expression)
		stv.visit(basicForStatement.forUpdate)

		val preForStart = classDeclar(captured, capturedThis, directiveClass) +
				  instance +
				  init(captured, capturedThis) +
				  s"final org.omp4j.runtime.IOMPExecutor $executor = new $executorClass($threadCount);\n" +
				  "/* === /OMP CONTEXT === */\n" +
				  initPrivates(captured)



		val postForStart = s"\tfinal int $iterName = $finalIterName;\n" +
			s"\t$executor.execute(new Runnable(){\n" +
			"\t\t@Override\n" +
			"\t\tpublic void run() {\n"

		val preForEnd = "\t}});\n"

		val postForEnd = s"$executor.waitForExecution();\n"

		rewriter.insertBefore(basicForStatement.start, preForStart)
		rewriter.insertAfter(statement.start, postForStart)
		rewriter.insertBefore(statement.stop, preForEnd)
		rewriter.insertAfter(basicForStatement.stop, postForEnd)
	}

}
