package org.omp4j.loader

import java.io.File
import java.net.URL;
import java.net.URLClassLoader;

/** JAR loader */
class Loader {

	/** Load jar given
	  * @param jar File of jar to be loaded
	  * @throws IllegalArgumentException If this jar URL is not absolute
	  * @throws MalformedURLException If a protocol handler for the URL could not be found, or if some other error occurred while constructing the URL
	  * @throws SecurityException If a required system property value cannot be accessed or if a security manager exists and its checkCreateClassLoader method doesn't allow creation of a class loader.
	  * @return ClassLoader for given jar
	  */
	def load(jar: File): ClassLoader = {
		val url = jar.toURI().toURL()
		val urls = Array[URL](url)
		val cl = new URLClassLoader(urls)
		cl		
	}

}

