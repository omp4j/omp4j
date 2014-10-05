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

//			if (im.singleTypeImportDeclaration != null/* && """\.""".r.split(im.singleTypeImportDeclaration.typeName.getText).last == className*/) {
//				// same as FQN
//				"""\.""".r.split(im.singleTypeImportDeclaration.typeName.getText).foreach(println)
//				println("\t" + """\.""".r.split(im.singleTypeImportDeclaration.typeName.getText).last)
//				if ("""\.""".r.split(im.singleTypeImportDeclaration.typeName.getText).last == className) println("ye")
//				else println("ney")
//				println()
////				im.singleTypeImportDeclaration.typeName.getText //+ "." + className
//			}

			println(className + "=======")
			println(im.getText)
			println()

			val FQN: Option[String] =
				if (im.singleTypeImportDeclaration != null && """\.""".r.split(im.singleTypeImportDeclaration.typeName.getText).last == className) { // same as FQN
//					println("1")
//					println(im.singleTypeImportDeclaration.typeName.getText)
					Some(im.singleTypeImportDeclaration.typeName.getText) //+ "." + className
					//					"fooo"
				} else if (im.typeImportOnDemandDeclaration != null) {
//					println("2")
					Some(im.typeImportOnDemandDeclaration.packageOrTypeName.getText + "." + className)
				} else if (im.singleStaticImportDeclaration != null && im.singleStaticImportDeclaration.Identifier.getText == className) {
//					println("3")
					Some(im.singleStaticImportDeclaration.typeName.getText + "." + className)
				} else if (im.staticImportOnDemandDeclaration != null) {
//					println("4")
					Some(im.staticImportOnDemandDeclaration.typeName.getText + "." + className)
				} else {
//					println("5")
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

		println(s"loading ${className} (${cunit.importDeclaration.asScala.toList.size}})")
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

