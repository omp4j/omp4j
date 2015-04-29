package org.omp4j.utils

import java.io._

import scala.util.Random

/** Temporary directory.
  *
  * @constructor Creates the unique directory in specified parent directory with specified prefix.
  * @param path parent directory
  * @param prefix name prefix
  */
class TmpDir(path: File, prefix: String = "") {
	/** Random generator */
	private val rand = new Random()

	/** The temporary directory itself */
	private val dir = createTmpDir(rand.alphanumeric.take(5).toString())

	/** Simulates atomic directory creation.
	  *
	  * This feature was added to Java8 via `java.io.Files` API, however in order to support Java 6 and 7 this method
	  * is developed. Code was taken from http://stackoverflow.com/questions/617414/create-a-temporary-directory-in-java
	  */
	private def createTmpDir(idx: String): File = {
		val tempFile = File.createTempFile(s"$prefix-", java.lang.Long.toString(System.nanoTime()), path)

		if (! tempFile.delete()) throw new IOException(s"Could not delete temp file '${tempFile.getAbsolutePath}'")
		if (! tempFile.mkdir())  throw new IOException(s"Could not create temp directory '${tempFile.getAbsolutePath}'")

		tempFile
	}

	/** File getter */
	def toFile: File = dir
}
