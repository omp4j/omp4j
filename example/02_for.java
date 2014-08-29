class For {
	public static void main(String[] args) {
		// omp parallel for
		for (int i = 2; i < 20; i += 3) {
			System.out.println("hello @" + i);
		}
	}
}
