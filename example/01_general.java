
class Item {
	private void blankMethod1 (int i, String s) {}
	private void blankMethod2 (int i, String s) {}
}

class Parent {
	public void publicInherited() {}
	protected void protectedInherited() {}
	private void privateInherited() {}
}

class General extends Parent {
	public static void main(String[] args) {
		General f = new General();
	}

	public General () {
		// local vars
		int a = 3;
		int b;
		b = a + var1;
		Item var2;

		// print;
		System.out.println(b);

		// for
		for (int i = 0; i < b; i++) {
			System.out.println(i);
		}
	}

	public int foo (char c, Item i) {
		return var1 + 38;
	}

	public void publicNew() {}
	protected void protectedNew() {}
	private void privateNew() {}


	// members
	public int var1 = 4;
	private Item var2;
	public int var3;
}
