package org.omp4j.utils

import java.io.File
import java.net.URISyntaxException

object FileTreeWalker {

	/** DFS file list */
	def recursiveListFiles(f: File): Array[File] = {
		if (f != null) {
			val these = f.listFiles
			these.filter(_.isDirectory).flatMap(recursiveListFiles) ++ these
		} else Array()
	}

	/** Delete recursively */
	def recursiveDelete(f: File) = {
		if (f != null) {
			recursiveListFiles(f).map(_.delete)
			f.delete
		}
	}
}
