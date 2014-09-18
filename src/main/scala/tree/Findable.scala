package org.omp4j.tree

trait Findable {
	def findClass(chunks: Array[String]): OMPClass
}
