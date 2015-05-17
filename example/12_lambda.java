import java.util.List;
import java.util.Arrays;

@FunctionalInterface
interface IntInterface {
	public void work(int a);
}

@FunctionalInterface
interface IntIntInterface {
	public int work(int a, int b);
}


class LambaExample {
	void foo () {

		Runnable r = () -> System.out.println("hello world");

		IntInterface s = (p) -> {
			{
				System.out.println("Hello World");
			}
		};

		IntIntInterface t =  (int a, int b) -> {
			// omp parallel
			{
				System.out.println("Hello World");
			}
			return a + b;
		};
 
	}
}
