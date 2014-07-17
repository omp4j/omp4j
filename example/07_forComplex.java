class Parent  {
	public int inheritedField = 0;
}

class Outer {

	int outerField = 14;
	static int staticInt = 15;

	class Inner extends Parent {
		int innerField = 42;

		public void innerFoo(int x, int y) {

			// omp parallel for
			for (int i = 0; i < x + innerField; i++) {

				int res = outerField + inheritedField + Outer.staticInt + getOne();
				System.out.println(res);
			}
		}

		int getOne() { return 1; }
	}

	void parallel(int x) {

		// omp parallel for
		for (int i = 0; i < x + outerField + this.outerField + Outer.staticInt + staticInt + this.staticInt; i++) {
			System.out.println(i);
		}


	}
}
