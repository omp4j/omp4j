package org.omp4j

import Array._

import java.io._
import java.nio.file.Files

import org.omp4j.exception._
import org.omp4j.loader.Loader
import org.omp4j.compiler.Compiler
import org.omp4j.preprocessor.Preprocessor

/** Configuration for compiler and other classes. Use implicitally. Use Config factory.*/
class Config {
	/** working directory */
	var workDir: File = null

	/** javac flags */
	var flags: Iterable[String]  = null
	
	/** files to be preprocessed (and compiled) */
	var files: Iterable[File] = null
	
	/** tmp JAR file */
	var jar: File = null

	/** ClassLoader for the jar defined above */
	var classLoader: ClassLoader = null
}

/** Config factory */
object Config {

	/** Make (compilation included) config based on	String args passed
	  * @param args Program params
	  * @throws almost everything
	  * @return new configuration
	  */
	def apply(args: Array[String]) = {
		val config = new Config
		
		config.workDir = createWorkingDir	// working directory, free to do anything
		config.jar = new File(config.workDir.getAbsolutePath() + "/output.jar")	// prepare jar-file

		val (flags, fileNames) = splitArgs(args)	// handle flags
		config.files = openFiles(fileNames)	// opened files

		val tmpFlags = concat(flags, Array("-d", config.workDir.getAbsolutePath()))
		config.flags = tmpFlags

		// compilation before preprocessing
		val compiler = new Compiler()(config)
		compiler.compile
		compiler.jar
		config.classLoader = (new Loader ).load(config.jar)

		config
	}

	/** Split args to list of flags and list of file names
	  * @param args Array of String arguments
	  * @return Tuple of Arrays (flags, fileNames)
	  */
	private def splitArgs(args: Array[String]): (Array[String], Array[String]) = {
		(Array[String](), args)
		// TODO: filter
	}

	/** Get files based on their string paths
	  * @param fileNames String relative paths
	  * @throws IllegalArgumentException When non-existing file is passed or file is not readable
	  * @return Open files
	  */
	private def openFiles(fileNames: Array[String]) = {
		if (fileNames.size == 0)  throw new IllegalArgumentException("No files passed")

		val files = fileNames.map{ new File(_) }
		files.foreach{ f =>
			if (!f.exists())  throw new IllegalArgumentException("File '" + f.getPath() + "' does not exist")
			if (!f.canRead()) throw new IllegalArgumentException("Missing read permission for file '" + f.getPath() + "'")
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

		if (!tmpRootFile.exists()) throw new RuntimeException("Directory described in property 'java.io.tmpdir' does not exist.")

		val tmpPath = Files.createTempDirectory(tmpRootFile.toPath(), "omp4j-")
		tmpPath.toFile()
	}

}
