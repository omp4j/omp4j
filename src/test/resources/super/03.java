class SuperTest01 {
	public void run() {
		// omp parallel
		{
			new Runnable() {
				@Override
				public void run() {
					super.hashCode();
				}
			};
		}
	}
}
