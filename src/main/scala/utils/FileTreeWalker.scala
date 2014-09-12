package org.omp4j.utils

import java.io.File

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
}
