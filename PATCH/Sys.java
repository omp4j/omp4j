package org.omp4j.runtime;

/**
 * Class reading OMP4J_MAX_PROC environment variable
 */
public class Sys {
	public static int availableProcessors (int desired) {
		 String userMaxProcStr = System.getenv("OMP4J_MAX_PROC");
		 int realAvailableProcessors = Runtime.getRuntime().availableProcessors();
		 if (desired == 0 || desired > realAvailableProcessors)
		 	desired = realAvailableProcessors;
		 if (userMaxProcStr == null)
		 	return desired;
		 int userMaxProc;
		 try {
		 	userMaxProc = Integer.parseInt(userMaxProcStr);
		 } catch (NumberFormatException e) {
		 	userMaxProc = 0;
		 }
		 if (userMaxProc < 1 || userMaxProc > realAvailableProcessors) {
		 	System.err.println("-- omp4j: envvar OMP4J_MAX_PROC is invalid (" + userMaxProcStr + ") - use [1.. + " + realAvailableProcessors + "]");
		 	return desired;
		 }
		 return userMaxProc > desired? desired: userMaxProc;
	}

	public static int availableProcessors () {
		return availableProcessors(0);
	}
}
