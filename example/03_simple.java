class Simple {
	public static void main(String[] args) {
		// omp parallel for
		for (int i = 0; i < 5; i++) {
			// omp parallel
			System.out.println("hello");
		}
		int a; // 4

		int c; // 5

		// 6
		// 7

		// omp sections
		{
			int b = a+4;
			System.out.println(b);
		}

		// unmatched comment
		{
			int x;
		}
	}
}
