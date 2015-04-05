package org.omp4j.utils

import java.io._

import org.omp4j.grammar.Java8Parser
import scala.collection.JavaConverters._

/**
 * Created by petr on 29.1.15.
 * TODO: doc
 */
object FileSaver {
	def saveToFile(content: String, dir: File, pkg: Java8Parser.PackageDeclarationContext, fileName: String): File = {
		val suffix =
			if (pkg == null) "."
			else pkg.Identifier.asScala.map(_.toString).mkString(File.separator)

		saveToFile(content, dir, suffix + File.separator + fileName)

	}

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
