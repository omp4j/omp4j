class Top {

	void foo() {
		class Local01 {}
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
