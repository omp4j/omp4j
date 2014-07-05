package org.omp4j.compiler

import java.io._
import java.util.jar._
import javax.tools._
import scala.collection.JavaConverters._
import scala.util.control.Breaks._

import org.omp4j.Config
import org.omp4j.exception._

/** Handler for JavaCompiler. All settings are passed (implicitelly) by Config */
class Compiler(implicit conf: Config) {

	/** Compile sources
	  * @throws IllegalArgumentException When file is not source file.
	  * @throws CompilationException When some error occured during compilation.
	  */
	def compile = {
		try {
			val jc	= ToolProvider.getSystemJavaCompiler()
			val fileManager = jc.getStandardFileManager(null, null, null)
			val units = fileManager.getJavaFileObjectsFromFiles(conf.files.asJava)
			
			val result = jc.getTask(null, fileManager, null, conf.flags.asJava, null, units).call() // TODO
			if (!result) throw new CompilationException("Compilation failed")
		} catch {
			case e: RuntimeException => throw new CompilationException("Unrecoverable error occurred", e)
			case e: IllegalStateException => throw new CompilationException("'call' called more than once", e)
		}
	}

	/** Pack sources to JAR
	  * @throws IllegalArgumentException When class-file to be packed is corrupted or missing
	  */
	def jar = {
		val buffer = new Array[Byte](10*1024)

		val stream = new FileOutputStream(conf.jar)
		val out = new JarOutputStream(stream, new Manifest())

		// classes to be packed
		val classFiles = conf.workDir.list(
			new FilenameFilter() { def accept(dir: File, name: String) = name.endsWith(".class") }
		).map{ f => new File(conf.workDir.getAbsolutePath() + "/" + f) }

		// pack each file
		classFiles.foreach{ f =>
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

		// close all streams
		out.close();
		stream.close();		
	}
}
