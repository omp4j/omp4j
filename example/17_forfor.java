import java.util.concurrent.atomic.AtomicInteger;

class ForFor {
	public static void main(String[] args) {
		int arr[][] = new int[4][10];

		AtomicInteger ra = new AtomicInteger(0);
		int constant = 15;

		// omp parallel
		{
			int r = ra.getAndIncrement();

			// omp for
			for (int c = 0; c < 10; c++) {
				arr[r][c] = r+c+constant;
			}
		}

		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 10; c++) {
				System.out.print(arr[r][c]);
				System.out.print(" ");
			}
			System.out.println();
		}
	}

}
