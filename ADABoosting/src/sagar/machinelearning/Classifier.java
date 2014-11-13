package sagar.machinelearning;

public class Classifier {
	private double classifierValue;
	private double error;
	private double weight;

	private double wrongPreNormalization;
	private double rightPreNormalization;
	private double normalizationFactor;

	public Classifier(double classifierValue) {
		super();
		this.classifierValue = classifierValue;
	}

	public Classifier() {
	}

	public double getClassifierValue() {
		return classifierValue;
	}

	public void setClassifierValue(double classifierValue) {
		this.classifierValue = classifierValue;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWrongPreNormalization() {
		return wrongPreNormalization;
	}

	public void setWrongPreNormalization(double wrongPreNormalization) {
		this.wrongPreNormalization = wrongPreNormalization;
	}

	public double getRightPreNormalization() {
		return rightPreNormalization;
	}

	public void setRightPreNormalization(double rightPreNormalization) {
		this.rightPreNormalization = rightPreNormalization;
	}

	public double getNormalizationFactor() {
		return normalizationFactor;
	}

	public void setNormalizationFactor(double normalizationFactor) {
		this.normalizationFactor = normalizationFactor;
	}

}
