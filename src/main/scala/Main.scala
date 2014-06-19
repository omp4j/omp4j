
import org.omp4j.preprocessor.Preprocessor

package org.omp4j {

/** The omp4j preprocessor entry point.
  *
  * Handle flags and start preprocessing files passed as parametr.
  * @param args files and flags as Strings
  */
object Main {
	def main(args: Array[String]): Unit = {
		val p = new Preprocessor(args)
		p.run()
	}
	
}

}	// package
