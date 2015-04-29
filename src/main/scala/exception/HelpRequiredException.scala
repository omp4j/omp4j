package org.omp4j.exception


/** Thrown if help is required.
  *
  * @constructor simply create super class
  * @param message description of the error
  * @param cause cause if rethrown exception
  */
class HelpRequiredException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)
