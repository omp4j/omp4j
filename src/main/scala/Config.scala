package org.omp4j

import java.io._

import org.omp4j.preprocessor.TokenSet
import org.omp4j.system._
import org.omp4j.utils.TmpDir

import scala.Array._

/** Configuration for compiler and other classes. Use implicitally. */
class Config(args: Array[String]) {
	
	/** working directory */
	val workDir: File = createWorkingDir

	/** directory of preprocessed sources */
	val preprocessedDir: File = new TmpDir(workDir, "preprocessed").toFile

	/** directory of sources without threadId methods */
	val validationDir: File = new TmpDir(workDir, "validation").toFile

	/** directory of binary classes (without threadId methods) */
	val compilationDir: File = new TmpDir(workDir, "compilation").toFile

	/** tmp JAR file */
	val jar: File = new File(compilationDir.getAbsolutePath + File.separator + "output.jar")

	/** javac flags and file names */
	lazy val (flags: Array[String], fileNames: Array[String]) = splitArgs(args)

	/** files to be preprocessed (and compiled) */
	lazy val files: Array[File] = openFiles(fileNames)

	/** Loader for the jar defined above */
	var loader: Loader = new Loader(jar)

	/** flags for first compilation */
	lazy val (optDir: File, firstCompFlags: Array[String]) = getOptDirAndFirstCompFlags

	/** set of all used strings */
	val tokenSet = new TokenSet

	/** TODO: doc */
	private def getOptDirAndFirstCompFlags = {

		// last index of flags
		val lastIdx: Int = flags.size - 1

		flags.indexOf("-d") match {
			case -1 =>
				val od = new File(".")
				val fcf = concat(flags, Array("-d", workDir.getAbsolutePath))
				(od, fcf)
			case `lastIdx` => throw new IllegalArgumentException("Missing value for '-d'")  // TODO: default . ??
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
			// TODO: string format
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
		(new TmpDir(tmpRootFile, "omp4j")).toFile
	}
}
