class For {
	public static void main(String[] args) {
		// omp parallel for
		for (int i = 3; i < 13; i+=1) {
			System.out.println("hello @" + i);
		}
	}
}
