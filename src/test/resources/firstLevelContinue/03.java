class FirstLevelContinueExtractorTest03 {
    public static void main(String[] args) {
        myLabel: for (int j = 0; j < 35; j++) {
            // omp parallel for
            for (int i = 0; i < 10; i++) {
                if (i+j % 15 == 0) continue myLabel;
            }
        }
    }
}
