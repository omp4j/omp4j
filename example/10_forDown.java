class ForDown {
	public static void main(String[] args) {
		// omp parallel for
		for (int i = 19; i > 0; i--) {
			System.out.println("hello @" + i);
		}
	}
}
