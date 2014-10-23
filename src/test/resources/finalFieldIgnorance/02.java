class Top {

	void foo() {
		class Local01 extends java.io.File {
			Local01(String f) { super(f); }
			public int publicLocal01Field;
			protected int protectedLocal01Field;
			private int privateLocal01Field;
			final public int publicLocal01FieldFinal = 5;
			final protected int protectedLocal01FieldFinal = 6;
			final private int privateLocal01FieldFinal = 7;
		}
	}
}
