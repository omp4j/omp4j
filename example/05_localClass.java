class Simple {

	class Inher {}

	public int field = 15;

	public void inc(int x) {

		class Local2 {
			public int anonField = 4;
			public void run (int a) {
				int x = 15;
				// omp parallel
				{
					int foo = anonField + this.anonField + x + a + field;
					foo = anonField;
					foo = this.anonField;
					foo = x;
					foo = a;
					foo = field;
				}
			}
		}


	}
}
