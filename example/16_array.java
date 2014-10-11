import java.lang.String;

class Arrays {

	void passing() {
		String[] yes = {"hey"};
		// omp parallel
		{
			String[] no = {"foo"};
			passed(yes);
			passed(no);
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
