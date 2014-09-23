package org.omp4j.utils

import java.io._

class TmpDir(path: File, prefix: String) {
	private val dir = createTmpDir()

	private def createTmpDir(idx: Int = 0): File = {
		val name = s"${path.getAbsolutePath}${File.separator}$prefix-$idx"
		val f = new File(name)

		if (f.exists) {
			createTmpDir(idx + 1)
		} else {
			f.mkdirs match {
				case true  => f
				case false => createTmpDir(idx + 1)	// TODO: randomly?
			}			
		}
	}

	def toFile: File = dir
}
