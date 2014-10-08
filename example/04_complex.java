class Complex {
	public int field = 15;
	class Inner {
		public int innerField = 4;
		class MoreInner {
			public int moreField = 4;
			private void fooo() {

				int capt = 4;

				// omp parallel
				{
					int x = 5;
					x += field;
					x += innerField;
					x += moreField;
					this.moreField++;
					x += capt;
				}
				inc(moreField);
				inc(this.moreField);
			}
		}
	}


	public static void main(String[] args) {

		Complex s = new Complex();
		Complex t = new Complex();

		int var1 = 0;
		var1++;
		{
			int var2 = 0;
			var1++;
			var2++;
			t.baar(var2);
			System.out.println("hello" + var1);

			{
				int var3 = 0;
				var1++;
				var2++;
				var3++;
			}
			int var4 = 0;
			var1++;
			var2++;
			var4++;
			// omp parallel
			{
				int var5 = 0;
				var1++;
				var2++;
				var4 += var1 + var5 + s.baar(var1);
				var5++;
			}
			int var6 = 0;
			var1++;
			var2++;
			var4++;
			var6++;
		}
		int var7 = 0;
		var1++;
		var7++;
	}

	private int baar(int c) {
		c++;
		this.inc(c);
		return c;
	}

	public void inc(int x) {
		// omp parallel
		{
			x++;

			int var4 = 0;
			var4 = field;
			var4 += this.field;

			int field = 4;
			var4 += field;
			var4 += this.field;
		}
	}
}
