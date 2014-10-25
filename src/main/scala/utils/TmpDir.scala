package org.omp4j.utils

import java.io._

import scala.util.Random

class TmpDir(path: File, prefix: String) {
	private val rand = new Random()
	private val dir = createTmpDir(rand.alphanumeric.take(5).toString())

	/**
	 * @source http://stackoverflow.com/questions/617414/create-a-temporary-directory-in-java
	 * */
	private def createTmpDir(idx: String): File = {
		val tempFile = File.createTempFile(s"$prefix-", java.lang.Long.toString(System.nanoTime()), path)

		if (! tempFile.delete()) throw new IOException(s"Could not delete temp file '${tempFile.getAbsolutePath()}'")
		if (! tempFile.mkdir())  throw new IOException(s"Could not create temp directory '${tempFile.getAbsolutePath()}'")

		tempFile
	}

	def toFile: File = dir
}
