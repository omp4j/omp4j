import java.util.ArrayList;

class InheritedLocal02 {
	public static void main(String[] args) {

		int ok1;
		int ok2 = 5;

		for (int i = 0; i < 10; i++) {
			int wrong1;
			if (i < 5) { String wrong2 = "foo"; }
			int wrong3 = 3;
		}

		int ok3;

		if (5 < 10) {
			double wrong4 = 1.2;
		}

		String ok4 = "heey";

		{
			int wrong5;
		}

		int ok5;
		{
			float ok6;
			{
				int wrong6;
				{
					int wrong7;
				}
			}

			int ok7;
			// omp parallel
			{
				int wrong8;
				for (int i = 0; i < 10; i++) {
					int wrong9;
					if (i < 5) { String wrong10 = "foo"; }
					int wrong11 = 3;
				}
			}

			int wrong12;
			{
				int wrong13;
				{
					int wrong14;
				}
			}
			int wrong15;

			for (int i = 0; i < 10; i++) {
				int wrong16;
				if (i < 5) { String wrong17 = "foo"; }
				int wrong18 = 3;
			}

		}
		int wrong19;

	}
}
