package org.omp4j.example;

class Recurs {
	public Recurs r;
	public Recurs nonAmb;

	public void foo() {
		// omp parallel
		{
			Recurs tmp;

			// anon
			set(new Recurs() {
				@Override
				public void goo() {
					this.set(this);
				}
			});

			// anon
			set(new org.omp4j.example.Recurs() {
				@Override
				public void goo() {
					this.set(this);
				}
			});

			// anon
			set3(0, new Recurs() {
				@Override
				public void goo() {
					this.set3(0, this);
				}
			});

			// anon
			set3(0, new org.omp4j.example.Recurs() {
				@Override
				public void goo() {
					this.set3(0, this);
				}
			});

		}
	}

	public void tester() {
		// anon
		set(new Recurs() {
			@Override
			public void goo() {
				// omp parallel
				{
					this.set(this);
				}
			}
		});

		// anon
		set(new org.omp4j.example.Recurs() {
			@Override
			public void goo() {
				// omp parallel
				{
					this.set(this);
				}
			}
		});

		// anon
		set3(0, new Recurs() {
			@Override
			public void goo() {
				// omp parallel
				{
					this.set(this);
				}
			}
		});

		// anon
		set3(0, new org.omp4j.example.Recurs() {
			@Override
			public void goo() {
				// omp parallel
				{
					this.set(this);
				}
			}
		});
	}

	void tester2() {

		abstract class Parent extends Recurs {
			public int public_field;
		}

		set(new Parent() {
			@Override
			public void goo() {
				// omp parallel
				{
					this.set(this);
					public_field += 12;
				}
			}
		});

		// omp parallel
		{
			set(new Parent() {
				@Override
				public void goo() {
					this.set(this);
					public_field += 12;
				}
			});
		}


	}


	public void goo() {}
	private void hoo() {}
	public Recurs get() { return r; }
	public void set(Recurs n_r) {}
	public Recurs set2(Recurs n_r) { return n_r; }
	public Recurs set3(int x, Recurs n_r) { return n_r; }

}
