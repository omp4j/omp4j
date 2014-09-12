class Top {

	void foo() {
		class Local01 {
			public int publicLocal01Field;
			protected int protectedLocal01Field;
			private int privateLocal01Field;
		}
		class Local02 {
			void goo() {
				class NLocal01 {}
			}
		}
	}

	void hoo() {
		class Local03 {}
		class Local04 {
			void goo() {
				class NLocal02 {}
			}
		}
	}

}
