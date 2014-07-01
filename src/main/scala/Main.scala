package org.omp4j

import java.io._
import org.omp4j.preprocessor.exception._
import org.omp4j.preprocessor.Preprocessor

/** The omp4j preprocessor entry point.
  *
  * Handle flags and start preprocessing the files passed as program parametr.
  * javac [ options ] [ sourcefiles ] [ classes ] [ @argfiles ]
  * @param args Files and flags as Strings
  */
object Main extends App {
	try {
		val (flags, fileNames) = splitArgs(args)
		val files = openFiles(fileNames)

		val p = new Preprocessor(files)
		p.run
	} catch {
		case e: IllegalArgumentException => println(e.getMessage())
		case e: ParseException => 
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
	  * @throws IllegalArgumentException When non-existing file is passed or file is not readable
	  * @param fileNames String relative paths
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

}
