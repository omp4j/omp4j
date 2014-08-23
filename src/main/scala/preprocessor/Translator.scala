package org.omp4j.preprocessor

import scala.io.Source
import scala.util.control.Breaks._
import scala.collection.JavaConverters._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.extractor._
import org.omp4j.preprocessor.grammar._

/** Translate context given with respect to directives */
class Translator(tokens: TokenStream, parser: Java8Parser, directives: List[Directive], ompFile: OMPFile)(implicit conf: Config) {

	/** Java8Parser.LocalVariableDeclarationContext typedef */
	type LVDC = Java8Parser.LocalVariableDeclarationContext

	/** Get sequence of all (in)direct parents of tree given
	  * @param pt Tree whose parents are about to be fetched
	  * @return Set of trees
	  */
	def getParentList(t: ParseTree): Seq[ParseTree] = {
		if (t == null) Seq[ParseTree]()
		else getParentList(t.getParent()) :+ t
	}

	/** Get set of variables (their declarations) whose can be reffered
	  * but are not declared in the tree given
	  * @param pt Tree whose variable are about to be fetched
	  * @return Set of variables
	  */
	def getPossiblyInheritedLocals(pt: ParseTree): Set[OMPVariable] = {
		// TODO: move to visitor
		// result set - TODO: rewrite more functionally
		var result = Set[OMPVariable]()
		val neck = getParentList(pt)	// list of parent

		// iterate through the list of tuples (tree-node, follower-in-neck)
		for {(t, follower) <- (neck zip neck.tail)} {
			// println("visiting:\t" + t)
			breakable {
				// iterate through all children left to the follower
				for {i <- 0 until t.getChildCount()} {
					val child = t.getChild(i)
					if (child == follower) break
					result = result ++ (new FirstLevelLocalVariableExtractor ).visit(child)
				}
			}
		}
		result
	}

	/** Get set of method parameters that can be reffered
	  * @param pt Tree whose params are about to be fetched
	  * @return Set of variables
	  */
	def getPossiblyInheritedParams(pt: ParseTree): Set[OMPVariable] = {

		type MDC = Java8Parser.MethodDeclarationContext

		// result set - TODO: rewrite more functionally
		var result = Set[OMPVariable]()
		val neck = getParentList(pt)	// list of parent

		// TODO: local classes?
		neck.foreach{ n =>
			try {
				val method: MDC = n.asInstanceOf[MDC]
				val list = method.methodHeader().methodDeclarator().formalParameterList()

				// add non-last
				if (list != null) {
					val firsts = list.formalParameters	// TODO: receiver??
					if (firsts != null) {
						firsts.formalParameter.asScala.foreach{ p =>
							result += new OMPVariable(p.variableDeclaratorId().Identifier().getText(), p.unannType().getText())
						}
					}

					val last = list.lastFormalParameter.formalParameter
					if (last != null) {
						result += new OMPVariable(last.variableDeclaratorId().Identifier().getText(), last.unannType().getText())
					}
				}
			} catch {
				case e: ClassCastException => ;
			}
		}
		result
	}

	/** Get set of variables (their declarations) that are declared directly
	  * in the tree given
	  * @param pt Tree whose variable are about to be fetched
	  * @return Set of variables
	  */
	def getLocals(t: ParseTree): Set[LVDC] = (new LocalVariableExtractor ).visit(t)


	def translate(directive: Directive, rewriter: TokenStreamRewriter, locals: Set[OMPVariable], params: Set[OMPVariable], captured: Set[OMPVariable], capturedThis: Boolean, currentClass: String) = {
		if      (directive.ompCtx.ompParallel()    != null) translateParallel(directive.ompCtx.ompParallel(), directive.ctx, rewriter, locals, params, captured, capturedThis, currentClass)
		else if (directive.ompCtx.ompParallelFor() != null) translateParallelFor(directive.ompCtx.ompParallelFor(), directive.ctx, rewriter, locals, params, captured, capturedThis, currentClass)
		else if (directive.ompCtx.ompSections()    != null) translateSections(directive.ompCtx.ompSections(), directive.ctx, rewriter, locals, params, captured, capturedThis, currentClass)
		else if (directive.ompCtx.ompFor()         != null) translateFor(directive.ompCtx.ompFor(), directive.ctx, rewriter, locals, params, captured, capturedThis, currentClass)
		else throw new IllegalArgumentException("Unsupported directive")
		rewriter.replace(directive.cmt, "\n")
	}

	private def translateParallel(ompCtx: OMPParser.OmpParallelContext, ctx: Java8Parser.StatementContext, rewriter: TokenStreamRewriter, locals: Set[OMPVariable], params: Set[OMPVariable], captured: Set[OMPVariable], capturedThis: Boolean, currentClass: String) = {
		val contextVar = uniqueContextVarName(rewriter)
		val contextClass = uniqueContextClassName(rewriter)
		val threadArr = uniqueThreadArrName(rewriter)
		val iter = uniqueIteratorName(rewriter)

		val thatDecl = if (capturedThis) s"public $currentClass THAT;\n" else ""
		val thatInit = if (capturedThis) s"$contextVar.THAT = this;\n" else ""

		val toPrepend =
			"/* === OMP CONTEXT === */\n" + 
			"class " + contextClass + " {\n" + 
				(for {c <- captured} yield "\tpublic " + c.varType + " " + c.meaning + "_" + c.name + ";\n").toList.mkString + 
				thatDecl + 
			"}\n" +
			"final " + contextClass + " " + contextVar + " = new " + contextClass + "();\n" + 
			thatInit + 
			(for {c <- captured} yield s"$contextVar.${c.meaning}_${c.name} = ${c.name};\n").toList.mkString + 
			"/* === /OMP CONTEXT === */\n" +
			"Thread " + threadArr + "[] = new Thread[4];\n" + 
			"for (int " + iter + " = 0; " + iter + " < 4; " + iter + "++) {\n" + 
				"\t" + threadArr + "[" + iter + "] = new Thread(new Runnable(){\n" + 
				"\t\t@Override\n" + 
				"\t\tpublic void run() {\n"

		val toAppend = 
				"\t\t}\n" +
				"\t});\n" +
				"\t" + threadArr + "[" + iter + "].start();"+
			"}\n" +
			"try {\n" + 
			"\tfor (int " + iter + " = 0; " + iter + " < 4; " + iter + "++) {\n" + 
			"\t\t" + threadArr + "[" + iter + "].join();\n" +
			"\t}\n" + 
			"} catch (InterruptedException e) {\n"+
			"\tSystem.out.println(\"omp4j: interrupted exception\");\n" + 
			"\tSystem.exit(1);\n" +
			"}"

		rewriter.insertBefore(ctx.start, toPrepend)
		rewriter.insertAfter(ctx.stop, toAppend)
	}

	private def translateParallelFor(ompCtx: OMPParser.OmpParallelForContext, ctx: Java8Parser.StatementContext, rewriter: TokenStreamRewriter, locals: Set[OMPVariable], params: Set[OMPVariable], captured: Set[OMPVariable], capturedThis: Boolean, currentClass: String) = {
		val contextVar = uniqueContextVarName(rewriter)
		val contextClass = uniqueContextClassName(rewriter)
		val threadArr = uniqueThreadArrName(rewriter)
		val iter = uniqueIteratorName(rewriter)
		val iter2 = "ompJ"	// TODO

		val threadCount = "(4)"

		val thatDecl = if (capturedThis) s"public $currentClass THAT;\n" else ""
		val thatInit = if (capturedThis) s"${contextVar}.THAT = this;\n" else ""

		val toPrepend =
			"/* === OMP CONTEXT === */\n" + 
			s"class $contextClass {\n" + 
				(for {c <- captured} yield s"\tpublic ${c.varType} ${c.meaning}_${c.name};\n").toList.mkString + 
				thatDecl + 
			"}\n" +
			s"final $contextClass $contextVar = new ${contextClass}();\n" + 
			thatInit + 
			(for {c <- captured} yield s"$contextVar.${c.meaning}_${c.name} = ${c.name};\n").toList.mkString + 
			"/* === /OMP CONTEXT === */\n" +
			s"Thread $threadArr[] = new Thread[$threadCount];\n" + 
			s"for (int $iter = 0; $iter < $threadCount; ${iter}++) {\n" + 
				s"\tfinal int $iter2 = $iter;\n" +
				s"\t${threadArr}[$iter] = new Thread(new Runnable(){\n" + 
				"\t\t@Override\n" + 
				"\t\tpublic void run() {\n"

		val toAppend = 
				"\t\t}\n" +
				"\t});\n" +
				s"\t${threadArr}[$iter].start();\n"+
			"}\n" +
			"try {\n" + 
			s"\tfor (int $iter = 0; $iter < $threadCount; ${iter}++) {\n" + 
			s"\t\t ${threadArr}[$iter].join();\n" +
			"\t}\n" + 
			"} catch (InterruptedException e) {\n"+
			"\tSystem.out.println(\"omp4j: interrupted exception\");\n" + 
			"\tSystem.exit(1);\n" +
			"}"

		// TODO: multistep inc.
		// TODO: banish break/continue!
		// rewrite for

		val forStatement = ctx.forStatement()
		if (forStatement == null) throw new ParseException("For directive before non-for statement")
		val basicForStatement = forStatement.basicForStatement
		if (forStatement.basicForStatement == null) throw new ParseException("For directive before enhanced for statement")


		val forInit = basicForStatement.forInit()
		if (forInit == null) throw new ParseException("For directive before enhanced for statement")
		val forUpdate = basicForStatement.forUpdate()
		// TODO: if more, deal only with it. var.
		if (forUpdate == null) throw new ParseException("For directive before enhanced for statement or missing it. variable update")

		// println(forUpdate.expressionList.expression(0).toStringTree(parser))
		// println(forUpdate.expressionList.expression(0).expression(0).toStringTree(parser))
		// println(forUpdate.expressionList.expression(0).expression(1).toStringTree(parser))

		// println(forUpdate.expressionList.expression(0).superSuffix.toStringTree(parser))
		// println(forUpdate.expressionList.expression(0).expression(1).toStringTree(parser))

		// if...

		val initExpr = forInit.localVariableDeclaration().variableDeclaratorList().variableDeclarator(0).variableInitializer().expression()
		val limitExpr = basicForStatement.expression()
		val cond = limitExpr.assignmentExpression.conditionalExpression
		val N = s"((${cond.getText()}) - (${initExpr.getText()}))"

		rewriter.replace(initExpr.start, initExpr.stop,
			s"(${initExpr.getText()}) + ($iter2 * $N/$threadCount)")
		rewriter.replace(cond.start, cond.stop,
			s"(${initExpr.getText()}) + ($iter2 + 1) * $N/$threadCount")

		rewriter.insertBefore(ctx.start, toPrepend)
		rewriter.insertAfter(ctx.stop, toAppend)
	}

	private def translateSections(ompCtx: OMPParser.OmpSectionsContext, ctx: Java8Parser.StatementContext, rewriter: TokenStreamRewriter, locals: Set[OMPVariable], params: Set[OMPVariable], captured: Set[OMPVariable], capturedThis: Boolean, currentClass: String) = {
		// TODO
	}

	private def translateFor(ompCtx: OMPParser.OmpForContext, ctx: Java8Parser.StatementContext, rewriter: TokenStreamRewriter, locals: Set[OMPVariable], params: Set[OMPVariable], captured: Set[OMPVariable], capturedThis: Boolean, currentClass: String) = {
		// TODO
	}

	// TODO
	def uniqueContextClassName(rewriter: TokenStreamRewriter) = "OMPContext"
	def uniqueContextVarName(rewriter: TokenStreamRewriter) = "ompContext"
	def uniqueThreadArrName(rewriter: TokenStreamRewriter) = "ompThreads"
	def uniqueIteratorName(rewriter: TokenStreamRewriter) = "ompI"
}
