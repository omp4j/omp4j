package org.omp4j

import java.io._

import org.omp4j.exception.HelpRequiredException
import org.omp4j.preprocessor.TokenSet
import org.omp4j.system._
import org.omp4j.utils.{FileDuplicator, Keywords, TmpDir}

import scala.Array._
import scala.collection.mutable.ArrayBuffer

/** Config companion object that stores directory structure. */
object Config {
	val PREPROCESSED = "preprocessed"
	val VALIDATION = "validation"
	val COMPILATION = "compilation"
}

/** Configuration context for compiler and other classes. Use implicitly.
  *
  * @constructor Setup new config context
  * @param args CLI arguments
  * @param level recursion level
  * @param runtimePath path of the runtime classes
  * @param runtimeClasses list of classes that are used in runtime
  */
class Config(args: Array[String], level: Int = 1, val runtimePath: String = s"org${File.separator}omp4j${File.separator}runtime", val runtimeClasses: List[String] = List("AbstractExecutor", "DynamicExecutor", "IOMPExecutor", "StaticExecutor", "StaticExecutor$1")) {

	/** Working directory */
	val workDir: File = createWorkingDir()

	/** Directory of preprocessed sources */
	val preprocessedDir: File = new TmpDir(workDir, Config.PREPROCESSED).toFile

	/** Directory of sources without threadId methods */
	val validationDir: File = new TmpDir(workDir, Config.VALIDATION).toFile

	/** Directory of binary classes (without threadId methods) */
	val compilationDir: File = new TmpDir(workDir, Config.COMPILATION).toFile

	/** Temporary JAR file */
	val jar: File = new File(compilationDir.getAbsolutePath + File.separator + "output.jar")

	/** Files to be preprocessed (and compiled) */
	lazy val files: Array[File] = openFiles(fileNames)

	/** Loader for the JAR defined in prior methods */
	var loader: Loader = new Loader(jar)

	/** Set of all used strings */
	val tokenSet = new TokenSet

	/** Buffer used for file names extraction */
	private val fileNamesBuffer = new ArrayBuffer[String]()

	/** Buffer used for CLI options extraction */
	private val flagsBuffer = new ArrayBuffer[String]()

	/** Destination directory */
	var destdir: String = null

	/** Source directory*/
	var srcdir: String = null

	/** Progress messages? */
	var verbose: Boolean = false

	/** Omit the final compilation? */
	var sourceOnly: Boolean = false

	/** Passed classpath*/
	var classpath: String = null

	/** Extracted file names from CLI options*/
	var fileNames = Array[String]()

	/** Extracted flags names from CLI options*/
	var flags = Array[String]()

	/** All used flags */
	var allFlags = Array[String]()

	/* constructor */
	if (level == 1) {
		processArgs(args)
		fetchVars()
	}
	if (destdir != null) new File(destdir).mkdirs()
	if (srcdir != null) new File(srcdir).mkdirs()
	/* /constructor */

	/** Init `allClass` buffer */
	private def fetchVars() = {
		fileNames = fileNamesBuffer.toArray
		flags = flagsBuffer.toArray

		val allFlagsBuffer = new ArrayBuffer[String]()
		allFlagsBuffer ++= flags.toList

		if (destdir != null) allFlagsBuffer ++= List("-d", destdir)
		if (classpath != null) allFlagsBuffer ++= List("-classpath", classpath)

		val allFlags = allFlagsBuffer.toArray
	}

	/** Process all arguments
	  *
	  *  @param args CLI parameters
	  */
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

	/** Get files based on their string paths
	  *
	  * @param fileNames String relative paths
	  * @throws IllegalArgumentException When non-existing file is passed or file is not readable
	  * @return Open files
	  */
	private def openFiles(fileNames: Array[String]) = {
		if (fileNames.length == 0) throw new IllegalArgumentException("No files passed")

		val files = fileNames.map(new File(_))
		files.foreach{ f =>
			if (!f.exists)  throw new IllegalArgumentException(s"File '${f.getPath}' does not exist")
			if (!f.canRead) throw new IllegalArgumentException(s"Missing read permission for file '${f.getPath}'")
		}
		files
	}

	/** Create unique tmp directory
	  *
	  * @throws RuntimeException When either property 'java.io.tmpdir' does not exist or is invalid
	  * @return Tmp directory
	  */
	private def createWorkingDir(): File = {
		if (System.getProperty("java.io.tmpdir") == null) throw new RuntimeException("Property 'java.io.tmpdir' not set.")

		val tmpRootStr = System.getProperty("java.io.tmpdir")
		val tmpRootFile = new File(tmpRootStr)

		if (!tmpRootFile.exists) throw new RuntimeException("Directory described in property 'java.io.tmpdir' does not exist.")
		// TODO: test writability

		// TODO: use hidden (.*)
		new TmpDir(tmpRootFile, "omp4j").toFile
	}

	/** Create new context that will be used in next recursion level.
	  *
	  * @param nextLvlFiles files to be proceeded in the next recursion level
	  * @return configuration context for next recursion level
	  */
	def nextLevel(nextLvlFiles: Array[File]): Config = {
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

	/** Copy all runtime classes into directory given.
	  *
	  * @param rootDir target directory
	  */
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
