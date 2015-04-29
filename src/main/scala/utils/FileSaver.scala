package org.omp4j.utils

import java.io._

import org.omp4j.grammar.Java8Parser
import scala.collection.JavaConverters._

/** Tool for simple file saving */
object FileSaver {

	/** Save content directly into directory
	  *
	  * @param content the content to be written
	  * @param dir the file parent directory
	  * @param pkg ANTLR package object (for correct subdirectory)
	  * @return the created file with the content written into
	  */
	def saveToFile(content: String, dir: File, pkg: Java8Parser.PackageDeclarationContext, fileName: String): File = {
		val suffix =
			if (pkg == null) "."
			else pkg.Identifier.asScala.map(_.toString).mkString(File.separator)

		saveToFile(content, dir, suffix + File.separator + fileName)

	}

	/** Save content directly into directory
	 *
	 * @param content the content to be written
	 * @param dir the file parent directory
	 * @param suffix the file name
	 * @return the created file with the content written into
	 */
	def saveToFile(content: String, dir: File, suffix: String): File = {
		if (!dir.isDirectory) throw new IllegalArgumentException("not a directory")

		val f = new File(dir.getAbsolutePath + File.separator + suffix)
		f.getParentFile.mkdirs()
		f.createNewFile()

		val pw = new PrintWriter(f, "UTF-8")
		pw.write(content)
		pw.close()

		f
	}
}
