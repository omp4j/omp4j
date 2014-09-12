class Top {

	void foo() {
		class Local01 {
			public int publicLocal01Field;
			protected int protectedLocal01Field;
			private int privateLocal01Field;
		}

		for (;;) {
			class Local02 extends Local01{
				public int publicLocal02Field;
				protected int protectedLocal02Field;
				private int privateLocal02Field;
			}
			
		}
	}
}
