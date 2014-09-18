class L {	// top
	private class No3 {};	// inner
	protected class No4 {};	// inner
	public class No5 {	// inner
		void goo() {
			class No6 {};	// local
		}
	};
}

class K {	// top
	private class H {};	// inner
	protected class I {};	// inner
	public class J {};	// inner

	public static void main(String[] args) {

		class G {};	// local

		class F {	// local
			class C {};	// innerInlocal
			class D {};	// innerInlocal
			class E {};	// innerInlocal
			int foo (int a, String b) {
				class B {	// local
					class No2 {};	// innerInlocal
					void foo() {
						class No1 {};	// local
					}
				};

				if (3 < a) {
					class No11 {};	// local
				}

				class A {	// local
					class P {};	// innerInlocal
					void bar (float x51) {
						// omp parallel
						{
							int xxx;

							A a;
							B b;
							C c;
							D d;
							E e;
							F f;
							G g;
							H h;
							I i;
							J j;
							K k;
							L l;

							L.No5 l_no5;

							class No12 {};	// local
						}

						class No7 {};	// local
					}
				};

				class No10 {};	// local

				return 0;
			}
		}

		class No8 {	// local
			int foox (int d, String e) {
				class No9 {	// local
					void barx (float f) {}
				}
				return 1;
			}
		}
	}

	private class M {};	// inner
	protected class N {};	// inner
	public class O {};	// inner
}
