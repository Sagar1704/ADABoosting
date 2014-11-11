package sagar.machinelearning.enums;

public enum Sign {
	POSITIVE(true), NEGATIVE(false);

	private boolean signValue;

	private Sign(boolean signValue) {
		this.signValue = signValue;
	}

	public boolean getSignValue() {
		return signValue;
	}

}
