package org.omp4j

import java.io.File

/** Configuration for compiler and other classes. Use implicitally. */
case class Config {
	/** working directory */
	var workDir: File = null

	/** javac flags */
	var flags: Iterable[String]  = null
	
	/** files to be preprocessed (and compiled) */
	var files: Iterable[File] = null
	
	/** tmp JAR file */
	var jar: File = null

	/** Store passed variables */
	def store(
		workDirP: File,
		flagsP: Iterable[String],
		filesP: Iterable[File],
		jarP: File ) = {
		workDir = workDirP
		flags = flagsP
		files = filesP
		jar = jarP
	}
}
