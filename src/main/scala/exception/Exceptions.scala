package org.omp4j.preprocessor.exception

class ParseException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)
class CompilationException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)
class SyntaxErrorException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)
