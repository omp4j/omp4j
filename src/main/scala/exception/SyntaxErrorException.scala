package org.omp4j.exception


/** Systax error.
  *
  * @constructor simply create super class
  * @param message description of the error
  * @param cause cause if rethrown exception
  */
class SyntaxErrorException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)
