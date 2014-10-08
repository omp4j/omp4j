package org.omp4j.system

import java.io.File
import java.net.URL;
import java.net.URLClassLoader;

import scala.collection.JavaConverters._

import org.omp4j.grammar._

/** JAR loader */
class Loader(jar: File) {

	/** Cached ClassLoader */
	val classLoader = loadClassLoader(jar)

	/** Load jar given
	  * @param jar File of jar to be loaded
	  * @throws IllegalArgumentException If this jar URL is not absolute
	  * @throws MalformedURLException If a protocol handler for the URL could not be found, or if some other error occurred while constructing the URL
	  * @throws SecurityException If a required system property value cannot be accessed or if a security manager exists and its checkCreateClassLoader method doesn't allow creation of a class loader.
	  * @return ClassLoader for given jar
	  */
	private def loadClassLoader(jar: File): ClassLoader = {
		val url = jar.toURI.toURL
		val urls = Array[URL](url)
		val cl = new URLClassLoader(urls)
		cl		
	}

	/** Load class specified by FQN (Binary name) */
	def loadByFQN(FQN: String): Class[_] = classLoader.loadClass(FQN)

	/** Construct FQN based on class name and package name */
	def buildFQN(name: String, cunit: Java8Parser.CompilationUnitContext) = packageNamePrefix(cunit) + name

	/** Construct package prefix for FQN based on package-statement existence */
	def packageNamePrefix(cunit: Java8Parser.CompilationUnitContext): String = {
		try {
			cunit.packageDeclaration.Identifier.asScala.map(_.getText).mkString(".") + "."
		} catch {
			case e: NullPointerException => ""
		}
	}

	/** Try imports until some match */
	def loadByImport(className: String, cunit: Java8Parser.CompilationUnitContext): Class[_] = {

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

	/** Try various loading possibilities */
	def load(name: String, cunit: Java8Parser.CompilationUnitContext): Class[_] = {
		try {
			loadByFQN(name)
		} catch {
			case e: ClassNotFoundException => 
				try {
					loadByFQN(buildFQN(name, cunit))
				} catch {
					case e: ClassNotFoundException => loadByImport(name, cunit)
				}
			
		}
	}
}

