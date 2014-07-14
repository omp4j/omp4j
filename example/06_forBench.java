class ForBench {

	private int fibonacci(int n) {
		if (n <= 1) return 1;
		else return fibonacci(n-1) + fibonacci(n-2);
	}

	public int[] serialFibonacci(int[] input) {
		int[] result = new int[input.length];

		for (int i = 0; i < input.length; i++) {
			result[i] = fibonacci(input[i]);
		}

		return result;
	}

	public int[] parallelFibonacci(int[] input) {
		int[] result = new int[input.length];

		// omp parallel for
		for (int i = 0; i < input.length; i++) {
			result[i] = fibonacci(input[i]);
		}

		return result;
	}

	public static void main(String[] args) {
		ForBench fb = new ForBench();
		int[] input = {42, 33, 44, 40, 41, 45, 38, 39, 35, 44};

		long s1 = System.nanoTime();
		int[] serialRes = fb.serialFibonacci(input);
		long s2 = System.nanoTime();
		System.out.println("Serial time: " + (s2 - s1));

		long p1 = System.nanoTime();
		int[] parallelRes = fb.parallelFibonacci(input);
		long p2 = System.nanoTime();
		System.out.println("Parallel time: " + (p2 - p1));

		System.out.println("Speedup: " + ((double)(s2 - s1)/(double)(p2 - p1)));

		for (int i = 0; i < input.length; i++) {
			if (serialRes[i] != parallelRes[i]) {
				System.out.println("fib(" + input[i] + ") =!= " + parallelRes[i] + " (expected " + serialRes[i] + ")");
				System.exit(1);
			}
		}
	}
}
