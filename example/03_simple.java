class Simple {
	public static void main(String[] args) {
		// omp parallel for
		for (int i = 0; i < 5; i++) {
			// omp parallel
			System.out.println("hello");
		}
		int a; // 4

		int c; // 5

		// I like omp
		c += (4 * 5) - a * 6;

		// This is not omp example
		int cvb = 42;

		// omp-astic comment
		cvb++;

		// omp sections
		{
			int b = a+4;
			System.out.println(b);
		}

		// unmatched comment
		int x = c + c * 8;
	}
}
