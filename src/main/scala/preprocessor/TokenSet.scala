package org.omp4j.preprocessor

import scala.collection.JavaConverters._

class TokenSet {

	type TokenSet = java.util.concurrent.ConcurrentHashMap[String, java.lang.Boolean]
	private val tokenSet = new TokenSet()

	def testAndSet(name: String): Boolean = {
		tokenSet.putIfAbsent(name, java.lang.Boolean.TRUE) match {
			case null => true
			case java.lang.Boolean.TRUE => false
			case _ => throw new RuntimeException("TokenSet must contain only TRUE or null")
		}
	}

	def tokensAsString: List[String] = tokenSet.keys.asScala.toList
}
