package org.omp4j.utils

import java.io.File
import java.net.URISyntaxException

object FileTreeWalker {

	/** DFS file list */
	def recursiveListFiles(f: File): Array[File] = {
		val these = f.listFiles
		these.filter(_.isDirectory).flatMap(recursiveListFiles) ++ these
	}

	/** Delete recursively */
	def recursiveDelete(f: File) = {
		recursiveListFiles(f).map(_.delete)
		f.delete
	}

	/** Get array of files that could be used in result binary */
	def getRuntimeFiles = {
		val resource = getClass.getResource("/runtime")
		val foo = 15;
		val runtimeDir = try {
			new File(resource.toURI)
		} catch {
			case e: URISyntaxException => new File(resource.getPath);
		}
		recursiveListFiles(runtimeDir).filter(_.getAbsolutePath.endsWith(".java"))
	}
}
