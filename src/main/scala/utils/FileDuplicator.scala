package org.omp4j.utils

import java.io._


object FileDuplicator {

	/** Also close inputstream
	 * @throws IOException */
	def streamToFile(inputStream: InputStream, outputFile: File) = {
		val outputStream = new FileOutputStream(outputFile)
		outputStream.write(Stream.continually(inputStream.read).takeWhile(-1 !=).map(_.toByte).toArray)
		outputStream.close()
		inputStream.close()
	}

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
