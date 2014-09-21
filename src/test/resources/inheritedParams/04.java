import java.util.ArrayList;

@FunctionalInterface
interface IntInterface {
	public void work(int a);
}

@FunctionalInterface
interface IntIntInterface {
	public int work(int a, int b);
}

class InheritedParams04 {
	public static void main(String[] args) {

		class Nested1 {
			int foo (int a, String b) {
				class Nested2 {
					void bar (float c) {

						IntInterface r = (int d) -> {
								int xxx = d;
								IntIntInterface s = (int e, int f) -> {
									// omp parallel
									{
										int tmp = 5 + d;
									}
									return 42;
								};
						};
					}
				}
				return 1;
			}
		}

		class Nested3 {
			int foox (int d, String e) {
				class Nested4 {
					void barx (float f) {
						
					}
				}
				return 0;
			}
		}
	}
}
