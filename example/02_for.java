class For {
	public static void main(String[] args) {
		// omp parallel for
		for (int i = 0; i < 20; i += 3) {
			System.out.println("hello @" + i);
		}
	}
}
