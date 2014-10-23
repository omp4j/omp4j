class InheritedParams01 {
	public static void main(String[] args) {

		class Nested1 {
			int foo (int a, String b) {
				class Nested2 {
					void bar (float c) {
						// omp parallel
						{ int xxx; }
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
