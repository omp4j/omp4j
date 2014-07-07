class Sections {
	public static void main(String[] args) {
		/* TODO: replace overlap */
		// omp sections
		{
			// omp section
			{
				System.out.println("task1 start");
				System.out.println("task1 stop");
			}

			// omp section
			{
				System.out.println("task2 start");
				System.out.println("task2 stop");
			}

			// omp section
			{
				System.out.println("task3 start");
				System.out.println("task3 stop");
			}
		}
	}
}
