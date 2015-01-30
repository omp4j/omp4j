package org.omp4j.system

import java.io._
import java.util.jar._
import javax.tools._

import org.omp4j.Config
import org.omp4j.exception._
import org.omp4j.utils.FileTreeWalker

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

/** Handler for JavaCompiler. All settings are passed (implicitelly) by Config */
class Compiler(files: Array[File])(implicit conf: Config) {

	// TODO: doc
	private lazy val jc = ToolProvider.getSystemJavaCompiler
	private lazy val fileManager = jc.getStandardFileManager(null, null, null)

	/** Compile sources
	  * @throws IllegalArgumentException When file is not source file.
	  * @throws CompilationException When some error occurred during compilation.
	  */
	def compile(additionalFlags: List[(String, String)] = List()) = {
		try {

			val flagBuffer = ArrayBuffer[String]()
			flagBuffer ++= conf.flags

			additionalFlags.foreach{case (left, right) =>
				flagBuffer.indexOf(left) match {
					case -1 => flagBuffer++=List(left, right)
					case i  => {
						flagBuffer.remove(i)
						if (right != null) flagBuffer.remove(i)
						flagBuffer+=left
						if (right != null) flagBuffer+=right
					}
				}
			}
			// the really used flags
			val flags = flagBuffer.toArray.toIterable.asJava

			val units = fileManager.getJavaFileObjectsFromFiles(files.toIterable.asJava)
			val result = jc.getTask(null, fileManager, null, flags, null, units).call // TODO

			if (!result) throw new CompilationException("Compilation failed")
		} catch {
			case e: RuntimeException      => throw new CompilationException("Unrecoverable error occurred", e)
			case e: IllegalStateException => throw new CompilationException("'call' called more than once", e)
		}
	}

	/** Pack sources to JAR
	  * @throws IllegalArgumentException When class-file to be packed is corrupted or missing
	  */
	def jar() = {
		val buffer = new Array[Byte](10*1024)	// TODO: size?

		val stream = new FileOutputStream(conf.jar)
		val out = new JarOutputStream(stream, new Manifest)

		// classes to be packed
		val classFiles: Array[java.io.File] = FileTreeWalker.recursiveListFiles(conf.compilationDir)   // TODO: !!!! originally workdir
			.filter(f => f.isFile && f.getName.endsWith(".class"))
		
		// pack each file
		classFiles.foreach{ f =>
			if (f == null) throw new IllegalArgumentException("File corruption during JAR creation - null file passed")
			else if (!f.exists || !f.isFile) throw new IllegalArgumentException("File corruption during JAR creation - '" + f.getAbsolutePath + "'")

			val relativePath = s"${conf.compilationDir.getAbsolutePath}${File.separator}".r.replaceFirstIn(f.getAbsolutePath, "")	// always works
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
