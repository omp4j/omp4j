package org.omp4j.utils

import java.io.File
import java.net.URISyntaxException

/** Tool for recursive file system iteration */
object FileTreeWalker {

	/** List all files in current subtree (recursively).
 	  *
	  * @param f from whence to start
	  * @return Array of lsited files
	  */
	def recursiveListFiles(f: File): Array[File] = {
		if (f != null && f.exists) {
			val these = f.listFiles
			these.filter(_.isDirectory).flatMap(recursiveListFiles) ++ these
		} else Array()
	}

	/** Delete the whole subtree (recursively).
	  *
	  * @param f from whence to start
	  */
	def recursiveDelete(f: File): Unit = {
		if (f != null) {
			recursiveListFiles(f).map(_.delete)
			f.delete
		}
	}
}
