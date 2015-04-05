package org.omp4j

import org.omp4j.preprocessor.Preprocessor
import org.omp4j.system.Compiler
import org.omp4j.utils.{FileDuplicator, FileTreeWalker}

import java.io.File

/** The omp4j preprocessor entry point.
  *
  * Handle flags and start preprocessing the files passed as program parametr.
  * @param args Same as javac = [ options ] [ sourcefiles ] [ classes ] [ @argfiles ]
  */
object Main extends App {

	var toDelete: File = null
	try {
		val conf = new Config(args)     // set up configuration based on program arguments
		val prep = new Preprocessor()(conf)     // create preprocessor

		val (translatedFiles, (tmpDir, prepDir)::_) = prep.run()        // fetch the array of (already saved) preprocessed files
		toDelete = tmpDir

		val compiler = new Compiler(translatedFiles)(conf)    // set up compiler with possible addition files such as omp4j runtime ones (if not installed already)

		if (!conf.sourceOnly) {
			val destDir = if (conf.destdir != null) conf.destdir else "."
			conf copyRuntimeClassesTo new File(destDir)
			compiler.compile(destDir) // and compile!
		}

		if (conf.srcdir != null) {
			FileDuplicator.dirToDir(prepDir, new File(conf.srcdir))
		}

	} catch {
		// TODO: exceptions
//		case e: IllegalArgumentException => e.printStackTrace
//		case e: MalformedURLException    => e.printStackTrace
//		case e: SecurityException        => e.printStackTrace
//		case e: CompilationException     => e.printStackTrace
//		case e: ParseException           => e.printStackTrace
//		case e: SyntaxErrorException     => e.printStackTrace
//		case e: RuntimeException         => e.printStackTrace
		// TODO: help
		case e: Exception => e.printStackTrace  // TODO:
	} finally {
		FileTreeWalker.recursiveDelete(toDelete)
	}
}
