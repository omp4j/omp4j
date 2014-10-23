class FirstLevelContinueExtractorTest02 {
	public static void main(String[] args) {
		int i =0;
		// omp parallel for
		for (; i < 10; i++) {
			
			for (int k=0; k<10; k++) {
				continue;
			}

			int x = 0;
			while (x < 10) {
				x++;
				continue;
			}

			continue;
		}
	}
}
