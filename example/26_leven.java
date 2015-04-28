import java.util.Random;

class Levenshtein {

	public Levenshtein(int workload) {
		s = new int[workload+1];
		t = new int[workload+1];
		Random r = new Random(31011993);

		for (int i = 0; i < workload; i++) {
			s[i] = r.nextInt(1000);
			t[i] = r.nextInt(1000);
		}
	}

	private int[] s;
	private int[] t;
	final private static int INF = Integer.MAX_VALUE;

	private int min3(int a, int b, int c) {
		return Math.min(a, Math.min(b,c));
	}

	public void runBenchmark() { 
	 	final int M = s.length;
		final int N = t.length;

		if (M < N) {
			int[] tmp = s;
			s = t;
			t = tmp;
			runBenchmark();
			return;
		}

		if (M == 0) return /*N*/;
		if (N == 0) return /*M*/;

		int prepre[] = new int[N + 1];
		int pre[] = new int[N + 1];
		int curr[] = new int[N + 1];

		int offset, front_offset = 0, back_offset = 0;
		int i, j;
		int cost;
		int stop;

		for (int wave = 0; wave < M + N + 1; wave++) { // wave nr.

			stop = N+1;
			offset = 1;

			curr[0] = wave;

			// bounds
			if (wave <= N) {	// before top break
				curr[wave] = wave;
				stop = wave;
			}
			else if (wave > M) {	// after bottom break
				offset = wave - M;
			}

			// top corner
			if (N/2 <= wave && wave <= N) {
				if (wave % 2 == 1) back_offset++;
				stop -= back_offset;
				curr[stop] = INF-1;
			}
			else if (N < wave && wave <= N + N/2) {
				if (wave % 2 == 0) back_offset--;
				stop -= back_offset;
				curr[stop] = INF-1;
			}

			// bottom corner
			if (M - N/2 < wave && wave <= M) {
				if (wave % 2 == 1) front_offset++;
				offset += front_offset;
				curr[offset] = INF-1;
			}
			else if (M < wave && wave <= M + N/2) {
				if (wave % 2 == 0) front_offset--;
				offset += front_offset;
				curr[offset] = INF-1;
			}

			// omp parallel for private(i, j, cost)
			for (int pos = offset; pos < stop; pos++) { // wave position
				j = pos;
				i = wave - j;

				cost = (s[i-1] == t[j-1]) ? 0 : 1;	// s[i-1] != t[j-1] // actually absolutely same time
				curr[pos] = min3(pre[pos-1] + 1, pre[pos] + 1, prepre[pos-1] + cost);
			}

			prepre = pre;
			pre = curr;
		}

		// return pre[N];
	}
}
