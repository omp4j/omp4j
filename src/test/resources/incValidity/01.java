class IncValidity01 {
	public static void main(String[] args) {
		int j = 0;
		// omp parallel for
		for (int i=0; i < 10; i++, j++) {
			// ...
		}
	}
}
