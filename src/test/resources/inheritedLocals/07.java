import java.lang.String;

class InheritedLocals07 {

	void use() {
		int[] arr1 = new int[15];
		int[] arr2 = new int[15];
		int[] arr3 = new int[15];
		// omp parallel
		{
			String[] no = {"foo"};
			int tmp;
			tmp = arr1[0] + arr2[1];
			arr2[0] = tmp;
			single(arr3[0]);
		}
	}

	void single(int foo) {}
}
