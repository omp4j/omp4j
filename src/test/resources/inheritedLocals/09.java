class InheritedLocals09 {

	public int x = 5;

	void use() {

		int arr[][] = new int[5][10];

		// omp parallel public(a)
		{
			x++;
			// omp parallel public(b)
			{x++;}

			int x = 4;
			// omp parallel public(c)
			{if (x < 5) x--;}

			arr[0][0] = 15;

		}
	}
}
