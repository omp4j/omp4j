class OddFor {
	public static void main(String[] args) {
		// omp parallel for
		for (int i = 0; i < 14; i+=3) {
			System.out.println(i);
		}
	}
}
