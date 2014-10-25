package org.omp4j.system

import java.io._
import java.util.jar._
import javax.tools._

import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.utils.FileTreeWalker

import scala.collection.JavaConverters._
import scala.util.control.Breaks._

/** Handler for JavaCompiler. All settings are passed (implicitelly) by Config */
class Compiler(files: Array[File], flags: Array[String])(implicit conf: Config) {

	/** Compile sources
	  * @throws IllegalArgumentException When file is not source file.
	  * @throws CompilationException When some error occured during compilation.
	  */
	def compile = {
		try {
			val jc = ToolProvider.getSystemJavaCompiler
			val fileManager = jc.getStandardFileManager(null, null, null)
			// val units = fileManager.getJavaFileObjectsFromFiles(conf.files.toIterable.asJava)
			val units = fileManager.getJavaFileObjectsFromFiles(files.toIterable.asJava)

			val result = jc.getTask(null, fileManager, null, flags.toIterable.asJava, null, units).call // TODO
			if (!result) throw new CompilationException("Compilation failed")
		} catch {
			case e: RuntimeException      => throw new CompilationException("Unrecoverable error occurred", e)
			case e: IllegalStateException => throw new CompilationException("'call' called more than once", e)
		}
	}

	/** Pack sources to JAR
	  * @throws IllegalArgumentException When class-file to be packed is corrupted or missing
	  */
	def jar(target: File) = {
		val buffer = new Array[Byte](10*1024)	// TODO: size?

		val stream = new FileOutputStream(target)
		val out = new JarOutputStream(stream, new Manifest)

		// classes to be packed
		val classFiles: Array[java.io.File] = FileTreeWalker.recursiveListFiles(conf.workDir)
			.filter(f => f.isFile && f.getName.endsWith(".class"))
		
		// pack each file
		classFiles.foreach{ f =>
			if (f == null) throw new IllegalArgumentException("File corruption during JAR creation - null file passed")
			else if (!f.exists || !f.isFile) throw new IllegalArgumentException("File corruption during JAR creation - '" + f.getAbsolutePath + "'")

			val relativePath = s"${conf.workDir.getAbsolutePath}/".r.replaceFirstIn(f.getAbsolutePath, "")	// always works
			val jarAdd = new JarEntry(relativePath)
			jarAdd.setTime(f.lastModified)
			out.putNextEntry(jarAdd)

			val in = new FileInputStream(f)
			breakable { while (true) {
				val nRead: Int = in.read(buffer, 0, buffer.length)
				if (nRead <= 0) break
				out.write(buffer, 0, nRead)
			}}
			in.close
		}

		// close all streams
		out.close
		stream.close
	}
}
