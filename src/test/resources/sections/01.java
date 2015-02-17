class SectionsTest01 {
	public static void main(String[] args) {
		// omp sections
		{
			System.out.println("Foo");

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
