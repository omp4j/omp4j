class Lambda {
	public static void main(String[] args) {
		Lambda l = new Lambda();
	}

	public Lambda () {
		Runnable lambda = () -> System.out.println("lambda");
		lambda.run();
	}
}
