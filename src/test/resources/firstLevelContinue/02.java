import java.util.ArrayList;

class FirstLevelContinueExtractorTest02 {
	public static void main(String[] args) {
		int i =0;
		// omp parallel for
		for (; i < 10; i++) {
			continue;

			for (;;) {
				continue;
			}

			continue;

			while (true) {
				continue;
			}

			continue;
		}
	}
}
