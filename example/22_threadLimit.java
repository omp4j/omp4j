class ThreadLimit {
	public static void main(String[] args) {

		int foo = 5;
		// omp parallel threadNum(2)
		{
			System.out.println("hello");
			System.out.println("world");
			System.out.println(foo);
		}

		System.out.println("last line");
	}
}
