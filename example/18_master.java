class Master {
	public static void main(String[] args) {
		// omp parallel
		{
			// omp master
			{
				System.out.println("hello from master only");
			}
			System.out.println("hello from worker");
		}
	}

}
