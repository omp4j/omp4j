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

	/** Java compiler provided by JVM */
	private lazy val jc = ToolProvider.getSystemJavaCompiler

	/** File manager used for file compilation*/
	private lazy val fileManager = jc.getStandardFileManager(null, null, null)

	/** Compile sources, params: -d, -cp, others
	  * @throws IllegalArgumentException When file is not source file.
	  * @throws CompilationException When some error occurred during compilation.
	  */
	def compile(destDir: String, addCP: String = null, additionalFlags: List[(String, String)] = List()) = {

		val flagBuffer = ArrayBuffer[String]()
		flagBuffer ++= conf.flags.toList

		flagBuffer ++= Array("-d", destDir)
		if (addCP != null) {
			if (conf.classpath != null) flagBuffer ++= List("-classpath", s"$addCP${File.pathSeparator}${conf.classpath}")
			else flagBuffer ++= List("-classpath", addCP)
		} else if (conf.classpath != null) flagBuffer ++= List("-classpath", {conf.classpath})

		// the really used flags
		val flags = flagBuffer.toArray.toIterable.asJava

		try {
			val units = fileManager.getJavaFileObjectsFromFiles(files.toIterable.asJava)
			val result = jc.getTask(null, fileManager, null, flags, null, units).call

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
		val buffer = new Array[Byte](10*1024)	// TODO: size? maybe configurable

		val stream = new FileOutputStream(conf.jar)
		val out = new JarOutputStream(stream, new Manifest)

		// classes to be packed
		val classFiles: Array[File] = FileTreeWalker.recursiveListFiles(conf.compilationDir)
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
