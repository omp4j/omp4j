class Complex {

	public static void main(String[] args) {

		Complex s = new Complex();
		int var1 = 0, var2 = 0, var3 = 0, var4 = 0;

		// omp parallel
		{
			int var5 = 0;
			var1++;
			var2++;
			var4 += var1 + s.baar(var3);
			var5++;
		}
	}

	private int baar(int c) {
		return c;
	}
}
