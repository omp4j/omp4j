package org.omp4j.preprocessor

import scala.collection.JavaConverters._

/** Represents atomic operation on the set of used tokens.
  *
  * @constructor create the empty set
  */
class TokenSet {

	/** Type alias */
	private type ConcurrentSet_sb = java.util.concurrent.ConcurrentHashMap[String, java.lang.Boolean]

	/** The concurrent set itself */
	private val tokenSet = new ConcurrentSet_sb()

	/** Try to insert new element.
	  *
	  * @param name element
	  * @return true if inserted, false if element is already inserted
	  * @throws RuntimeException if internal error occurs
	  */
	def testAndSet(name: String): Boolean = {
		tokenSet.putIfAbsent(name, java.lang.Boolean.TRUE) match {
			case null => true
			case java.lang.Boolean.TRUE => false
			case _ => throw new RuntimeException("TokenSet must contain only TRUE or null")
		}
	}

	def tokensAsString: List[String] = tokenSet.keys.asScala.toList
}
