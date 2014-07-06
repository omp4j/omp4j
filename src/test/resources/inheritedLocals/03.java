import java.util.ArrayList;

class InheritedLocal03 {
	public static void main(String[] args) {

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

		double wrong5 = 1.2;
	}
}
