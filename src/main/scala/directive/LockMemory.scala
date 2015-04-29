package org.omp4j.directive

/** LockMemory trait complementing Directive */
trait LockMemory {

	/** Abstract uniqueName method */
	def uniqueName(baseName: String): String

	/** Item storage */
	protected var additionalItems: scala.collection.mutable.Set[String]

	/** Add java.util.concurrent.atomic.AtomicBoolean into the storage
	  *
	  * @param baseName name of the item
	  * @return used name
	  */
	def addAtomicBool(baseName: String): String = {
		val name = uniqueName(baseName)
		additionalItems += s"public java.util.concurrent.atomic.AtomicBoolean $name = new java.util.concurrent.atomic.AtomicBoolean(false);"
		name
	}
}
