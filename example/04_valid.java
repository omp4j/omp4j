class Simple {
	protected int fooVar;
	protected String barVar = "hello";

	public static void main(String[] args) {
		// omp parallel for
		for (int i = 0; i < 5; i++) {
			// omp parallel
			System.out.println("hello");
		}

		int a;
		{
			int x;
		}
		int c;
	}

	private void fooMethod(int tmpParam) {
		// omp sections
		{
			// omp section
			{
				System.out.println("foo1");
			}

			// omp section
			{
				System.out.println("foo2");
			}
		}
	}

	private class Nested {
		public void innerMethod(String s) {}
	}
}
