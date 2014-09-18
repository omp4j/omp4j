package org.omp4j.extractor

import scala.util.control.Breaks._
import scala.collection.JavaConverters._

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._

import org.omp4j.grammar._
import org.omp4j.tree._

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

	/** get Local and InnerInLocal classes*/
	def getVisibleLocalClasses(pt: ParseTree, ompFile: OMPFile): List[OMPClass] = {

		type CDC = Java8Parser.ClassDeclarationContext

		/** Fetch classes using extractors */
		def iiner(t: ParseTree, follower: ParseTree): List[OMPClass] = {
			var result = List[OMPClass]()
			breakable {
				// iterate through all children left to the follower
				for {i <- 0 until t.getChildCount} {
					val child = t.getChild(i)
					if (child == follower) break
					// println("----")
					try {
						val bs = child.asInstanceOf[Java8Parser.BlockStatementContext]
						val cdc = bs.classDeclaration
						ompFile.classMap.get(cdc) match {
							case Some(ompC) => ompC match {
								case LocalClass(_,_,_)        => result = ompC :: result
								case InnerInLocalClass(_,_,_) => result = ompC :: result
								case _          => ;
							}

								
							case None => ;
						}
					} catch {
						case e: Exception => ; //println(child.getText + "\n\t" + child.getClass.getName.toString)
					}
					// result = result ::: (new ClassExtractor ).visit(child).toList.map(ctx => 
					// 	classMap.get(ctx) match {
					// 		case Some(ompC) => ompC
					// 		case None        => null
					// 	}).filter(c => c != null)
				}
			}
			result

		}

		val neck = getParentList(pt)	// list of parent
		(neck zip neck.tail).map{ case (t, follower) => iiner(t, follower)}.flatten.toList
	}

	/** Get Top and Inner classes */
	def getVisibleNonLocalClasses(pt: ParseTree, ompFile: OMPFile): List[OMPClass] = {

		type CDC = Java8Parser.ClassDeclarationContext

		/** Fetch classes using extractors */
		def iiner(t: ParseTree, follower: ParseTree): List[OMPClass] = {
			try {
				val cl: CDC = t.asInstanceOf[CDC]
				((new FieldClassExtractor ).visit(cl) :+ cl).map(ctx => 
					ompFile.classMap.get(ctx) match {
						case Some(ompC) => ompC match {
							case LocalClass(_,_,_) => null
							case InnerInLocalClass(_,_,_) => null
							case _ => ompC
						}
						case None => null
					}).filter(c => c != null)
			} catch {
				case e: Exception => List[OMPClass]()
			}
		}

		val neck = getParentList(pt)	// list of parent
		// TODO: delete duplicites
		val onPath = (neck zip neck.tail).map{ case (t, follower) => iiner(t, follower)}.flatten.toList
		// add top classes as they are always visible
		onPath ::: ompFile.classes
	}


}
