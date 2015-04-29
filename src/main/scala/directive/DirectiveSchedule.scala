package org.omp4j.directive

import org.omp4j.grammar.OMPParser

/** Enum representing possible directive schedule policies */
object DirectiveSchedule extends Enumeration {
	type DirectiveSchedule = Value
	val Static = Value("static")
	val Dynamic = Value("dynamic")

	/** DirectiveSchedule factory. Default value is Dynamic.
	 *
	 * @param osc OMP schedule context
	 * @return DirectiveSchedule alternative
	 */
	def apply(osc: OMPParser.OmpScheduleContext) = {
		try {
			if (osc.getText.contains("dynamic")) Dynamic
			else throw new NullPointerException
		} catch {
			case _: NullPointerException => Dynamic
		}
	}
}
