class Single {
	public static void main(String[] args) {
		// omp parallel
		{
			// omp single
			{
				System.out.println("hello from some one thread only");
			}
			System.out.println("hello from worker");
		}
	}

}
