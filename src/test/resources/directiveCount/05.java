import java.util.ArrayList;

class InheritedLocal01 {
	public static void main(String[] args) {

		{
			int wrong1;
		}

		int ok1;
		{
			int ok2;
			{
				int wrong2;
				{
					int wrong3;
				}
			}

			int ok3;
			// omp for parallel
			{
				int wrong4;
			}

			int wrong5;
			{
				int wrong6;
				{
					int wrong7;
				}
			}
			int wrong8;
		}
		int wrong9;

	}
}
