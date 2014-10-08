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
//		f.mkdirs() match {
//			case true  => f
//			case false => throw new RuntimeException(s"Tmp dir '${f.getAbsolutePath}' can't be created.")
//		}
//		f
		/*

		val name = s"${path.getAbsolutePath}${File.separator}$prefix-$idx"
		val f = new File(name)

		if (f.exists) {
			createTmpDir(idx + 1)
		} else {
			f.mkdirs match {
				case true  => f
				case false => createTmpDir(s"$idx-${rand.alphanumeric.take(3).toString()}")	// TODO: randomly?
			}			
		}
		*/
	}

	def toFile: File = dir
}
