class SuperParent {
	public void publicSuperInherited() {}
	protected void protectedSuperInherited() {}
	private void privateSuperInherited() {}

	public int publicSuperInheritedField;
	protected int protectedSuperInheritedField;
	private int privateSuperInheritedField;

	final public int publicSuperInheritedFieldFinal = 1;
	final protected int protectedSuperInheritedFieldFinal = 1;
	final private int privateSuperInheritedFieldFinal = 1;

}

class Parent extends SuperParent {
	public void publicInherited() {}
	protected void protectedInherited() {}
	private void privateInherited() {}

	public int publicInheritedField;
	protected int protectedInheritedField;
	private int privateInheritedField;
}

class Child extends Parent {
	public void publicNewMethod() {}
	protected void protectedNewMethod() {}
	private void privateNewMethod() {}

	public int publicNewField;
	protected int protectedNewField;
	private int privateNewField;
}
