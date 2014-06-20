package org.omp4j

import org.omp4j.preprocessor.Preprocessor

/** The omp4j preprocessor entry point.
  *
  * Handle flags and start preprocessing the files passed as program parametr.
  * @param args files and flags as Strings
  */
object Main extends App {
	val p = new Preprocessor(args)
	p.run()
}
