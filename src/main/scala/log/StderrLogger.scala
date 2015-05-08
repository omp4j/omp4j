package org.omp4j.log

/** Logger that deletes passed messages immediately */
class StderrLogger extends Logger {

	/** Print the message into stderr
	  *
	  * @param msg message o be logged
	  */
	override def log(msg: String) = System.err.println(msg)
}
