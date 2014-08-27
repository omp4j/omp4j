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

/** Context for Translator class */
case class ContextContainer (ompCtx: OMPParser.OmpParallelContext, ctx: Java8Parser.StatementContext, rewriter: TokenStreamRewriter, locals: Set[OMPVariable], params: Set[OMPVariable], captured: Set[OMPVariable], capturedThis: Boolean, currentClass: String, secondIter: Boolean){
	/** Number of threads */
	lazy val threadCount    = "(4)"	// TODO

	/** Context variable name */
	lazy val contextVar     = uniqueName("ompContext")

	/** Context class name */
	lazy val contextClass   = uniqueName("OMPContext")

	/** Thread array name*/
	lazy val threadArr      = uniqueName("ompThreads")

	/** 1. iterator name */
	lazy val iter           = uniqueName("ompI")

	/** 2. iterator name */
	lazy val iter2          = uniqueName("ompJ")

	/** Initialization of 2. iterator */
	private lazy val secondIterInit = if (secondIter) s"\tfinal int $iter2 = $iter;\n" else ""


	/** Declaration of THAT (captured this) */
	private lazy val thatDecl = if (capturedThis) s"\tpublic $currentClass THAT;\n" else ""

	/** Class declaration */
	lazy val classDeclar    = 
		"/* === OMP CONTEXT === */\n" + 
		s"class $contextClass {\n" + 
			(for {c <- captured} yield s"\tpublic ${c.varType} ${c.meaning}_${c.name};\n").toList.mkString + 
			thatDecl + 
		"}\n"

	/** Instance of context class */
	lazy val instance       = s"final $contextClass $contextVar = new ${contextClass}();\n"

	/** THAT initialization*/
	private lazy val thatInit = if (capturedThis) s"$contextVar.THAT = this;\n" else ""

	/** Initialization of captured variables + THAT */
	lazy val init           = thatInit + (for {c <- captured} yield s"$contextVar.${c.meaning}_${c.name} = ${c.name};\n").toList.mkString 

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
		"} catch (InterruptedException e) {\n"+
		"\tSystem.out.println(\"omp4j: interrupted exception\");\n" + 
		"\tSystem.exit(1);\n" +
		"}"

	/** Code to be prepended */
	lazy val toPrepend = classDeclar + instance + init + threadsBegin

	/** Code to be appended*/
	lazy val toAppend  = threadsEnd

	/** Modify code according to toPrepend and toAppend */
	def wrap = {
		rewriter.insertBefore(ctx.start, toPrepend)
		rewriter.insertAfter(ctx.stop, toAppend)
	}

	/** Create unique name for variable, based on text given*/
	def uniqueName(baseName: String): String = {
	// TODO: check tokens
	// TODO: check reflection
		baseName
	}

}
