package org.omp4j.compiler

import java.io._
import java.util.jar._
import javax.tools._
import scala.collection.JavaConverters._
import scala.util.control.Breaks._

import org.omp4j.preprocessor.exception._

class Compiler(flags: Iterable[String], files: Iterable[File], classes: Iterable[String], argfiles: Iterable[File]) {

	/** Compile sources
	  * @throws IllegalArgumentException When file is not source file.
	  * @throws CompilationException When some error occured during compilation.
	  * @return Open files
	  */
	def run = {
		try {
			val jc	= ToolProvider.getSystemJavaCompiler()
			val fileManager = jc.getStandardFileManager(null, null, null)
			val units = fileManager.getJavaFileObjectsFromFiles(files.asJava)
			
			val result = jc.getTask(null, fileManager, null, flags.asJava, null, units).call() // TODO
			if (!result) throw new CompilationException("Compilation failed")
		} catch {
			case e: RuntimeException => throw new CompilationException("Unrecoverable error occurred", e)
			case e: IllegalStateException => throw new CompilationException("'call' called more than once", e)
		}
	}

	def jar(jarFile: File, files: Traversable[File]) = {
		val buffer = new Array[Byte](10*1024)

		val stream = new FileOutputStream(jarFile)
		val out = new JarOutputStream(stream, new Manifest())

		files.foreach{ f =>
			if (f == null || !f.exists() || f.isDirectory()) throw new IllegalArgumentException("File corruption during JAR creation - '" + f.getAbsolutePath() + "'")

			val jarAdd = new JarEntry(f.getName())
			jarAdd.setTime(f.lastModified())
			out.putNextEntry(jarAdd)

			val in = new FileInputStream(f)
			breakable { while (true) {
				val nRead: Int = in.read(buffer, 0, buffer.length)
				if (nRead <= 0) break
				out.write(buffer, 0, nRead)
			}}
			in.close();
		}

		out.close();
		stream.close();		
	}

}
