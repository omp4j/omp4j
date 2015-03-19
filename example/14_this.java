package org.omp4j.example;

class RecursThis {
	public RecursThis r;
	public RecursThis nonAmb;

	public void foo() {
		// omp parallel
		{
			RecursThis tmp;
			
			// non-this
			tmp = r;
			tmp = r.r;
			tmp = r.r.r;
			tmp = r.r.get();
			tmp = r.r.r.r.r.r.r.get();
			tmp = get().r.get();
			tmp = get().r.get().r;
			tmp = r.get().r.get().r;
			r.get().goo();
			r.get().r.goo();

			// this
			tmp = this.r;
			tmp = this.r.r;
			tmp = this.r.r.r;
			tmp = this.get().r.get();
			tmp = this.get().r.get().r;
			tmp = this.r.get().r.get().r;
			this.r.get().goo();
			this.r.get().r.goo();

			// methods
			hoo();
			this.hoo();


			// ambig
			RecursThis r = new RecursThis();
			tmp = r;
			tmp = this.r;

			// non-amb
			tmp = nonAmb;

			// local
			RecursThis s = new RecursThis();
			tmp = s.r;
			tmp = s.r.r;
			tmp = s.r.r.r;
			tmp = s.get().r.get();
			tmp = s.get().r.get().r;
			tmp = s.r.get().r.get().r;
			s.r.get().goo();
			s.r.get().r.goo();

			// other
			System.out.println("hello");

			// arg
			RecursThis t = new RecursThis();
			set(t);
			set(this);

			this.set(t);
			this.set(this);

			this.r.set(t);
			this.r.set(this);

			this.get().set(t);
			this.get().set(this);

			// self
			tmp = set2(t);
			tmp = set2(this);

			tmp = this.set2(t);
			tmp = this.set2(this);

			tmp = this.r.set2(t);
			tmp = this.r.set2(this);

			tmp = this.get().set2(t);
			tmp = this.get().set2(this);
		}

	}

	public void goo() {}
	private void hoo() {}
	public RecursThis get() { return r; }
	public void set(RecursThis n_r) {}
	public RecursThis set2(RecursThis n_r) { return n_r; }

	public int x = 5;
	void use() {
		// omp parallel
		{
			x++;
			{x++;}

			int x = 4;
			{if (x < 5) x--;}

		}
	}
}
