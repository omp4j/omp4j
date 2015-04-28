class ForFor {
	public static void main(String[] args) {
		// omp for
		for (int c = 0; c < 10; c++) {
			System.out.print(c + " ");
		}
		System.out.println("");

		char globe1 = 'X';

		// omp parallel
		{
			System.out.print("pre" + OMP_THREAD_NUM + " ");
			char globe2 = 'Y';

			// omp for
			for (int c = 0; c < 10; c++) {
				System.out.print("" + globe1 + globe2 + c + " ");
			}
			System.out.print("post" + OMP_THREAD_NUM + " ");
		}
		System.out.println("");
	}
}
