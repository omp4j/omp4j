class Captured {

	public int method(int x) {
		System.out.println(x);
		return 1;
	}

	public int field1;
	public int field2;

	public static void main(String[] args) {
		
		int a=0, b=0, c=0, d=0, e=0, f=0;
		Captured capt = new Captured();
		
		// omp parallel
		{
			Captured nonCapt = new Captured();

			capt.method(a);
			capt.method(capt.field1);
			capt.field2 += 14;
			System.out.println(b);
			c += 10;
			c = c + 10;
			d++;
			e += capt.method(f);

			nonCapt.method(140);
		}
	}

	public void paraMethod(int par1, int par2) {

		Object foo = new Object();
		Captured doCapt = new Captured();

		// omp parallel
		{
			int non = 0;
			par1++;
			par1 += this.field1;
			paraMethod(0,1);
			this.paraMethod(0,par2);
			this.paraMethod(0,this.field1);
			foo.notify();
			doCapt.field2++;
		}
	}
}
