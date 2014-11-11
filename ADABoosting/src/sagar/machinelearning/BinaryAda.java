package sagar.machinelearning;

import java.util.ArrayList;

public class BinaryAda {
	private int T;
	private int n;
	private ArrayList<ADAInput> inputs;

	private ArrayList<Classifier> classifiers;

	public ArrayList<ADAInput> getInputs() {
		return inputs;
	}

	public void setInputs(ArrayList<ADAInput> inputs) {
		this.inputs = inputs;
	}

	public int getT() {
		return T;
	}

	public void setT(int t) {
		T = t;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public BinaryAda() {
		setClassifiers();
	}

	public void boost() {
		StringBuilder sb = new StringBuilder();
		for (int iterationCounter = 0; iterationCounter < classifiers.size(); iterationCounter++) {// 1. Select a weak
																									// classifier
			sb.append("\nSelected weak classifier ::\n\th" + (iterationCounter + 1) + " = {\t 1\tif e < "
				+ classifiers.get(iterationCounter) + "\n\t\t{-1\tif e > " + classifiers.get(iterationCounter));

			Classifier classifier = classifiers.get(iterationCounter);
			classifier.setError(calculateError(classifiers.get(iterationCounter)));
			sb.append("\n\tError in classifier:: " + classifier.getError());

			classifier.setWeight(calculateWeight(classifier.getError()));
			sb.append("\n\tThe Goodness weight:: " + classifier.getWeight());

			classifier.setWrongPreNormalization(calculatePreNormalizationFactor(classifier.getWeight()));
			classifier.setRightPreNormalization(calculatePreNormalizationFactor(-1 * classifier.getWeight()));

			calculatePreNormalizationProbabilities(classifier);
		}
	}

	private void calculatePreNormalizationProbabilities(Classifier classifier) {
		for (ADAInput input : inputs) {
			
		}
	}

	/**
	 * Calculate the pre-normalization factor based on the classification and weight
	 * 
	 * @param weight
	 * @return e raised to weight
	 */
	private double calculatePreNormalizationFactor(double weight) {
		return Math.pow(Math.E, weight);
	}

	/**
	 * Goodness Weight
	 * 
	 * @param error
	 * @return (1/2) * ln ((1 - E) / E)
	 */
	private double calculateWeight(double error) {
		return Math.log((1 - error) / error) / 2;
	}

	/**
	 * Left is +ve And Right is -ve
	 * 
	 * @param classifier
	 * @return sum of probabilities of wrongly classified inputs
	 */
	private double calculateError(Classifier classifier) {
		double error = 0.0f;
		for (ADAInput input : inputs) {
			if (input.getExample() < classifier.getClassifierValue()) {
				if (!input.isPositive())
					error += input.getProbability();
			} else {
				if (input.isPositive())
					error += input.getProbability();
			}
		}
		return error;
	}

	public ArrayList<Classifier> getClassifiers() {
		return classifiers;
	}

	/**
	 * Generate the weak classifiers from the inputs provided
	 */
	private void setClassifiers() {
		int elementCounter = 0;
		int iterationCounter = 0;
		classifiers.add(new Classifier(inputs.get(0).getExample() - 0.5f));
		while (iterationCounter < T - 2) {
			if (elementCounter < inputs.size() && (elementCounter + 1) < inputs.size()) {
				classifiers.add(new Classifier((inputs.get(elementCounter).getExample() + inputs
					.get(elementCounter + 1).getExample()) / 2));
				iterationCounter++;
			}

			elementCounter = (elementCounter + 1) % n;
		}
		classifiers.add(new Classifier(inputs.get(inputs.size()).getExample() + 0.5f));
	}

	public BinaryAda(int t, int n, ArrayList<ADAInput> inputs) {
		super();
		T = t;
		this.n = n;
		for (ADAInput adaInput : inputs) {
			this.inputs.add(adaInput);
		}
	}
}
