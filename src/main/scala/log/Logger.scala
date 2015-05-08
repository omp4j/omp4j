package org.omp4j.log

/** */
trait Logger {

	/** Log the message
	  *
	  * @param msg message o be logged
	  */
	def log(msg: String): Unit
}
