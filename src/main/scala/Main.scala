package org.omp4j

import org.omp4j.preprocessor.Preprocessor

/** The omp4j preprocessor entry point.
  *
  * Handle flags and start preprocessing the files passed as program parametr.
  * @param args Same as javac = [ options ] [ sourcefiles ] [ classes ] [ @argfiles ]
  */
object Main extends App {
	val conf = new Config(args)
	val prep = new Preprocessor()(conf)
	prep.run match {
		case 0 => ;
		case e => System.exit(e)        // TODO: exceptions
	}
}
