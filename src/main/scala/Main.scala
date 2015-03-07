package org.omp4j

import org.omp4j.preprocessor.Preprocessor
import org.omp4j.system.Compiler
import org.omp4j.utils.FileTreeWalker

/** The omp4j preprocessor entry point.
  *
  * Handle flags and start preprocessing the files passed as program parametr.
  * @param args Same as javac = [ options ] [ sourcefiles ] [ classes ] [ @argfiles ]
  */
object Main extends App {
	val conf = new Config(args)     // set up configuration based on program arguments
	val prep = new Preprocessor()(conf)     // create preprocessor

	try {
		val translatedFiles = prep.run  // fetch the array of (already saved) preprocessed files
		val compiler = new Compiler(translatedFiles ++ FileTreeWalker.getRuntimeFiles)(conf)    // set up compiler with possible addition files such as omp4j runtime ones (if not installed already)
		compiler.compile()      // and compile! (this is optional, one can terminate once the preprocessed files are saved)
	} catch {
		// TODO: exceptions
//		case e: IllegalArgumentException => e.printStackTrace
//		case e: MalformedURLException    => e.printStackTrace
//		case e: SecurityException        => e.printStackTrace
//		case e: CompilationException     => e.printStackTrace
//		case e: ParseException           => e.printStackTrace
//		case e: SyntaxErrorException     => e.printStackTrace
//		case e: RuntimeException         => e.printStackTrace
		case e: Exception => e.printStackTrace  // TODO:
	}
}
