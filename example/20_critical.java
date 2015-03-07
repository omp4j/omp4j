class Critical {
	public static void main(String[] args) {
		// omp parallel
		{
			// omp critical
			{
				System.out.println("Critical section");
			}
		}

		final String lock = "myString";
		// omp parallel
		{
			// omp critical(lock)
			{
				System.out.println("Critical section");
			}
		}
	}
}
