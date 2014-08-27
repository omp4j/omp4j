import java.util.ArrayList;

class FirstLevelBreakExtractorTest02 {
	public static void main(String[] args) {
		int i =0;
		// omp parallel for
		for (; i < 10; i++) {
			break;

			for (;;) {
				break;
			}

			break;

			while (true) {
				break;
			}

			break;
		}
	}
}
