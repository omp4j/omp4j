import java.awt.*;

class Top {
	class Inner1 {
		public String inner1Public;
		protected String inner1Protected;
		private String inner1Private;
	}

	public void foo1() {

		// local class inherits from inner class
		class Local1 extends Inner1 {
			public String local1Public;
			protected String local1Protected;
			private String local1Private;
	
			public void run() {
				// omp parallel
				{
					String tmp;
					tmp = inner1Public;
					tmp = inner1Protected;
					tmp = local1Public;
					tmp = local1Protected;
					tmp = local1Private;

					tmp = this.inner1Public;
					tmp = this.inner1Protected;
					tmp = this.local1Public;
					tmp = this.local1Protected;
					tmp = this.local1Private;
				}
			}
		}
	}

	public void foo2() {

		// local class inherits from lib. class
		class Local2 extends Point {
			public int local2Public;
			protected int local2Protected;
			private int local2Private;

			public void run() {
				// omp parallel
				{
					int tmp;
					tmp = x;	// inherited
					tmp = local2Public;
					tmp = local2Protected;
					tmp = local2Private;

					tmp = this.x;
					tmp = this.local2Public;
					tmp = this.local2Protected;
					tmp = this.local2Private;
				}
			}
		}

		// local class inherits from another local class
		class Local3 extends Local2 {
			public int local3Public;
			protected int local3Protected;
			private int local3Private;

			public void run() {
				// omp parallel
				{
					int tmp;
					tmp = x;
					tmp = local2Public;
					tmp = local2Protected;
					tmp = local3Public;
					tmp = local3Protected;
					tmp = local3Private;

					tmp = this.x;
					tmp = this.local2Public;
					tmp = this.local2Protected;
					tmp = this.local3Public;
					tmp = this.local3Protected;
					tmp = this.local3Private;
				}
			}
		}
	}

	public int field;

	// complex example - params and locals must be declared final
	class Inner2 {
		int f1 = 0;

		void m1(final int n1) {
			final int l1 = 0;
			class Local4 {
				int f2 = 0;
				class Nested2 {
					int f3 = 0;
					void m2(final int n2){
						final int l2 = 0;
						// nested local class inherits from inner class
						class Local5 extends Inner1 {
							int f4 = 0;
							void m3(final int n3) {
								final int l3 = 0;
								// omp parallel
								{
									int temp = 
										field +
										f1 +
										f2 +
										f3 +
										f4 +
										n1 +
										n2 +
										n3 +
										l1 +
										l2 +
										l3;

									String tempS;
									tempS = inner1Public;
									tempS = inner1Protected;
								}
							}
						}
						
					}
				}
			}
		}
	}
}

class java {
	public static class awt {
		public static class geom {
			public static class Area {
				public int bububu;
			}
		}
	}
}

class notjava {
	public class awt {
		public class geom {
			public class Area {
				public int trololo;
			}
		}
	}
}


class A {
	public static class B {
		public static class C {
			public int abcPublic;
			protected int abcProtected;
			private int abcPrivate;
		}
	}
}
