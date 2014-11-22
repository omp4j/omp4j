package org.omp4j.directive

import org.antlr.v4.runtime.tree.SyntaxTree
import org.omp4j.Config
import org.omp4j.grammar.OMPParser.OmpCriticalContext
import org.omp4j.preprocessor.TranslationVisitor
import org.omp4j.tree.{OMPClass, OMPVariable, OMPFile}
import org.omp4j.utils.Keywords

import scala.collection.mutable.{ListBuffer, HashSet, SynchronizedSet}
import org.antlr.v4.runtime.{TokenStreamRewriter, ParserRuleContext, Token}
import org.omp4j.directive.DirectiveSchedule._
import org.omp4j.exception._
import org.omp4j.grammar._


import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import scala.util.Random
import scala.collection.JavaConverters._

/** Abstract omp directive class; implemented by several case classes */
abstract class Directive(val parent: Directive, val publicVars: List[String], val privateVars: List[String])(implicit val schedule: DirectiveSchedule, val ctx: Java8Parser.StatementContext, val cmt: Token, val line: Int, conf: Config) {

	/** Closest omp-parallel directive or null if none exists */
	val parentOmpParallel: Directive = parent match {
		case null => null
		case _ => parent.parentOmpParallel
	}

	// constructor
	validate()
	parent match {
		case null => ;
		case _ => parent.registerChild(this)
	}

	/** [Shortcut] Number of threads */
	lazy val threadCount = "4"	// TODO: thread count

	/** Context variable name */
	lazy val contextVar = uniqueName("ompContext")

	/** Context variable name */
	lazy val executor = uniqueName("ompExecutor")

	/** Context class name */
	lazy val contextClass = uniqueName("OMPContext")

	/** Thread array name */
	lazy val threadArr = uniqueName("ompThreads")

	/** 1. iterator name */
	lazy val iter = uniqueName("ompI")

	/** 2. iterator name */
	lazy val iter2 = uniqueName("ompJ")

	/** Is second iterator used? */
	lazy val secondIter = false

	/** exception name */
	lazy val exceptionName = uniqueName("ompE")

	val executorClass = schedule match {
		case Dynamic => "org.omp4j.runtime.DynamicExecutor"
		case Static  => "org.omp4j.runtime.StaticExecutor"
	}

	/** Directly nested directives builder */
	private var childrenBuff = ListBuffer[Directive]()

	/** Register child */
	def registerChild(child: Directive) = childrenBuff += child

	/** Directly nested directives */
	def children = childrenBuff.toList

	/** Transitive closure */
	def allChildren: List[Directive] = children ++ children.flatMap(_.allChildren)

	/** Extract children of type T and return them as list of T */
	def childrenOfType[T <: Directive : ClassTag]: List[T] = children.filter(_ match {
		case _: T => true
		case _    => false
	}).map{case x: T => x}

	/** Delete directive comment from source code */
	protected def deleteCmt(implicit rewriter: TokenStreamRewriter) = rewriter.replace(cmt, "\n")

	// TODO: use some trait together with omptree
	/** Fetch CompilationUnitContext associated with this file */
	private def cunit(t: ParserRuleContext = ctx): Java8Parser.CompilationUnitContext = {
		t.isInstanceOf[Java8Parser.CompilationUnitContext] match {
			case true => t.asInstanceOf[Java8Parser.CompilationUnitContext]
			case false => cunit(t.getParent)
		}
	}

	/** Directive validation */
	def validate() = parent match {   // parent validation
		case _: Sections | _: Master | _: Critical | _: Single | _: Barrier | _: Atomic => throw new SyntaxErrorException("In block 'omp sections' only 'omp section' blocks are allowed.")
		case _ => ;
	}

	/** Source code line and directive text */
	override def toString = s"Before line $line: ${cmt.getText}"

	/** Generate unique name (different from every token used and every loadable class) */
	def uniqueName(baseName: String): String = {

		val rand = new Random

		/** Static name generator */
		def getName(prefix: String): String = {
			if (Keywords.JAVA_KEYWORDS contains prefix) {
				val suff = rand.alphanumeric.take(3).mkString("")
				getName(s"${prefix}_${suff}")
			} else {
				conf.tokenSet.testAndSet(prefix) match {
					case true => prefix
					case false =>
						val suff = rand.alphanumeric.take(3).mkString("")
						getName(s"${prefix}_${suff}")
				}
			}
		}

		/** Dynamic name generator */
		val name = getName(baseName)
		// name
		try {
			conf.loader.load(name, cunit())
			val suff = rand.alphanumeric.take(3).mkString("")
			uniqueName(s"${baseName}_${suff}")
		} catch {
			case _: ClassNotFoundException => name
		}
	}

	// TODO: thread-safe rewriter
	/** Translate this directive and delete the directive comment */
	def translate(implicit rewriter: TokenStreamRewriter, ompFile: OMPFile) = {
		val (captured, capturedThis, directiveClass) = preTranslate
		postTranslate(captured, capturedThis, directiveClass)
		translateChildren(captured, capturedThis, directiveClass)
		deleteCmt
	}

	/** First level of translation - rewrite used variables */
	protected def preTranslate(implicit rewriter: TokenStreamRewriter, ompFile: OMPFile) = {
		val tv = new TranslationVisitor(rewriter, ompFile, this)
		tv.visit(ctx)
		(tv.getCaptured, tv.getCapturedThis, tv.getDirectiveClass)
	}

	/** Method where children translation is invoked (if any) */
	protected def translateChildren(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter) = {
		childrenOfType[Critical].foreach{_.postTranslate}
		// TODO: atomic
	}

	/** Second level of translation - make parallelism */
	protected def postTranslate(captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass)(implicit rewriter: TokenStreamRewriter)

	/** Default implementation, should be mixed with LockMemory trait */
	def addAtomicBool(baseName: String): String = parent match {
		case null => throw new SyntaxErrorException("Unable to register AtomicBoolean. Please make sure the directive is in 'omp parallel [for]' block.")
		case _    => parent.addAtomicBool(baseName)
	}

	/** Storage for some additional elements */
	protected var additionalItems = scala.collection.mutable.Set[String]()

	/** Initialization of 2. iterator (if required) */
	protected def secondIterInit = if (secondIter) s"\tfinal int $iter2 = $iter;\n" else ""

	/** Declaration of THAT (captured this) */
	protected def thatDecl(implicit capturedThis: Boolean, directiveClass: OMPClass) = if (capturedThis) s"\tpublic ${directiveClass.name} THAT;\n" else ""

	/** Class declaration */
	protected def classDeclar(implicit captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass) =
		"/* === OMP CONTEXT === */\n" +
			s"class $contextClass {\n" +
			(for {c <- captured} yield s"\t${c.declaration}\n").toList.mkString +
			thatDecl +
			(for {a <- additionalItems} yield s"\t$a\n").toList.mkString +
			"}\n"

	/** Instance of context class */
	protected def instance = s"final $contextClass $contextVar = new $contextClass();\n"

	/** THAT initialization*/
	protected def thatInit(implicit capturedThis: Boolean) = if (capturedThis) s"$contextVar.THAT = this;\n" else ""

	/** Initialization of captured variables + THAT */
	protected def init(implicit captured: Set[OMPVariable], capturedThis: Boolean) = thatInit + (for {c <- captured} yield s"$contextVar.${c.fullName} = ${c.arrayLessName};\n").toList.mkString

	/**  First part of executor */
	protected def executorBegin =
		s"final org.omp4j.runtime.IOMPExecutor $executor = new $executorClass($threadCount);\n" +
			"/* === /OMP CONTEXT === */\n" +
			s"for (int $iter = 0; $iter < $threadCount; ${iter}++) {\n" +
			secondIterInit +
			s"\t$executor.execute(new Runnable(){\n" +
			"\t\t@Override\n" +
			"\t\tpublic void run() {\n"

	/** Closing task and executor */
	protected def executorEnd =
		"\t\t}\n" +
			"\t});\n" +
			"}\n" +
			s"$executor.waitForExecution();\n"


	/** Assing primitive values */
	protected def primitiveAssigments(implicit captured: Set[OMPVariable]) = (for {c <- captured if (Keywords.JAVA_VALUE_TYPES contains c.varType)} yield s"\t${c.arrayLessName} = $contextVar.${c.fullName};\n").toList.mkString

	/** Code to be prepended */
	protected def toPrepend(implicit captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass) = classDeclar + instance + init + executorBegin

	/** Code to be appended*/
	protected def toAppend(implicit captured: Set[OMPVariable]) = executorEnd + primitiveAssigments

	/** Modify code according to toPrepend and toAppend */
	def wrap(rewriter: TokenStreamRewriter)(implicit captured: Set[OMPVariable], capturedThis: Boolean, directiveClass: OMPClass) = {
		rewriter.insertBefore(ctx.start, toPrepend)
		rewriter.insertAfter(ctx.stop, toAppend)
	}

	/** Wrapper of TokenStreamRewriter.getText(SyntaxTree) until it is officially supported */
	protected def getRewrittenText(ctx: SyntaxTree)(implicit rewriter: TokenStreamRewriter) = rewriter.getText(ctx.getSourceInterval)
}

/** Static directive procedures */
object Directive {

	/** Directive constructor */
	def apply(parent: Directive, ompCtx: OMPParser.OmpUnitContext, cmt: Token, ctx: Java8Parser.StatementContext)(implicit conf: Config): Directive = {
		if (ompCtx == null) throw new SyntaxErrorException("null OMP context")

		val parallel = ompCtx.ompParallel
		val parallelFor = ompCtx.ompParallelFor
		val nonParFor = ompCtx.ompFor
		val sections = ompCtx.ompSections
		val section = ompCtx.ompSection
		val single = ompCtx.ompSingle
		val master= ompCtx.ompMaster
		val barrier = ompCtx.ompBarrier
		val atomic = ompCtx.ompAtomic
		val critical = ompCtx.ompCritical

		if (parallel != null) {
			// TODO: pass num
			val (sch, num, privates, publics) = getModifiers[OMPParser.OmpParallelModifiersContext, OMPParser.OmpParallelModifierContext](parallel.ompParallelModifiers)(
				_.ompParallelModifier,
				_.ompParallelModifiers,
				_.ompSchedule,
				_.ompThreadNum,
				_.ompAccessModifier
			)
			new Parallel(parent, publics, privates)(DirectiveSchedule(sch), ctx, cmt, getLine(ctx), conf)
		} else if (parallelFor != null) {
			// TODO: pass num
			val (sch, num, privates, publics) = getModifiers[OMPParser.OmpParallelForModifiersContext, OMPParser.OmpParallelForModifierContext](parallelFor.ompParallelForModifiers)(
				_.ompParallelForModifier,
				_.ompParallelForModifiers,
				_.ompSchedule,
				_.ompThreadNum,
				_.ompAccessModifier
			)
			new ParallelFor(parent, publics, privates)(DirectiveSchedule(sch), ctx, cmt, getLine(ctx), conf)
		} else if (nonParFor != null) {
			val (publics, privates) = separate(nonParFor.ompAccessModifier.asScala.toList)
			new For(parent, publics, privates)(ctx, cmt, getLine(ctx), conf)
		} else if (sections != null) {
			new Sections(parent)(DirectiveSchedule(sections.ompSchedule), ctx, cmt, getLine(ctx), conf)
		} else if (section != null) {
			new Section(parent)(ctx, cmt, getLine(ctx), conf)
		} else if (single != null) {
			new Single(parent)(ctx, cmt, getLine(ctx), conf)
		} else if (master != null) {
			new Master(parent)(ctx, cmt, getLine(ctx), conf)
		} else if (barrier != null) {
			new Barrier(parent)(ctx, cmt, getLine(ctx), conf)
		} else if (atomic != null) {
			new Atomic(parent)(ctx, cmt, getLine(ctx), conf)
		} else if (critical != null) {
			new Critical(parent, critical.ompVar)(ctx, cmt, getLine(ctx), conf)
		} else {
			throw new SyntaxErrorException("Invalid directive")
		}
	}

	/** Get approximate line number */
	private def getLine(ctx: ParserRuleContext) = {
		if (ctx == null || ctx.start == null) -1
		else ctx.start.getLine
	}

	/** Get tuple of (schedule, threadNum, privates, publics)*/
	private def getModifiers[ML, M](mList: ML)(modifier: ML => M, nextList: ML => ML, schedule: M => OMPParser.OmpScheduleContext, threadNum: M => OMPParser.OmpThreadNumContext, access: M => OMPParser.OmpAccessModifierContext): (OMPParser.OmpScheduleContext, Int, List[String], List[String]) = {
		if (mList == null || modifier(mList) == null) {
			(null, -1, List(), List())
		} else {
			val (sch, num, privates, publics) = getModifiers(nextList(mList))(modifier, nextList, schedule, threadNum, access)
			val mod = modifier(mList)
			val newSch = if (schedule(mod) != null) schedule(mod) else sch
			val newTN  = if (threadNum(mod) != null) threadNum(mod).ompNumber.getText.toInt else num
			val newPri = try {
				val acc = access(mod)
				val res = getVars(acc.ompVars)
				if (acc.PRIVATE != null) privates ::: res
				else privates
			} catch {
				case e: NullPointerException => privates
			}
			val newPub = try {
				val acc = access(mod)
				val res = getVars(acc.ompVars)
				if (acc.PUBLIC != null) publics ::: res
				else publics
			} catch {
				case e: NullPointerException => publics
			}
			(newSch, newTN, newPri, newPub)
		}
	}

	/** Extracts variables from public/private statement */
	private def getVars(vars: OMPParser.OmpVarsContext): List[String] = {
		if (vars == null) List()
		else {
			val v = vars.ompVar
			if (v == null) List()
			else v.asScala.map(_.VAR.getText).toList
		}
	}

	/**
	  * Separate public and private variables
	  * @return tuple of (Public, Private)
	  */
	private def separate(list: List[OMPParser.OmpAccessModifierContext]): (List[String], List[String]) = {

		/** Extracts variables from public/private statement */
		def getVars(vars: OMPParser.OmpVarsContext): List[String] = {
			if (vars == null) List()
			else {
				val v = vars.ompVar
				if (v == null) List()
				else v.asScala.map(_.VAR.getText).toList
			}
		}

		if (list == null || list.size == 0) (List(), List())
		else {
			val head = list.head
			val (resPub, resPri) = separate(list.tail)

			if (head.PUBLIC != null) (getVars(head.ompVars) ++ resPub, resPri)
			else if (head.PRIVATE != null) (resPub, getVars(head.ompVars) ++ resPri)
			else throw new ParseException("Unexpected variable modifier")
		}
	}
}
