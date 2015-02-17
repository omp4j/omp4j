package org.omp4j.example;

import java.lang.Thread;
import java.util.concurrent.atomic.AtomicInteger;

public class PublicClass {
	public static void main(String arg[]) {
		// omp parallel
		{
			System.out.println(OMP_THREAD_NUM + "/" + OMP_NUM_THREADS);
		}
	}
}
