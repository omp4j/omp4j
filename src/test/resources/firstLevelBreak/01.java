class FirstLevelBreakExtractorTest01 {
	public static void main(String[] args) {
		// omp parallel for
		for (int i=0, j=0; i < 10; i++) {
			
			for (;;) {
				break;
			}

			while (true) {
				break;
			}

		}
	}
}
