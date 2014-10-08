package org.omp4j.example;

class Recurs {
	public Recurs r;
	public Recurs nonAmb;

	public void foo() {
		// omp parallel
		{
			Recurs tmp;
			
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
			Recurs r = new Recurs();
			tmp = r;
			tmp = this.r;

			// non-amb
			tmp = nonAmb;

			// local
			Recurs s = new Recurs();
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
			Recurs t = new Recurs();
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
	public Recurs get() { return r; }
	public void set(Recurs n_r) {}
	public Recurs set2(Recurs n_r) { return n_r; }
}
