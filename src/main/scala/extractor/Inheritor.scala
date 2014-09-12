package org.omp4j.extractor

import scala.util.control.Breaks._
import scala.collection.JavaConverters._

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._

import org.omp4j.preprocessor.grammar._
import org.omp4j.preprocessor.OMPVariable
/** Extracts various inherited stuff */
object Inheritor {

	/** Get sequence of all (in)direct parents of tree given
	  * @param pt Tree whose parents are about to be fetched
	  * @return Set of trees
	  */
	def getParentList(t: ParseTree): Seq[ParseTree] = {
		if (t == null) Seq[ParseTree]()
		else getParentList(t.getParent) :+ t
	}

	/** Get set of variables (their declarations) whose can be reffered
	  * but are not declared in the tree given
	  * @param pt Tree whose variable are about to be fetched
	  * @return Set of variables
	  */
	def getPossiblyInheritedLocals(pt: ParseTree): Set[OMPVariable] = {
		// result set - TODO: rewrite more functionally
		var result = Set[OMPVariable]()
		val neck = getParentList(pt)	// list of parent

		// iterate through the list of tuples (tree-node, follower-in-neck)
		for {(t, follower) <- (neck zip neck.tail)} {
			// println("visiting:\t" + t)
			breakable {
				// iterate through all children left to the follower
				for {i <- 0 until t.getChildCount} {
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

		neck.foreach{ n =>
			try {
				val method: MDC = n.asInstanceOf[MDC]
				val list = method.methodHeader.methodDeclarator.formalParameterList

				// add non-last
				if (list != null) {
					val firsts = list.formalParameters	// TODO: receiver??
					if (firsts != null) {
						firsts.formalParameter.asScala.foreach{ p =>
							result += new OMPVariable(p.variableDeclaratorId.Identifier.getText, p.unannType.getText)
						}
					}

					val last = list.lastFormalParameter.formalParameter
					if (last != null) {
						result += new OMPVariable(last.variableDeclaratorId.Identifier.getText, last.unannType.getText)
					}
				}
			} catch {
				case e: ClassCastException => ;
			}
		}
		result
	}

	def getVisibleLocalClasses(pt: ParseTree): List[Java8Parser.ClassDeclarationContext] = {

		type CDC = Java8Parser.ClassDeclarationContext

		// result List - TODO: rewrite more functionally
		var result = List[CDC]()
		val neck = getParentList(pt)	// list of parent

		// iterate through the list of tuples (tree-node, follower-in-neck)
		for {(t, follower) <- (neck zip neck.tail)} {
			// println("visiting:\t" + t)

			try {
				val cl: CDC = t.asInstanceOf[CDC]
				result = result ::: ((new FieldClassExtractor ).visit(cl) :+ cl)
				// result


			} catch {
				case e: Exception => ;
			}

			breakable {
				// iterate through all children left to the follower
				for {i <- 0 until t.getChildCount} {
					val child = t.getChild(i)
					if (child == follower) break
					result = result ::: (new ClassExtractor ).visit(child).toList
				}
			}
		}
		result
		
	}

}
