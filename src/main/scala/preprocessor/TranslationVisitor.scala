package org.omp4j.preprocessor

import org.antlr.v4.runtime._
import org.omp4j.Config
import org.omp4j.directive._
import org.omp4j.exception._
import org.omp4j.extractor.Inheritor
import org.omp4j.grammar._
import org.omp4j.tree._

import scala.collection.JavaConverters._
import scala.collection.mutable.Stack

/** Listener for directive application */
class TranslationVisitor(tokens: CommonTokenStream, parser: Java8Parser, tree: Java8Parser.CompilationUnitContext)(implicit conf: Config) extends Java8BaseVisitor[Unit] {

	/** Reflected file structure */
	private lazy val ompFile = new OMPFile(tree, parser)

	/** List of directives */
	private lazy val directives = (new DirectiveVisitor(tokens, parser)).visit(tree)

	/** Rewriter for directive expansions*/
	private lazy val rewriter = new TokenStreamRewriter(tokens)

	/** Directive translator */
	private lazy val translator = new Translator(rewriter, parser, directives, ompFile)

	/** Directive currently being processed */
	private var currentDirective: Directive = null

	/** Stack of nested classes (Class name, isLocal) */
	private val clStack = Stack[OMPClass]()

	/** Set of local variables */
	private var locals = Set[OMPVariable]()

	/** Last visited class when directive was discovered */
	private var directiveClass: OMPClass = null

	/** Set of parameters */
	private var params = Set[OMPVariable]()

	/** Set of variables to be added to context*/
	private var captured = Set[OMPVariable]()

	/** Does 'this' keyword appears in parallel statement? */
	private var capturedThis = false

	/** Name of OMPContext variable */
	private def contextName = currentDirective match {
		case null => throw new RuntimeException("Not existing directive context name required")
		case _    => currentDirective.contextVar
	}

	/** Run translator and return modified source as String */
	def translate: String = {
		visit(tree)
		rewriter.getText
	}
	
	/** Translate statements having directive */
	override def visitStatement(ctx: Java8Parser.StatementContext) = {

		// already processing directive; continue rewriting
		if (currentDirective != null) {
			super.visitStatement(ctx)
		}
		// no current directive; check existence
		else {
			directives.get(ctx) match {	// TODO: nested directives
				case Some(d) => {	// accessing new directive
					// set things up
					currentDirective = d
					directiveClass = clStack.head
					locals = Inheritor.getPossiblyInheritedLocals(ctx)
					params = Inheritor.getPossiblyInheritedParams(ctx)

					// process the statement
					super.visitStatement(ctx)

					// translate (into rewriter)
					translator.translate(currentDirective, locals, params, captured, capturedThis, clStack.head.name)

					// reset
					currentDirective = null
					directiveClass = null
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
	private def handleStack[T <: ParserRuleContext](ctx: T, f: (T) => Unit) = {
		ompFile.classMap.get(ctx) match {
			case Some(c) => 
				clStack.push(c)
				f(ctx)
				clStack.pop
			case None => throw new ParseException("Unexpected error - class not found")
		}
	}

	override def visitClassDeclaration(ctx: Java8Parser.ClassDeclarationContext) = {
		handleStack(ctx, super.visitClassDeclaration)
	}

	override def visitClassBody(ctx: Java8Parser.ClassBodyContext) = {
		if (
			ctx.parent.isInstanceOf[Java8Parser.ClassInstanceCreationExpressionContext] ||
			ctx.parent.isInstanceOf[Java8Parser.ClassInstanceCreationExpression_lf_primaryContext] ||
			ctx.parent.isInstanceOf[Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext]) {

			handleStack(ctx, super.visitClassBody)
		} else {
			super.visitClassBody(ctx)
		}

	}

	// TODO: doc
	private def getLeftName[T, S](
		top: T,
		topF: (T) => S,
		bottomF: (S) => S,
		topId: (T) => String,
		bottomId: (S) => String): String = {

		def getRec(under: S): String = {
			if (bottomF(under) == null) bottomId(under)
			else getRec(bottomF(under))
		}

		if (top == null) throw new IllegalArgumentException
		else if (topF(top) == null) topId(top)
		else getRec(topF(top))
	}

	/** Capture variables/fields */
	override def visitExpressionName(ctx: Java8Parser.ExpressionNameContext) = {
		if (currentDirective != null) {
			// globals (not actually functional, TODO)
			try {

				// println(s"-> ${ctx.getText}")
				val id = getLeftName[Java8Parser.ExpressionNameContext, Java8Parser.AmbiguousNameContext](
					ctx,
					_.ambiguousName,
					_.ambiguousName,
					_.Identifier.getText,
					_.Identifier.getText)
				if (! (Inheritor.getDirectiveLocals(ctx, currentDirective).map(_.name) contains id)) {
					try {
						val v = OMPVariable(id, locals, params, directiveClass)

						val tkns = translator.getContextTokens(ctx)
						if (tkns.head.getText == id) {
							rewriter.replace(tkns.head, s"$contextName.${v.fullName}")
						} else {
							rewriter.replace(ctx.start, ctx.stop, s"$contextName.${v.fullName}")
						}

						captured += v
					} catch {
						case e: IllegalArgumentException => ; // local (ok)
					}
				}
			} catch {
				// TODO: exceptions?
				case e: IllegalArgumentException => println(s"IAE: ${e.getMessage}")
			}
		}
		super.visitExpressionName(ctx)
	}

	override def visitMethodInvocation(ctx: Java8Parser.MethodInvocationContext) = {
		if (currentDirective != null) {

			if (ctx.primary != null && ctx.primary.getText == "this") {
				if (clStack.head == directiveClass) {
					// handle only the '.' as 'this' will be handled automatically later on
					val dot = translator.getContextTokens(ctx)(1)
					rewriter.delete(dot)
				}
			} else if (ctx.typeName != null) {

				val id = getLeftName[Java8Parser.TypeNameContext, Java8Parser.PackageOrTypeNameContext](
				ctx.typeName,
				_.packageOrTypeName,
				_.packageOrTypeName,
				_.Identifier.getText,
				_.Identifier.getText)

				if (! (Inheritor.getDirectiveLocals(ctx, currentDirective).map(_.name) contains id)) {
					try {
						val v = OMPVariable(id, locals, params, directiveClass)
						val firstToken = translator.getContextTokens(ctx).head
						rewriter.replace(firstToken, s"$contextName.${v.fullName}")

						captured += v
					} catch {
						case e: IllegalArgumentException => ; // local (ok)
					}
				}
			}
		}
		super.visitMethodInvocation(ctx)
	}

	override def visitPrimary(ctx: Java8Parser.PrimaryContext) = {
		if (currentDirective != null) {

			if (ctx.primaryNoNewArray_lfno_primary == null) {
				// TODO: primaryNoNewArray_lfno_primary
			}
			else {
				val first = ctx.primaryNoNewArray_lfno_primary
				val seconds: List[Java8Parser.PrimaryNoNewArray_lf_primaryContext] =
					if (ctx.primaryNoNewArray_lf_primary != null) ctx.primaryNoNewArray_lf_primary.asScala.toList
					else List[Java8Parser.PrimaryNoNewArray_lf_primaryContext]()

				handlePrimary(ctx, first, seconds)
			}
		}
		super.visitPrimary(ctx)
	}

	private def handlePrimary(ctx: Java8Parser.PrimaryContext, first: Java8Parser.PrimaryNoNewArray_lfno_primaryContext, seconds: List[Java8Parser.PrimaryNoNewArray_lf_primaryContext]) = {

		// is primary expression of method invocation
		val isMI = first.parent.parent.isInstanceOf[Java8Parser.MethodInvocationContext]
		try {

			// primary starts with 'this'
			if (first.getText == "this") {

				// only for first-class expressions
				if (clStack.head == directiveClass) {

					// 'this' as a standalone
					if (seconds.size == 0) {
						if (isMI) {
							rewriter.delete(first.start, first.stop)
						} else {
							rewriter.replace(first.start, first.stop, s"$contextName.THAT")
							capturedThis = true
						}
					} else {
						// 'this' in a tandem
						val next = seconds.head
						if (next.fieldAccess_lf_primary != null) {
							val id = next.fieldAccess_lf_primary.Identifier.getText
							try {
								// try to rewrite var name (if captured)
								val v = OMPVariable.findField(id, directiveClass)
								rewriter.replace(first.start, first.stop, contextName)
								rewriter.replace(next.start, next.stop, s".${v.fullName}")

								captured += v   // ??
							} catch {
								case e: IllegalArgumentException => ; // local (ok)
							}
						} else if (next.methodInvocation_lf_primary != null) {
							// getting rid of 'this.'
							rewriter.delete(first.start, first.stop)

							val dot = translator.getContextTokens(ctx)(1)
							rewriter.delete(dot)
						}

						capturedThis = true
					}
				}


			} else {
				if (first.fieldAccess_lfno_primary != null) {

					val id = getLeftName[Java8Parser.TypeNameContext, Java8Parser.PackageOrTypeNameContext](
					first.fieldAccess_lfno_primary.typeName,
					_.packageOrTypeName,
					_.packageOrTypeName,
					_.Identifier.getText,
					_.Identifier.getText)

					if (! (Inheritor.getDirectiveLocals(ctx, currentDirective).map(_.name) contains id)) {
						try {
							val v = OMPVariable(id, locals, params, directiveClass)
							rewriter.replace(first.start, first.stop, s"$contextName.${v.fullName}")
						} catch {
							case e: IllegalArgumentException => ; // local (ok)
						}
					}

				} else if (first.methodInvocation_lfno_primary != null) {
					val mip = first.methodInvocation_lfno_primary
					if (mip.methodName != null) {
						// simple method call
					} else if (mip.typeName != null) {

						val id = getLeftName[Java8Parser.TypeNameContext, Java8Parser.PackageOrTypeNameContext](
							first.methodInvocation_lfno_primary.typeName,
							_.packageOrTypeName,
							_.packageOrTypeName,
							_.Identifier.getText,
							_.Identifier.getText)

						if (! (Inheritor.getDirectiveLocals(ctx, currentDirective).map(_.name) contains id)) {
							try {
								val v = OMPVariable(id, locals, params, directiveClass)
								val firstToken = translator.getContextTokens(first).head
								rewriter.replace(firstToken, s"$contextName.${v.fullName}")
								captured += v

							} catch {
								case e: IllegalArgumentException => ; // local (ok)
							}
						}
					} else {
						// TODO: mip is not method or typename
					}
				}
			}
		// TODO: exception (should never happen?)
		} catch {
			case e: Exception => throw new ParseException("Unepected exception in handlePrimary", e)
		}

	}
	// TODO: super

}
