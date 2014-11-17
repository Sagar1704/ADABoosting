package sagar.machinelearning;

/**
 * This is the classifier ht with:
 * 1. classifying value < or > <some value>
 * 2. error associated with it
 * 3. Goodness weight - alpha
 * 4. Normalization factor - Z
 * 
 * @author Sagar
 * 
 */
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(classifierValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Classifier other = (Classifier) obj;
		if (Double.doubleToLongBits(classifierValue) != Double.doubleToLongBits(other.classifierValue))
			return false;
		return true;
	}

}
