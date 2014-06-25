class Simple {
	public static void main(String[] args) {
		int a = 5;

		// omp parallel for
		for (int i = 0; i < 5; i++) {}

		// omp parallel for err
		for (int i = 0; i < 5; i++) {}

		// omp parallell
		a++;

		// omp
		a++;

		// omp sections foo
		a++;

		// omp #? sections foo
		a++;

	}
}
