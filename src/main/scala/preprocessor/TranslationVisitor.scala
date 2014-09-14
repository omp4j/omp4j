package org.omp4j.preprocessor

import scala.io.Source
import scala.util.control.Breaks._
import scala.collection.mutable.Stack
import scala.collection.JavaConverters._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.tree._
import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.extractor.Inheritor
import org.omp4j.grammar._

/** Listener for directive application */
class TranslationVisitor(tokens: CommonTokenStream, parser: Java8Parser, tree: Java8Parser.CompilationUnitContext)(implicit conf: Config) extends Java8BaseVisitor[Unit] {

	/** Reflected file structure */
	private lazy val ompFile = new OMPFile(tree, parser)

	/** List of directives */
	private lazy val directives = (new DirectiveVisitor(tokens, parser)).visit(tree)

	/** Directive translator */
	private lazy val translator = new Translator(tokens, parser, directives, ompFile)

	/** Rewriter for directive expansions*/
	private lazy val rewriter = new TokenStreamRewriter(tokens)

	/** Directive currently being proccesed*/
	private var currentDirective: Directive = null

	/** Stack of nested classes (Class name, isLocal) */
	private val clStack = Stack[StackClass]()

	/** Set of local variables */
	private var locals = Set[OMPVariable]()

	/** Set of parameters */
	private var params = Set[OMPVariable]()

	/** Set of variables to be added to context*/
	private var captured = Set[OMPVariable]()

	/** Name of OMPContext variable; TODO: unique*/
	private var contextName = "ompContext"

	/** Does 'this' keywork appears in parallel statement? */
	private var capturedThis = false

	/** Run translator and resturn modified source as String */
	def translate: String = {
		visit(tree)
		rewriter.getText
	}
	
	/** Translate statements having directive */
	override def visitStatement(ctx: Java8Parser.StatementContext) = {

		// already getting directive
		if (currentDirective != null) {
			super.visitStatement(ctx)	// continue visiting
		} else {	// no directive
			directives.find(_.ctx == ctx) match {	// TODO: nested directives
				case Some(d) => {	// accessing new directive
					// set things up
					currentDirective = d
					locals = Inheritor.getPossiblyInheritedLocals(ctx)
					params = Inheritor.getPossiblyInheritedParams(ctx)

					// work the statement
					super.visitStatement(ctx)

					// println(s"Found ${locals.size} locals")
					// locals.foreach{l => println(s"\t${l.name} - ${l.meaning}")}
					// println(s"Found ${captured.size} captured")
					// captured.foreach{c => println(s"\t${c.name} - ${c.meaning}")}

					// translate (into rewriter)
					translator.translate(currentDirective, rewriter, locals, params, captured, capturedThis, clStack.head.name)

					// reset
					currentDirective = null
					capturedThis = false
					captured = Set()
					locals = Set()
					params = Set()
				}
				case None => super.visitStatement(ctx)	// continue visiting
			}
		}
	}

	/** Handle class stack */
	override def visitClassDeclaration(ctx: Java8Parser.ClassDeclarationContext) = {
		clStack.push(new StackClass(ctx))
		super.visitClassDeclaration(ctx)
		clStack.pop
	}

	/** Construct OMPVariable properly or throws exception */
	private def constructVariable(id: String, locals: Set[OMPVariable], params: Set[OMPVariable]) = {

		var meaning = OMPVariableType.Class	// Primary meaning (class/local/...)
		var classType = ""	// extracted variable type (if really variable)

		var clazz: OMPClass = null
		ompFile.classMap.get(clStack.head.ctx) match {
			case Some(x) => clazz = x
			case None    => throw new ParseException("class not loaded")
		}
		val fields = clazz.allFields

		(locals find (_.name == id)) match {
			case Some(v) => {
				// if (id == "capt") println(s"\tfound in local")
				meaning = OMPVariableType.Local
				classType = v.varType

			}
			case None => {
				(params find (_.name == id)) match {
					case Some(v) => {
						meaning = OMPVariableType.Param
						classType = v.varType;
						// println(s"found param - $meaning")
					}
					case None => {
						(fields find (_.name == id)) match {
							case Some(v) => {
								meaning = OMPVariableType.Field
								classType = v.varType;
								// println(s"found field - $meaning")
							}
							// TODO: ignore exception?
							case None => throw new IllegalArgumentException(s"Variable '$id' not found in locals/params/fields")
						}
					}
				}
			}
		}
		new OMPVariable(id, classType, meaning)
	}

	// TODO: http://docs.oracle.com/javase/tutorial/java/javaOO/anonymousclasses.html
	/** Capture variables/fields */
	override def visitExpressionName(ctx: Java8Parser.ExpressionNameContext) = {
		if (currentDirective != null) {
			// globals (not actually functional, TODO)
			try {
				var id = ""
				if (ctx.ambiguousName != null) {
					id = ctx.ambiguousName.Identifier.getText
				} else {
					id = ctx.Identifier.getText
				}

				val toCapture = constructVariable(id, locals, params)

				// prefix the first token
				val ctxTokens = translator.getContextTokens(ctx)
				ctxTokens.head.asInstanceOf[CommonToken].setText(s"$contextName.${toCapture.meaning}_${ctxTokens.head.getText}")
				captured += toCapture

			} catch {
				// TODO: exceptions?
				case e: IllegalArgumentException => ; // println(s"IAE: ${e.getMessage}")
				case e: Exception => println(s" E1: ${e.getMessage}")
			}
		}
		super.visitExpressionName(ctx)
	}

	/** Capture objects invoking methods */
	override def visitMethodInvocation(ctx: Java8Parser.MethodInvocationContext) = {
		if (currentDirective != null) {
			generalMethodInvocation(ctx)
		}		
		super.visitMethodInvocation(ctx)
	}

	/** Capture objects invoking methods */
	override def visitMethodInvocation_lfno_primary(ctx: Java8Parser.MethodInvocation_lfno_primaryContext) = {
		if (currentDirective != null) {
			generalMethodInvocation(ctx)
		}	
		super.visitMethodInvocation_lfno_primary(ctx)
	}

	/** Handle all method invocations */
	private def generalMethodInvocation[T <: {
			def methodName(): Java8Parser.MethodNameContext;
			def typeName(): Java8Parser.TypeNameContext;
			def expressionName(): Java8Parser.ExpressionNameContext;
			def getStart(): Token;
			def getStop(): Token;
			def toStringTree(parser: Parser): String
		}](ctx: T) = {

		try {
			if (ctx.methodName != null) {
				val ctxTokens = translator.getContextTokens(ctx)
				ctxTokens.head.asInstanceOf[CommonToken].setText(s"$contextName.THAT.${ctxTokens.head.getText}")
				capturedThis = true
			} else if (ctx.typeName != null) {
				val id = ctx.typeName.Identifier.getText
				val toCapture = constructVariable(id, locals, params)
				// if (id == "capt") println(s"1 $toCapture  - ${ctx.toStringTree(parser)}")

				// prefix the first token
				val ctxTokens = translator.getContextTokens(ctx)
				ctxTokens.head.asInstanceOf[CommonToken].setText(s"$contextName.${toCapture.meaning}_${ctxTokens.head.getText}")
				captured += toCapture
				// rewriter.insertBefore(ctx.start, contextName + "." + meaning + "_")
			} else if (ctx.expressionName != null) {
				val id = ctx.expressionName.Identifier.getText
				val toCapture = constructVariable(id, locals, params)
				// if (id == "capt") println(s"2 $toCapture - ${ctx.toStringTree(parser)}")

				// prefix the first token
				val ctxTokens = translator.getContextTokens(ctx)
				ctxTokens.head.asInstanceOf[CommonToken].setText(s"$contextName.${toCapture.meaning}_${ctxTokens.head.getText}")
				captured += toCapture
				// rewriter.insertBefore(ctx.start, contextName + "." + meaning + "_")
			}
		} catch {
			// TODO: exceptions?
			case e: IllegalArgumentException => ;	// println(s"IAE: ${e.getMessage}")
			// case e: Exception => println(s" E2: ${e.getStackTrace}")
			case e: Exception => e.printStackTrace
		}

	}

	// TODO: super
	/** Handle 'this' keyword */
	override def visitPrimaryNoNewArray(ctx: Java8Parser.PrimaryNoNewArrayContext) = {
		generalPrimary(ctx)
		super.visitPrimaryNoNewArray(ctx)
	}

	/** Handle 'this' keyword */
	override def visitPrimaryNoNewArray_lfno_arrayAccess(ctx: Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext) = {
		generalPrimary(ctx)
		super.visitPrimaryNoNewArray_lfno_arrayAccess(ctx)
	}

	/** Handle 'this' keyword */
	override def visitPrimaryNoNewArray_lfno_primary(ctx: Java8Parser.PrimaryNoNewArray_lfno_primaryContext) = {
		generalPrimary(ctx)
		super.visitPrimaryNoNewArray_lfno_primary(ctx)
	}

	/** Handle 'this' keyword */
	override def visitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(ctx: Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext) = {
		generalPrimary(ctx)
		super.visitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(ctx)
	}

	/** Handle all primaries */
	private def generalPrimary(ctx: ParserRuleContext) = {
		if (currentDirective != null) {
			try {
				if (ctx.getText == "this") {
					// replace first (and only) token "this"
					val ctxTokens = translator.getContextTokens(ctx)
					ctxTokens.head.asInstanceOf[CommonToken].setText(contextName + ".THAT")
					capturedThis = true
				}
			} catch {
				// TODO: exceptions?
				// case e: IllegalArgumentException => println(s"IAE: ${e.getMessage}")
				case e: Exception => println(s" E3: ${e.getMessage}")
			}
		}
	}
}
