import java.util.ArrayList;

class InheritedLocal04 {

	public int wrongField1;

	public void foo() {
		int wrong0;
	}

	public int wrongField2;

	class Nested {

		public int wrongField5;

		public void bar() {
			int wrong6;
		}
	
		Nested(int wrong1) {

			int ok1;
			{
				int wrong1;
			}
			int ok2 = 5;
			
			// omp parallel for
			for (int i = 0; i < 10; i++) {
				int wrong2;
				if (i < 5) { String wrong3 = "foo"; }
				int wrong4 = 3;
			}
		}

		public int wrongField6;
	}

	public int wrongField3;

	public static void main(String[] args) {

		for (int i = 0; i < 10; i++) {
			int wrong2;
			if (i < 5) { String wrong3 = "foo"; }
			int wrong4 = 3;
		}

		double wrong5 = 1.2;
	}

	public int wrongField4;
}
