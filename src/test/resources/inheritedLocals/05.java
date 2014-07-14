import java.util.ArrayList;

class InheritedLocal05 {

	public int wrongField1;

	public void foo() {
		int wrong0;
	}

	public int wrongField2;

	public static void main(String[] args) {

		int ok1;

		button.addListener(new Listener() {
			@Override
			void action() {
				int ok2;

				// omp parallel
				{
					int wrong = 4;
					foo(wrong);
				}
			}
		});

		for (int i = 0; i < 10; i++) {
			int wrong2;
			if (i < 5) { String wrong3 = "foo"; }
			int wrong4 = 3;
		}

		double wrong5 = 1.2;
	}

	public int wrongField4;
}
