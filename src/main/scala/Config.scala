package org.omp4j

import Array._

import java.io._
import java.nio.file.Files

import org.omp4j.exception._
import org.omp4j.loader.Loader
import org.omp4j.compiler.Compiler
import org.omp4j.preprocessor.Preprocessor

/** Configuration for compiler and other classes. Use implicitally. */
class Config(args: Array[String]) {
	
	/** working directory */
	lazy val workDir: File = createWorkingDir

	/** directory of preprocessed sources */
	lazy val prepDir: File = Files.createTempDirectory(workDir.toPath, "preprocessed-").toFile

	/** tmp JAR file */
	lazy val jar: File = new File(workDir.getAbsolutePath + "/output.jar")

	/** javac flags and file names */
	lazy val (flags: Array[String], fileNames: Array[String]) = splitArgs(args)

	/** files to be preprocessed (and compiled) */
	lazy val files: Array[File] = openFiles(fileNames)

	/** Loader for the jar defined above */
	var loader: Loader = null

	/** flags for first compilation */
	lazy val (optDir: File, firstCompFlags: Array[String]) = getOptDirAndFirstCompFlags

	def init = {
		initCompile
		load
	}

	/** compile before preprocessing */
	private def initCompile = {
		val compiler = new Compiler(files, firstCompFlags)(this)
		compiler.compile
		compiler.jar(jar)
	}

	/** set the loader */
	private def load = {
		loader = new Loader(jar)
	}

	/** */
	private def getOptDirAndFirstCompFlags = {

		// last index of flags
		val lastIdx: Int = flags.size - 1

		flags.indexOf("-d") match {
			case -1 =>
				val od = new File(".")
				val fcf = concat(flags, Array("-d", workDir.getAbsolutePath))
				(od, fcf)
			case `lastIdx` => throw new IllegalArgumentException("Missing value for '-d'")
			case idx: Int =>
				val od = new File(flags(idx + 1))
				val fcf = flags.updated(idx + 1, workDir.getAbsolutePath)
				(od, fcf)
		}
	}

	/** Split args to list of flags and list of file names
	  * @param args Array of String arguments
	  * @return Tuple of Arrays (flags, fileNames)
	  */
	private def splitArgs(args: Array[String]): (Array[String], Array[String]) = {
		// TODO: case -1
		args.indexOf("--") match {
			case -1 => (Array[String](), args)
			case id => (args.take(id), args.drop(id+1))
		}
	}

	/** Get files based on their string paths
	  * @param fileNames String relative paths
	  * @throws IllegalArgumentException When non-existing file is passed or file is not readable
	  * @return Open files
	  */
	private def openFiles(fileNames: Array[String]) = {
		if (fileNames.size == 0) throw new IllegalArgumentException("No files passed")

		val files = fileNames.map(new File(_))
		files.foreach{ f =>
			if (!f.exists)  throw new IllegalArgumentException("File '" + f.getPath + "' does not exist")
			if (!f.canRead) throw new IllegalArgumentException("Missing read permission for file '" + f.getPath + "'")
		}
		files
	}

	/** Create unique tmp directory
	  * @throws RuntimeException When either property 'java.io.tmpdir' does not exist or is invalid
	  * @return Tmp directory
	  */
	private def createWorkingDir: File = {
		if (System.getProperty("java.io.tmpdir") == null) throw new RuntimeException("Property 'java.io.tmpdir' not set.")

		val tmpRootStr = System.getProperty("java.io.tmpdir")
		val tmpRootFile = new File(tmpRootStr)

		if (!tmpRootFile.exists) throw new RuntimeException("Directory described in property 'java.io.tmpdir' does not exist.")
		// TODO: test writability

		// TODO: use hidden (.*)
		val tmpPath = Files.createTempDirectory(tmpRootFile.toPath, "omp4j-")
		tmpPath.toFile
	}
}
