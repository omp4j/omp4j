package org.omp4j.system

import java.io.File
import java.net.{MalformedURLException, URL, URLClassLoader}

import org.omp4j.grammar._

import scala.collection.JavaConverters._

/** JAR loader */
class Loader(val jar: File) {

	/** Cached ClassLoader */
	val classLoader = loadClassLoader(jar)

	/** Load jar given.
	  *
	  * @param jar JAR to be loaded
	  * @throws IllegalArgumentException If this jar URL is not absolute
	  * @throws MalformedURLException If a protocol handler for the URL could not be found, or if some other error occurred while constructing the URL
	  * @throws SecurityException If a required system property value cannot be accessed or if a security manager exists and its checkCreateClassLoader method doesn't allow creation of a class loader.
	  * @return ClassLoader for given jar
	  */
	private def loadClassLoader(jar: File): ClassLoader = {
		val url = jar.toURI.toURL
		val parent = jar.getParentFile.toURI.toURL
		val urls = Array[URL](url, parent)      // MS Windows for some reason can't read from JAR, thus the directory is extra provided. UNIX and linux read from JAR as expected.
		val cl = new URLClassLoader(urls)
		cl		
	}

	/** Load class specified by FQN (Binary name).
	 *
	 * @param FQN Fully Qualified Name of the class to be loaded
	 * @return the requested Class object
	 * @throws ClassNotFoundException if class is not found
	 */
	def loadByFQN(FQN: String): Class[_] =
		classLoader.loadClass(FQN)

	/** Construct FQN based on class name and package name
	 *
	 * @param name name of the class
	 * @param cunit compilation unit context (provided by ANTLR)
	 * @return Fully Qualified Name of the class
	 */
	private def buildFQN(name: String, cunit: Java8Parser.CompilationUnitContext) = packageNamePrefix(cunit) + name

	/** Construct package prefix for FQN based on package-statement existence */
	private def packageNamePrefix(cunit: Java8Parser.CompilationUnitContext): String = {
		try {
			cunit.packageDeclaration.Identifier.asScala.map(_.getText).mkString(".") + "."
		} catch {
			case e: NullPointerException => ""
		}
	}

	/** Try to load a class until some `import` match
	  *
	  * @param className the class to be loaded
	  * @param cunit compilation unit context (provided by ANTLR)
	  * @return the requested Class object
	  * @throws ClassNotFoundException if class is not found
	  */
	private def loadByImport(className: String, cunit: Java8Parser.CompilationUnitContext): Class[_] = {

		/** Recursively try the first import */
		def loadFirst(imports: List[Java8Parser.ImportDeclarationContext]): Class[_]  = {
			if (imports.size == 0) throw new ClassNotFoundException

			val im = imports.head

			val FQN: Option[String] =
				if (im.singleTypeImportDeclaration != null && """\.""".r.split(im.singleTypeImportDeclaration.typeName.getText).last == className) { // same as FQN
					Some(im.singleTypeImportDeclaration.typeName.getText) //+ "." + className
				} else if (im.typeImportOnDemandDeclaration != null) {
					Some(im.typeImportOnDemandDeclaration.packageOrTypeName.getText + "." + className)
				} else if (im.singleStaticImportDeclaration != null && im.singleStaticImportDeclaration.Identifier.getText == className) {
					Some(im.singleStaticImportDeclaration.typeName.getText + "." + className)
				} else if (im.staticImportOnDemandDeclaration != null) {
					Some(im.staticImportOnDemandDeclaration.typeName.getText + "." + className)
				} else {
					None
				}

			FQN match {
				case Some(str) =>
					try {
						loadByFQN(str)
					} catch {
						case e: ClassNotFoundException => loadFirst(imports.tail)
					}
				case None => loadFirst(imports.tail)
			}
		}

		loadFirst(cunit.importDeclaration.asScala.toList)
	}

	/** Load class specified by its name.
	  *
	  * @param name class name
	  * @param cunit compilation unit context (provided by ANTLR)
	  * @return the requested Class object
	  * @throws ClassNotFoundException if class is not found
	  */
	def load(name: String, cunit: Java8Parser.CompilationUnitContext): Class[_] = {
		try {
			loadByFQN(name)
		} catch { case e: ClassNotFoundException =>
			try {
				loadByFQN("java.lang." + name)
			} catch { case e: ClassNotFoundException =>
				try {
					loadByFQN(buildFQN(name, cunit))
				} catch {
					case e: ClassNotFoundException => loadByImport(name, cunit)
				}
			}

		}
	}
}

