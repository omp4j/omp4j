package org.omp4j

import java.io._

import org.omp4j.exception.HelpRequiredException
import org.omp4j.preprocessor.TokenSet
import org.omp4j.system._
import org.omp4j.utils.{FileDuplicator, Keywords, TmpDir}

import scala.Array._
import scala.collection.mutable.ArrayBuffer

object Config {
	val PREPROCESSED = "preprocessed"
	val VALIDATION = "validation"
	val COMPILATION = "compilation"
}

/** Configuration for compiler and other classes. Use implicitally. */
class Config(args: Array[String], level: Int = 1, val runtimePath: String = s"org${File.separator}omp4j${File.separator}runtime", val runtimeClasses: List[String] = List("AbstractExecutor", "DynamicExecutor", "DynamicExecutor$DynamicExecutorThread", "IOMPExecutor", "StaticExecutor", "StaticExecutor$StaticExecutorThread")) {

	/** working directory */
	val workDir: File = createWorkingDir

	/** directory of preprocessed sources */
	val preprocessedDir: File = new TmpDir(workDir, Config.PREPROCESSED).toFile

	/** directory of sources without threadId methods */
	val validationDir: File = new TmpDir(workDir, Config.VALIDATION).toFile

	/** directory of binary classes (without threadId methods) */
	val compilationDir: File = new TmpDir(workDir, Config.COMPILATION).toFile

	/** tmp JAR file */
	val jar: File = new File(compilationDir.getAbsolutePath + File.separator + "output.jar")

	/** javac flags and file names */
	/** files to be preprocessed (and compiled) */
	lazy val files: Array[File] = openFiles(fileNames)

	/** Loader for the jar defined above */
	var loader: Loader = new Loader(jar)

	/** flags for first compilation */
	lazy val (optDir: File, firstCompFlags: Array[String]) = getOptDirAndFirstCompFlags

	/** set of all used strings */
	val tokenSet = new TokenSet

	private val fileNamesBuffer = new ArrayBuffer[String]()
	private val flagsBuffer = new ArrayBuffer[String]()

	var destdir: String = null
	var srcdir: String = null
	var verbose: Boolean = false
	var sourceOnly: Boolean = false
	var classpath: String = null

	var fileNames = Array[String]()
	var flags = Array[String]()
	var allFlags = Array[String]()

	// constructor
	if (level == 1) {
		processArgs(args)
		fetchVars()
	}
	if (destdir != null) new File(destdir).mkdirs()
	if (srcdir != null) new File(srcdir).mkdirs()


	private def fetchVars() = {
		fileNames = fileNamesBuffer.toArray
		flags = flagsBuffer.toArray

		val allFlagsBuffer = new ArrayBuffer[String]()
		allFlagsBuffer ++= flags.toList

		if (destdir != null) allFlagsBuffer ++= List("-d", destdir)
		if (classpath != null) allFlagsBuffer ++= List("-classpath", classpath)

		val allFlags = allFlagsBuffer.toArray
	}

	private def processArgs(args: Array[String]): Unit = {
		if (args.length > 0) {
			args.head match {
				case "-d" | "--destdir" =>
					//flagsBuffer += "-d"
					//flagsBuffer += args.tail.head

					destdir = args.tail.head
					processArgs(args.tail.tail)
				case "-s" | "--srcdir" =>
					srcdir = args.tail.head
					processArgs(args.tail.tail)
				case "-cp" | "-classpath" | "--classpath" =>
					//flagsBuffer += "-classpath"
					//flagsBuffer += args.tail.head

					classpath = args.tail.head
					processArgs(args.tail.tail)
				case "-v" | "-verbose" | "--verbose" =>
					flagsBuffer += "-verbose"
					verbose = true
					processArgs(args.tail)
				case "-n" | "-srconly" | "--source-only" | "--no-compile" =>
					sourceOnly = true
					processArgs(args.tail)
				case "-h" | "-help" | "--help" =>
					throw new HelpRequiredException()
				case "--" =>
					fileNamesBuffer ++= args.tail.toList
				case _ =>
					if (Keywords.JAVAC_SINGLE_OPTS_FULL contains args.head) {
						flagsBuffer += args.head
						processArgs(args.tail)
					} else if (Keywords.JAVAC_SINGLE_OPTS_START.count(_ == args.head) > 0) {
						flagsBuffer += args.head
						processArgs(args.tail)
					} else if (Keywords.JAVAC_DOUBLE_OPTS contains args.head) {
						flagsBuffer += args.head
						flagsBuffer += args.tail.head
						processArgs(args.tail.tail)
					} else {
						fileNamesBuffer += args.head
						processArgs(args.tail)
					}
			}
		}
	}


	/** TODO: doc */
	private def getOptDirAndFirstCompFlags = {

		// last index of flags
		val lastIdx: Int = flags.size - 1

		flags.indexOf("-d") match {
			case -1 =>
				val od = new File(".")
				val fcf = concat(flags, Array("-d", workDir.getAbsolutePath))
				(od, fcf)
			case `lastIdx` => throw new IllegalArgumentException("Missing value for '-d'")  // TODO: default . ??
			case idx: Int =>
				val od = new File(flags(idx + 1))
				val fcf = flags.updated(idx + 1, workDir.getAbsolutePath)
				(od, fcf)
		}
	}

	/** Get files based on their string paths
	  * @param fileNames String relative paths
	  * @throws IllegalArgumentException When non-existing file is passed or file is not readable
	  * @return Open files
	  */
	private def openFiles(fileNames: Array[String]) = {
		if (fileNames.size == 0) throw new IllegalArgumentException("No files passed")

		val files = fileNames.map(new File(_))
		files.foreach{ f =>
			// TODO: string format
			if (!f.exists)  throw new IllegalArgumentException("File '" + f.getPath + "' does not exist")
			if (!f.canRead) throw new IllegalArgumentException("Missing read permission for file '" + f.getPath + "'")
		}
		files
	}

	/** Create unique tmp directory
	  * @throws RuntimeException When either property 'java.io.tmpdir' does not exist or is invalid
	  * @return Tmp directory
	  */
	private def createWorkingDir: File = {
		if (System.getProperty("java.io.tmpdir") == null) throw new RuntimeException("Property 'java.io.tmpdir' not set.")

		val tmpRootStr = System.getProperty("java.io.tmpdir")
		val tmpRootFile = new File(tmpRootStr)

		if (!tmpRootFile.exists) throw new RuntimeException("Directory described in property 'java.io.tmpdir' does not exist.")
		// TODO: test writability

		// TODO: use hidden (.*)
		(new TmpDir(tmpRootFile, "omp4j")).toFile
	}

	def nextLevel(nextLvlFiles: Array[File]): Config = {
		//val c = this
		val c = new Config(args, level + 1, runtimePath, runtimeClasses) {
			override lazy val files = nextLvlFiles
		}
		c.fileNames  = nextLvlFiles.map(_.getAbsolutePath)
		c.flags      = c.flags
		c.allFlags   = c.allFlags
		c.destdir    = c.destdir
		c.srcdir     = c.srcdir
		c.verbose    = c.verbose
		c.sourceOnly = c.sourceOnly
		c.classpath  = c.classpath

		c
	}

	def copyRuntimeClassesTo(rootDir: File) = {

		val runtimeDir = new File(s"${rootDir.getAbsolutePath}${File.separator}$runtimePath")
		runtimeDir.mkdirs()

		runtimeClasses.foreach{ name =>
			val inputStream = getClass.getResourceAsStream(s"/$runtimePath${File.separator}$name.class")
			val outputFile = new File(runtimeDir, s"$name.class")
			FileDuplicator.streamToFile(inputStream, outputFile)
		}

	}

}
