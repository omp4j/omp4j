package org.omp4j.preprocessor

import org.antlr.v4.runtime._
import org.omp4j.Config
import org.omp4j.directive.{Sections, Directive, ParallelFor}
import org.omp4j.tree.OMPVariable

/** Context for Translator class */
case class ContextContainer (directive: Directive, locals: Set[OMPVariable], params: Set[OMPVariable], captured: Set[OMPVariable], capturedThis: Boolean, currentClass: String)(implicit conf: Config) {

	/** Shortcut */
	lazy val ctx = directive.ctx

	/** [Shortcut] Number of threads */
	lazy val threadCount = "(4)"	// TODO: thread count

	/** [Shortcut] Context variable name */
	lazy val contextVar = directive.contextVar

	/** [Shortcut] Context class name */
	lazy val contextClass = directive.contextClass

	/** [Shortcut] Thread array name*/
	lazy val threadArr = directive.threadArr

	/** [Shortcut] 1. iterator name */
	lazy val iter = directive.iter

	/** [Shortcut] 2. iterator name */
	lazy val iter2 = directive.iter2

	/** [Shortcut] exception name */
	lazy val exceptionName = directive.exceptionName

	lazy val secondIter = directive match {
		case _: ParallelFor => true
		case _: Sections => true
		case _ => false
	}

	/** Initialization of 2. iterator */
	private lazy val secondIterInit = if (secondIter) s"\tfinal int $iter2 = $iter;\n" else ""

	/** Declaration of THAT (captured this) */
	private lazy val thatDecl = if (capturedThis) s"\tpublic $currentClass THAT;\n" else ""

	/** Class declaration */
	lazy val classDeclar =
		"/* === OMP CONTEXT === */\n" + 
		s"class $contextClass {\n" + 
			(for {c <- captured} yield s"\tpublic ${c.varType} ${c.fullName};\n").toList.mkString +
			thatDecl + 
		"}\n"

	/** Instance of context class */
	lazy val instance = s"final $contextClass $contextVar = new ${contextClass}();\n"

	/** THAT initialization*/
	private lazy val thatInit = if (capturedThis) s"$contextVar.THAT = this;\n" else ""

	/** Initialization of captured variables + THAT */
	lazy val init = thatInit + (for {c <- captured} yield s"$contextVar.${c.fullName} = ${c.name};\n").toList.mkString

	/** Top part of thread wrap */
	lazy val threadsBegin   =
		"/* === /OMP CONTEXT === */\n" +
		s"Thread $threadArr[] = new Thread[$threadCount];\n" + 
		s"for (int $iter = 0; $iter < $threadCount; ${iter}++) {\n" + 
			secondIterInit + 
			s"\t${threadArr}[$iter] = new Thread(new Runnable(){\n" + 
			"\t\t@Override\n" + 
			"\t\tpublic void run() {\n"

	/** Bottom part of thread wrap */
	lazy val threadsEnd     =
			"\t\t}\n" +
			"\t});\n" +
			s"\t${threadArr}[$iter].start();\n"+
		"}\n" +
		"try {\n" + 
		s"\tfor (int $iter = 0; $iter < $threadCount; ${iter}++) {\n" + 
		s"\t\t ${threadArr}[$iter].join();\n" +
		"\t}\n" + 
		"} catch (InterruptedException $exceptionName) {\n"+
		"\tSystem.out.println(\"omp4j: interrupted exception\");\n" + 
		"\tSystem.exit(1);\n" +
		"}\n"

	/** Java8 value types*/
	private lazy val primitiveDataTypes = List("boolean", "byte", "short", "int", "long", "char", "float", "double")

	/** Assing primitive values */
	lazy val primitiveAssigments = (for {c <- captured if (primitiveDataTypes contains c.varType)} yield s"\t${c.name} = $contextVar.${c.fullName};\n").toList.mkString

	/** Code to be prepended */
	lazy val toPrepend = classDeclar + instance + init + threadsBegin

	/** Code to be appended*/
	lazy val toAppend  = threadsEnd + primitiveAssigments

	/** Modify code according to toPrepend and toAppend */
	def wrap(rewriter: TokenStreamRewriter) = {
		rewriter.insertBefore(directive.ctx.start, toPrepend)
		rewriter.insertAfter(directive.ctx.stop, toAppend)
	}
}
