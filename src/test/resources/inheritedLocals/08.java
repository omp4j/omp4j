import java.lang.String;

class InheritedLocals08 {

	public int x = 5;

	void use() {
		// omp parallel
		{
			x++;
			// omp parallel
			{x++;}

			int x = 4;
			// omp parallel
			{if (x < 5) x--;}

		}
	}
}
