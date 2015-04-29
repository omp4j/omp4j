package org.omp4j.utils

import java.io._

/** Tool for simple data duplication */
object FileDuplicator {

	/** Also close inputstream
	 * @throws IOException */

	/** Write the whole stream into a file.
	  *
	  * Automatically closes the stream.
	  *
	  * @param inputStream stream to be written
	  * @param outputFile file into which the content will be written
	  * @throws IOException when the file doesn't exist or the stream fails
	  */
 	def streamToFile(inputStream: InputStream, outputFile: File) = {
		val outputStream = new FileOutputStream(outputFile)
		outputStream.write(Stream.continually(inputStream.read).takeWhile(-1 !=).map(_.toByte).toArray)
		outputStream.close()
		inputStream.close()
	}

	/** Copies a filesystem subtree into another.
	  *
	  * Only regular files and directories supported.
	  * @param src root of the filesystem subtree that will be copied
	  * @param dst directory the content will be copied in
	  * @throws IOException if some file doesn't exist or unsupported type occurs
	  */
	def dirToDir(src: File, dst: File): Unit = {
		if (src.isFile) {       // copy file
			val in = new FileInputStream(src)
			streamToFile(in, dst)
		} else if (src.isDirectory) {          // copy dir
			if (!dst.exists) dst.mkdirs()
			src.list.foreach{f =>
				val nextSrc = new File(src, f)
				val nexDst= new File(dst, f)

				dirToDir(nextSrc, nexDst)
			}
		} else {        // links etc.
			throw new IOException(s"Neither file nor directory: '${src.getAbsolutePath}'")
		}

	}
}
