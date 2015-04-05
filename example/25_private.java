import java.util.concurrent.atomic.AtomicInteger;

class Private {
	public static void main(String[] args) {
		
		MyInteger five = new MyInteger(5);
		MyInteger ten = new MyInteger(10);

		// omp parallel private(five) firstprivate(ten)
		{
			System.out.println("++five: " + five.incrementAndGet());
			System.out.println("++ten: " + ten.incrementAndGet());
		}

		System.out.println("final");
		System.out.println("five: " + five);
		System.out.println("ten: " + ten);
	}
}

class MyInteger extends AtomicInteger {
	/** Default constructor*/
	public MyInteger() {
		super(0);
	}
	/** Default constructor*/
	public MyInteger(int val) {
		super(val);
	}
	/** Copy constructor */
	public MyInteger(MyInteger orig) {
		super(orig.get());
	}
}
