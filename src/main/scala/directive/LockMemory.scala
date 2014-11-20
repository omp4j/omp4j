package org.omp4j.directive

import scala.collection.mutable.Set

trait LockMemory {
	/** Inherit */
	def uniqueName(baseName: String): String

	/** Storage */
	protected var additionalItems: Set[String]

	/** Add java.util.concurrent.atomic.AtomicBoolean
	  * @return used name
	  * */
	def addAtomicBool(baseName: String): String = {
		val name = uniqueName(baseName)
		additionalItems += s"public java.util.concurrent.atomic.AtomicBoolean $name = new java.util.concurrent.atomic.AtomicBoolean(false);"
		name
	}

}
