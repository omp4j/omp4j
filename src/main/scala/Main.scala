package org.omp4j

import java.io.File
import java.net.MalformedURLException

import org.omp4j.exception._
import org.omp4j.preprocessor.Preprocessor

/** The omp4j preprocessor entry point.
  *
  * Handle flags and start preprocessing the files passed as program parametr.
  * @param args Same as javac = [ options ] [ sourcefiles ] [ classes ] [ @argfiles ]
  */
object Main extends App {
	// pass config to all classes implicitelly
	implicit var conf: Config = null

	// run omp4j main lifecycle
	try {
		conf = Config/*.create*/(args)
		run
		cleanup
	} catch {
		case e: IllegalArgumentException => println(e.getMessage())
		case e: MalformedURLException    => println(e.getMessage())
		case e: SecurityException        => println(e.getMessage())
		case e: CompilationException     => println(e.getMessage() + ": " + e.getCause().getMessage())
		case e: ParseException           => println(e.getMessage() + ": " + e.getCause().getMessage())
		case e: SyntaxErrorException     => println(e.getMessage() + ": " + e.getCause().getMessage())
		case e: RuntimeException         => println(e.getMessage())
		// unexpected exception
		case e: Exception                => e.printStackTrace()
	}

	/** Create and run preprocessor */
	def run = {
		val preprocessor = new Preprocessor(conf.files)
		preprocessor.run
	}

	/** Delete workdir */
	def cleanup = {
		recusiveDelete(conf.workDir)
	}

	/** Delete file recursively
	  * @param f file to delete
	  */
	private def recusiveDelete(f: File): Unit = {
		if (f.isFile()) f.delete()
		else if (f.isDirectory()) {
			f.list().foreach{ g => recusiveDelete(new File(f.getAbsolutePath() + "/" + g)) }
			f.delete()
		}
	}
}
