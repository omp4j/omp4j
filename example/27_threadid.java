class ThreadID {
	public static void main(String[] args) {
		// omp parallel
		{
			System.out.println("Thread #" + OMP4J_THREAD_NUM + "/" + OMP4J_NUM_THREADS);
		}
	}
}
