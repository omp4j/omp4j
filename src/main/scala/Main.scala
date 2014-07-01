package org.omp4j

import java.io._
import java.nio.file.Files

import org.omp4j.preprocessor.exception._
import org.omp4j.preprocessor.Preprocessor
import org.omp4j.compiler.Compiler

/** The omp4j preprocessor entry point.
  *
  * Handle flags and start preprocessing the files passed as program parametr.
  * javac [ options ] [ sourcefiles ] [ classes ] [ @argfiles ]
  * @param args Files and flags as Strings
  */
object Main extends App {
	try {
		// working directory, free to do anything
		val workDir: File = createWorkingDir
		// println(workDir.getAbsolutePath())

		// handle flags
		val (flags, fileNames) = splitArgs(args)
		// opened files
		val files = openFiles(fileNames)

		// compilation before preprocessing
		val compiler = new Compiler(flags++Array("-d", workDir.getAbsolutePath()), files, null, null) // TODO
		compiler.run

		val jar = new File(workDir.getAbsolutePath() + "/output.jar")
		val classFiles = workDir.list(
			new FilenameFilter() { def accept(dir: File, name: String) = name.endsWith(".class") }
		).map{f => new File(workDir.getAbsolutePath() + "/" + f) }
		compiler.jar(jar, classFiles)
		
		// preprocessing
		val preprocessor = new Preprocessor(files)
		// preprocessor.run

		// result compilation
		// TODO

		// cleanup
		// workDir.delete()
	} catch {
		case e: IllegalArgumentException => println(e.getMessage())
		case e: CompilationException => println(e.getMessage() + ": " + e.getCause().getMessage())
		case e: ParseException => ;
		case e: RuntimeException => println(e.getMessage())
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

	/** Creates unique tmp directory
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
