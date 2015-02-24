class Atomic {
	public static void main() {
		int foo=0;

		// omp parallel
		{
			// omp atomic
			foo += 8;
		}
	}
}
