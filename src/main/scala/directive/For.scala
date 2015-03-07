package org.omp4j.directive

import org.antlr.v4.runtime.{TokenStreamRewriter, Token}
import org.omp4j.Config
import org.omp4j.directive.DirectiveSchedule._
import org.omp4j.exception.{ParseException, SyntaxErrorException}
import org.omp4j.grammar.Java8Parser
import org.omp4j.preprocessor.{SingleTranslationVisitor, DirectiveVisitor}
import org.omp4j.tree.{OMPClass, OMPVariable}

case class For(override val parent: Directive, override val publicVars: List[String], override val privateVars: List[String])(implicit threadNum: String, ctx: Java8Parser.StatementContext, cmt: Token, line: Int, conf: Config) extends Directive(parent, publicVars, privateVars)(DirectiveSchedule.Static, threadNum, ctx, cmt, line, conf) with ForCycle {


	// validate existence of omp-parallel parent block
	override def validate(directives: DirectiveVisitor.DirectiveMap) = {
		if (parentOmpParallel == null) throw new SyntaxErrorException("'omp for' in no 'omp parallel [for]' block.")
		super.validate(directives)
	}

	// inherit all
	override lazy val threadCount = parentOmpParallel.threadCount
	override lazy val contextVar = parentOmpParallel.contextVar
	override lazy val executor = parentOmpParallel.executor
	override lazy val contextClass = parentOmpParallel.contextClass
	override lazy val threadArr = parentOmpParallel.threadArr
	override lazy val iter = parentOmpParallel.iter
	override lazy val iter2 = parentOmpParallel.iter2
	override lazy val secondIter = true
	override lazy val exceptionName = parentOmpParallel.exceptionName
	override val executorClass = parentOmpParallel.executorClass

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

		// TODO: rewrite iterName -> finalIterName

		val stv = new SingleTranslationVisitor(rewriter, iterName, finalIterName)
		stv.visit(basicForStatement.forInit)
		stv.visit(basicForStatement.expression)
		stv.visit(basicForStatement.forUpdate)


		rewriter.insertAfter(statement.start, before)
		rewriter.insertBefore(statement.stop, after)


		deleteCmt

	}

}
