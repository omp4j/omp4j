package org.omp4j.directive

import org.omp4j.grammar.OMPParser

object DirectiveSchedule extends Enumeration {
	type DirectiveSchedule = Value
	val Static = Value("static")
	val Dynamic = Value("dynamic")

	def apply(osc: OMPParser.OmpScheduleContext) = {
		try {
			if (osc.getText.contains("dynamic")) Dynamic
			else throw new NullPointerException
		} catch {
			case _: NullPointerException => Static
		}
	}
}
