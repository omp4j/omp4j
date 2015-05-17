class ParaPara {
	public static void main(String[] args) {
		
		int x = 1;

		// omp parallel
		{
			int y = 2;
			System.out.println("out x=" + x);
			System.out.println("out y=" + y);

			// omp parallel
			{
				System.out.println("hello world :)");
				System.out.println("in x=" + x);
				System.out.println("in y=" + y);
			}
		}

		System.out.println("last line");
	}
}
