package org.omp4j.tree

/** Trait for `findClass` method. Classes extending this trait can return subclasses by names (or chunks). */
trait Findable {

	/** Find class by its chunked FQN
	 *
	 * @param chunks array of chunks
	 * @return requested class
	 */
	def findClass(chunks: Array[String]): OMPClass
}
