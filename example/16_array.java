import java.lang.String;

class Arrays {

	protected String[] arrF = new String[15];
	protected String[][] arrF2 = new String[15][10];
	protected byte[][] arrB2 = new byte[15][10];
	protected short xxx = 15;

	void passing() {
		String[] yes = {"hey"};
		// omp parallel
		{
			String[] no = {"foo"};
			passed(yes);
			passed(no);
			passed(arrF);
			arrF2[0][0] = "foo";
			arrB2[0][0] = 0;
			xxx = 12;
		}
	}

	void passed(String[] param) {
		param[0] = "hello";
	}

	void use() {
		int[] arr1 = new int[15];
		int[] arr2 = new int[15];
		int[] arr3 = new int[15];
		// omp parallel
		{
			int tmp;
			tmp = arr1[0] + arr2[1];
			arr2[0] = tmp;
			single(arr3[0]);
		}
	}

	void single(int foo) {}
}
