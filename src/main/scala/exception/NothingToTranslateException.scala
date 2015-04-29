package org.omp4j.exception


/** Thrown when there is nothing to translate.
  *
  * @constructor simply create super class
  * @param message description of the error
  * @param cause cause if rethrown exception
  */
class NothingToTranslateException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)
