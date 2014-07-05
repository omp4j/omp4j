package org.omp4j.preprocessor

import scala.io.Source
import scala.util.control.Breaks._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.Config
import org.omp4j.preprocessor.grammar._
import org.omp4j.preprocessor.exception._

/** Listener for directive application */
class TranslationListener(directives: List[Directive], tokens: TokenStream, tree: OMPFile, parser: Java8Parser) extends Java8BaseListener {

	/** Java8Parser.FieldDeclarationContext typedef */
	type SC = Java8Parser.StatementContext

	/** Java8Parser.LocalVariableDeclarationContext typedef */
	type LVDC = Java8Parser.LocalVariableDeclarationContext
	
	/** Rewriter for directive expansions*/
	var rewriter = new TokenStreamRewriter(tokens)

	/** Filter statements with directive */
	override def enterStatement(ctx: SC) = {
		directives.find(d => d.ctx == ctx) match {
			case Some(d) => addDirectiveHead(d)
			case None => ;
		}
	}

	/** Filter statements with directive */
	override def exitStatement(ctx: SC) = {
		directives.find(d => d.ctx == ctx) match {
			case Some(d) => addDirectiveTail(d)
			case None => ;
		}
	}

	/** For now only add simple comment at the beginning -> insert context */
	private def addDirectiveHead(d: Directive) = {
		val source = Source.fromURL(getClass.getResource("/head.in"))
		// val head = source.getLines mkString "\n"
		val head = "/* " +
			getPossiblyInheritedLocals(d.ctx).map(_.getText()) +
			"\t" + 
			getLocals(d.ctx).map(_.getText()) +
			" */"

		rewriter.insertBefore(d.ctx.start, head);
	}

	/** For now only add simple comment at the end */
	private def addDirectiveTail(d: Directive) = {
		val source = Source.fromURL(getClass.getResource("/tail.in"))
		val tail = source.getLines mkString "\n"

		rewriter.insertAfter(d.ctx.stop, tail);
	}

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
	def getPossiblyInheritedLocals(pt: ParseTree): Set[LVDC] = {

		// result set - TODO: rewrite more functionally
		var result = Set[LVDC]()
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

	/** Get set of variables (their declarations) that are declared directly
	  * in the tree given
	  * @param pt Tree whose variable are about to be fetched
	  * @return Set of variables
	  */
	def getLocals(t: ParseTree): Set[LVDC] = (new LocalVariableExtractor ).visit(t)

}

