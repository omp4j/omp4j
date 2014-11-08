import java.util.concurrent.atomic.AtomicInteger;

class TranslationVisitor01 {
	public static void main(String[] args) {
		int supa_arr[][] = new int[4][10];
		int nuta = 15;

		AtomicInteger ra = new AtomicInteger(0);

		// omp parallel
		{
			int r = ra.getAndIncrement();
			nuta = 15;
			// omp for
			for (int c = 0; c < 10; c++) {
				supa_arr[r][c] = r+c;
			}
		}

		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 10; c++) {
				System.out.print(supa_arr[r][c]);
				System.out.print(" ");
			}
			System.out.println();
		}
	}

}
