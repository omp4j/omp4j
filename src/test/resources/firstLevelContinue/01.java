class FirstLevelContinueExtractorTest01 {
	public static void main(String[] args) {
		// omp parallel for
		for (int i=0, j=0; i < 10; i++) {
			
			for (int k=0; k<10; k++) {
				continue;
			}

			int x = 0;
			while (x < 10) {
				x++;
				continue;
			}

		}
	}
}
