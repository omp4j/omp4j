class For {
    public void test1() {
        // omp parallel for
        for (int i = 2; i < 20; i += 3) {
            System.out.println("hello @" + i);
        }
    }

    private int field = 40;
    public void test2() {
        // omp parallel for
        for (int i = 2; i < this.field; i += 3) {
            System.out.println("hello @" + i);
        }
    }
}
