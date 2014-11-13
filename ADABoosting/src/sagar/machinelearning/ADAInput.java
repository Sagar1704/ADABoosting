package sagar.machinelearning;

public class ADAInput {
	private double example;
	private boolean value;
	private double probability;
	private boolean erroneous;

	private double preNormalizedProbability;

	public ADAInput(double example, boolean value, double probability) {
		super();
		this.example = example;
		this.value = value;
		this.probability = probability;
		this.erroneous = false;
	}

	public ADAInput() {
		this.erroneous = false;
	}

	public double getExample() {
		return example;
	}

	public void setExample(double example) {
		this.example = example;
	}

	public boolean isPositive() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public double getPreNormalizedProbability() {
		return preNormalizedProbability;
	}

	public void setPreNormalizedProbability(double preNormalizedProbability) {
		this.preNormalizedProbability = preNormalizedProbability;
	}

	public boolean isErroneous() {
		return erroneous;
	}

	public void setErroneous(boolean erroneous) {
		this.erroneous = erroneous;
	}

}
