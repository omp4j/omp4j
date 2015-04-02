package org.omp4j.directive

import org.antlr.v4.runtime.{TokenStreamRewriter, Token}
import org.omp4j.Config
import org.omp4j.directive.DirectiveSchedule._
import org.omp4j.exception.{ParseException, SyntaxErrorException}
import org.omp4j.extractor.Inheritor
import org.omp4j.grammar.Java8Parser
import org.omp4j.preprocessor.{SingleTranslationVisitor, DirectiveVisitor, TranslationVisitor}
import org.omp4j.tree.{OMPClass, OMPVariable}

case class For(override val parent: Directive, override val privateVars: List[String], override val firstPrivateVars: List[String])(implicit threadNum: String, ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, privateVars, firstPrivateVars)(DirectiveSchedule.Static, threadNum, ctx, cmt, line, conf) with ForCycle {


	// validate existence of omp-parallel parent block
	override def validate(directives: DirectiveVisitor.DirectiveMap) = {
		if (parentOmpParallel == null) throw new SyntaxErrorException("'omp for' in no 'omp parallel [for]' block.")
		super.validate(directives)
	}

	// inherit all
	override lazy val threadCount = parentOmpParallel.threadCount
//	override lazy val contextVar = parentOmpParallel.contextVar
	override lazy val executor = parentOmpParallel.executor
//	override lazy val contextClass = parentOmpParallel.contextClass
	override lazy val threadArr = parentOmpParallel.threadArr
	override lazy val iter = parentOmpParallel.iter
	override lazy val iter2 = parentOmpParallel.iter2
	override lazy val secondIter = true
	override lazy val exceptionName = parentOmpParallel.exceptionName
	override val executorClass = parentOmpParallel.executorClass

	/** Translate directives of type Master, Single */
	override protected def translateChildren(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		super.translateChildren(captured, capturedThis, directiveClass)
		childrenOfType[Master].foreach{_.postTranslate}
		childrenOfType[Single].foreach{_.postTranslate}
		childrenOfType[For].foreach{_.postTranslate(captured, capturedThis, directiveClass)}
	}

	override def postTranslate(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		//translateFor(iter2, threadCount)
//		wrap(rewriter)(captured, capturedThis, directiveClass)

		val basicForStatement = getBasicForStatement(ctx)
		validateBasicForStatement(basicForStatement)
		val (iterName, _) = getInit(basicForStatement)
		val finalIterName = uniqueName(iterName)
		val statement = basicForStatement.statement

		if (statement == null) throw new ParseException("Empty for-cycle body.")

		val before =
			s"\n\tfinal int $iterName = $finalIterName;\n" +
			s"\t$executor.execute(new Runnable(){\n" +
			"\t\t@Override\n" +
			"\t\tpublic void run() {\n"

		val after = "\t}});\n"

		val stv = new SingleTranslationVisitor(rewriter, iterName, finalIterName)
		stv.visit(basicForStatement.forInit)
		stv.visit(basicForStatement.expression)
		stv.visit(basicForStatement.forUpdate)

		val tv = new TranslationVisitor(rewriter, directiveClass.ompFile, this, parent.contextVar, parent.captured)
		tv.visit(statement)
		val captured = tv.getCaptured
		val capturedThis = tv.getCapturedThis


		val initString =
			classDeclar(captured, capturedThis, directiveClass) +
			instance +
			thatInit(capturedThis) +
			init(captured, capturedThis)
		rewriter.insertBefore(ctx.start, initString)

		rewriter.insertAfter(statement.start, before)
		rewriter.insertBefore(statement.stop, after)

		deleteCmt
		translateChildren(captured, capturedThis, directiveClass)
	}
}
