class InheritedLocal06 {
	public int field = 15;

	public static void main(String[] args) {

		final InheritedLocal06 s = new InheritedLocal06();

		class Local {
			public int anonField = 4;
			public void run (int a) {
				int x = a + anonField + s.field;
				{ s.inc(x); }
			}
		}

		Local an = new Local();
	}

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
