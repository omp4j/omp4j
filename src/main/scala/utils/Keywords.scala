package org.omp4j.utils

/** Various keywords */
object Keywords {
	/** List of all Java keywords */
	val JAVA_KEYWORDS = List("abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while")

	/** Java8 value types */
	val JAVA_VALUE_TYPES = List("boolean", "byte", "short", "int", "long", "char", "float", "double")

	val JAVAC_SINGLE_OPTS_FULL = List("-deprecation", "-g", "-g:none", "-g:source", "-g:lines", "-g:vars", "-help", "-nowarn", "-verbose", "-X", "-Xlint:none", "-Xlint:-xxx", "-Xlint:unchecked", "-Xlint", "-Xlint:serial", "-Xlint:finally", "-Xlint:fallthrough")
	val JAVAC_SINGLE_OPTS_START = List("-Djava.ext.dirs=", "-Djava.endorsed.dirs=", "-Xbootclasspath/p:", "-Xbootclasspath/a:", "-Xbootclasspath/", "-J")
	val JAVAC_DOUBLE_OPTS = List("-classpath", "-d", "-encoding", "-source", "-sourcepath", "-target", "-bootclasspath", "-extdirs", "-Xmaxerrors", "-Xmaxwarns", "-Xstdout")
}
