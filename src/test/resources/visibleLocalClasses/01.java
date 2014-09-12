class L {
	private class No3 {};
	protected class No4 {};
	public class No5 {
		void goo() {
			class No6 {};
		}
	};
}

class K {
	private class H {};
	protected class I {};
	public class J {};

	public static void main(String[] args) {

		class G {};

		class F {
			class C {};
			class D {};
			class E {};
			int foo (int a, String b) {
				class B {
					class No2 {};
					void foo() {
						class No1 {};
					}
				};

				class A {
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
						}

						class No7 {};
					}
				};

				class No10 {};

				return 0;
			}
		}

		class No8 {
			int foox (int d, String e) {
				class No9 {
					void barx (float f) {}
				}
				return 1;
			}
		}
	}

	private class M {};
	protected class N {};
	public class O {};
}
