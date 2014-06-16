class Item {}
class General {
	public static void main(String[] args) {
		General f = new General();
	}

	public General () {
		// local vars
		int a = 3;
		int b;
		b = a + var1;
		Item var2;

		// print
		System.out.println(b);

		// for
		for (int i = 0; i < b; i++) {
			System.out.println(i);
		}
	}

	// members
	public int var1 = 4;
	private Item var2;
	public int var3;
}
