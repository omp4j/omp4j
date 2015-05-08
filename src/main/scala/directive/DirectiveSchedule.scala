package org.omp4j.directive

import org.omp4j.grammar.OMPParser

/** DirectiveSchedule companion object and factory */
object DirectiveSchedule {

	def Static  = new DirectiveSchedule("org.omp4j.runtime.StaticExecutor")
	def Dynamic = new DirectiveSchedule("org.omp4j.runtime.DynamicExecutor")

	/** DirectiveSchedule factory. Default value is Dynamic.
	 *
	 * @param osc OMP schedule context
	 * @return DirectiveSchedule representation
	 */
	def apply(osc: OMPParser.OmpScheduleContext) = {



		val Pattern = """schedule\((.*)\)""".r
		try {
			osc.getText.toLowerCase match {
				case Pattern("dynamic") => Dynamic
				case Pattern("static")  => Static
				case Pattern(fqn)       => new DirectiveSchedule(osc.getText.toLowerCase)
			}
		} catch {
			case _: NullPointerException => new DirectiveSchedule("org.omp4j.runtime.DynamicExecutor")
		}
	}
}

/** Wrapper class representing the directive schedule policy */
class DirectiveSchedule(executorType: String) {
	override def toString = executorType
}
