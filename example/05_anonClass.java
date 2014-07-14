class Simple {
	public int field = 15;

	public static void main(String[] args) {

		final Simple s = new Simple();

		class Anon {
			public int anonField = 4;
			public void run (int a) {
				int x = a + anonField + s.field;
				// omp parallel
				{ s.inc(x); }
			}
		}

		Anon an = new Anon();

		int var1 = 0;
		var1++;

		// omp parallel
		{
			an.run(18);
			Anon am = new Anon();
		}
	}

	public void inc(int x) {
		x++; // TODO params
	}
}
