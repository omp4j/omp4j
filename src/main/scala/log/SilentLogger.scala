package org.omp4j.log

/** Logger that deletes passed messages immediately */
class SilentLogger extends Logger {

	/** Do nothing
	  *
	  * @param msg message o be logged
	  */
	override def log(msg: String) = {}
}
