import java.lang.Thread;
import java.util.concurrent.atomic.AtomicInteger;

class Barrier {
	public static void main(String[] args) {

		AtomicInteger timeout = new AtomicInteger(0);
		// omp parallel
		{
			try {
				int to = timeout.incrementAndGet();
				System.out.println("Sleeping for " + to + "s");
				Thread.sleep(to * 1000);
				System.out.println("Woken up after " + to + "s");
			} catch (Exception e) {
				System.out.println("Exception");
			}

			// omp barrier
			{}

			// omp single
			{
				System.out.println("Only master after barrier!");
			}
		}
	}

}
