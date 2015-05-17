package org.omp4j

import org.omp4j.exception.HelpRequiredException
import org.omp4j.preprocessor.Preprocessor
import org.omp4j.system.Compiler
import org.omp4j.utils.{FileDuplicator, FileTreeWalker}

import java.io.File

import scala.collection.:+

/** The omp4j preprocessor entry point.
  *
  * Handles the CLI options and starts preprocessing the files passed as program parameter.
  * Usage is similar to `javac`: [ options ] [ sourcefiles ] [ classes ] [ @argfiles ]
  */
object Main extends App {

	var toDelete: List[File] = null
	val tmpDirs: List[(File, File)] = null
	var verbose = false
	var conf: Config = null

	try {
		conf = new Config(args)     // set up configuration based on program arguments
		conf.logger.log(s"The configuration context created")

		val prep = new Preprocessor()(conf)     // create preprocessor

		val (translatedFiles, dirs) = prep.run()
		val tmpDirs :+ ((tmpDir, prepDir)) :+ ((lastDir, lastPrepDir)): List[(File, File)] = dirs
		toDelete = tmpDir :: prepDir :: lastDir :: lastPrepDir :: tmpDirs.foldLeft[List[File]](List()){ case (z, (a,b)) => a :: b :: z}

		val destDir = if (conf.destdir != null) conf.destdir else "."
		conf.logger.log(s"Destination directory set to '$destDir'")

		if (conf.sourceOnly) {
			conf.logger.log("Copying translated source files into destination directory...")
			FileDuplicator.dirToDir(prepDir, new File(destDir))
			conf.logger.log("Done")
		} else {
			conf.logger.log("Compiling translated source files into bytecode...")
			val compiler = new Compiler(translatedFiles)(conf)    // set up compiler with possible addition files such as omp4j runtime ones (if not installed already)
			conf copyRuntimeClassesTo new File(destDir)
			compiler.compile(destDir) // and compile!
			conf.logger.log("Done")
		}

		// TODO: wtf
		if (conf.srcdir != null) {
			FileDuplicator.dirToDir(prepDir, new File(conf.srcdir))
		}

	} catch {
		case e: HelpRequiredException => printHelp()
		case e: Exception =>
			if (conf.verbose) e.printStackTrace()
			else println(e.getMessage)

	} finally {
		conf.logger.log("Deleting work directories...")
		toDelete.foreach(FileTreeWalker.recursiveDelete)
		conf.logger.log("Done")
	}

	/** Print help, options and examples */
	def printHelp() = {
		println("omp4j - the OpenMP-like preprocessor\n" +
			"http://www.omp4j.org\n" +
			"Author: Petr Belohlavek <omp4j@petrbel.cz>\n" +
			"------------------------------------------\n" +
			"Execution: omp4j [params] [files]\n" +
			"Supported options are the same as the options supported by javac\n" +
			"Additionally, the option listed below are supported:\n" +
			"-d <dir>\tDirectory where preprocessed/compiled classes are stored.\n" +
			"-h\tPrint help\n" +
			"-n\tDo not compile preprocessed sources. Store only .java files.\n" +
			"-v\tProvide progress information\n" +
			"Please refer to www.omp4j.org/tutorial for more information\n" +
			"------------------------------------------\n" +
			"Example executions:\n" +
			"$ omp4j -d classes MyClass1.java MyClass2.java # javac-like behavior\n" +
			"$ omp4j -d sources -v -n MyClass1.java MyClass2.java # preprocess only")
	}
}
