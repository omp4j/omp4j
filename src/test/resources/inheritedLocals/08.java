class InheritedLocals08 {

	public int x = 5;

	void use() {
		// omp parallel public(a)
		{
			x++;
			// omp parallel public(b)
			{x++;}

			int x = 4;
			// omp parallel public(c)
			{if (x < 5) x--;}

		}
	}
}
