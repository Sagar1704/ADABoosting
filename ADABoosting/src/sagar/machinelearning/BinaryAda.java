package sagar.machinelearning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class BinaryAda {
	private int T;
	private int n;
	private ArrayList<ADAInput> inputs;

	private ArrayList<Classifier> classifiers;
	private String boostedClassifier;
	private double bound;

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
		this.boostedClassifier = "";
		this.bound = 1;
		this.inputs = new ArrayList<ADAInput>();
		// setClassifiers();
	}

	public StringBuilder boost() {
		StringBuilder sb = new StringBuilder();
		for (int iterationCounter = 0; iterationCounter < T; iterationCounter++) {// 1. Select a weak
																					// classifier
			setClassiferErrors();
			Classifier classifier = classifiers.get(0);
			sb.append("Iteration" + (iterationCounter + 1));
			sb.append("\nThe selected weak classifier:\n\th" + (iterationCounter + 1) + " = {\t1\tif e < "
				+ classifier.getClassifierValue() + "\n\t     {\t-1\tif e > " + classifier.getClassifierValue());
			
			setErroneous(classifier);
			sb.append("\n\tThe error of h" + (iterationCounter + 1) + ": " + classifier.getError());

			classifier.setWeight(calculateWeight(classifier.getError()));
			sb.append("\n\tThe weight of h" + (iterationCounter + 1) + ": " + classifier.getWeight());

			classifier.setWrongPreNormalization(calculatePreNormalizationFactor(classifier.getWeight()));
			classifier.setRightPreNormalization(calculatePreNormalizationFactor(-1 * classifier.getWeight()));

			calculatePreNormalizationProbabilities(classifier);

			classifier.setNormalizationFactor(calculateNormalizationFactor());
			sb.append("\n\tThe probabilities normalization factor: " + classifier.getNormalizationFactor());

			sb.append("\n\tThe probabilities after normalization: ");
			calculateNewProbabilities(classifier, sb);

			calculateBoostedClassifier(classifier);
			sb.append("\n\tThe boosted classifier: ft = "
				+ boostedClassifier.substring(0, boostedClassifier.length() - 2));

			sb.append("\n\tThe error of the boosted classifier: " + calculateBoostedClassifierError(classifier));
			sb.append("\n\tThe bound on E" + (iterationCounter + 1) + ": " + calculateBound(classifier));

			classifiers.add(0, classifier);
		}

		return sb;
	}

	/**
	 * Find the wrongly classified examples by the classifier
	 * 
	 * @param classifier
	 */
	private void setErroneous(Classifier classifier) {
		for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
			ADAInput input = (ADAInput) iterator.next();

			if (input.getExample() < classifier.getClassifierValue()) {
				if (!input.isPositive()) {
					input.setErroneous(true);
				}
			} else {
				if (input.isPositive()) {
					input.setErroneous(true);
				}
			}
		}

	}

	/**
	 * @param classifier
	 * @return the product of bounds
	 */
	private double calculateBound(Classifier classifier) {
		bound *= classifier.getNormalizationFactor();
		return bound;
	}

	/**
	 * Find the ratio of the wrongly classified examples over all the examples by new boosted classifier
	 * 
	 * @param classifier
	 * @return
	 */
	private double calculateBoostedClassifierError(Classifier classifier) {
		double boostedClassifierError = 0;
		for (ADAInput input : inputs) {
			String booster = boostedClassifier;
			double boostedWeight = 0.0f;
			while (booster.contains("<")) {
				double weight = Double.parseDouble(booster.substring(0, booster.indexOf("I")).trim());
				double threshold = Double.parseDouble(booster.substring(booster.indexOf("<") + 1, booster.indexOf(")"))
					.trim());
				boostedWeight += weight * classify(input, threshold);

				booster = booster.substring(booster.indexOf(")") + 4);
			}
			if (!xnor(boostedWeight > 0, input.isPositive())) {
				boostedClassifierError += 1;
			}
		}
		return boostedClassifierError / n;
	}

	private boolean xnor(boolean b, boolean inputValue) {
		return b == inputValue;
	}

	/**
	 * Classify the input via new boosted classifier
	 * 
	 * @param input
	 * @param threshold
	 * @return
	 */
	private int classify(ADAInput input, double threshold) {
		if (input.getExample() < threshold)
			return 1;
		return -1;
	}

	/**
	 * Find the combined classifier formed after ADA Boosting
	 * 
	 * @param classifier
	 */
	private void calculateBoostedClassifier(Classifier classifier) {
		boostedClassifier += classifier.getWeight() + " I(x < " + classifier.getClassifierValue() + ") + ";
	}

	/**
	 * Calculate new probabilities using the normalization factor
	 * 
	 * @param classifier
	 * @param sb
	 */
	private void calculateNewProbabilities(Classifier classifier, StringBuilder sb) {
		for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
			ADAInput input = (ADAInput) iterator.next();
			input.setProbability(input.getPreNormalizedProbability() / classifier.getNormalizationFactor());
			sb.append("\t" + input.getProbability());
		}
	}

	/**
	 * Calculate the normalization factor to normalize and get the new probabilities
	 * 
	 * @return sum of all pre-normalized probabilities
	 */
	private double calculateNormalizationFactor() {
		double z = 0.0f;
		for (ADAInput input : inputs) {
			z += input.getPreNormalizedProbability();
		}
		return z;
	}

	/**
	 * Calculate q(wrong) and q(right)
	 * 
	 * @param classifier
	 */
	private void calculatePreNormalizationProbabilities(Classifier classifier) {
		for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
			ADAInput input = (ADAInput) iterator.next();

			if (input.isErroneous())
				input.setPreNormalizedProbability(input.getProbability() * classifier.getWrongPreNormalization());
			else
				input.setPreNormalizedProbability(input.getProbability() * classifier.getRightPreNormalization());
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
		for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
			ADAInput input = (ADAInput) iterator.next();

			if (input.getExample() < classifier.getClassifierValue()) {
				if (!input.isPositive()) {
					error += input.getProbability();
				}
			} else {
				if (input.isPositive()) {
					error += input.getProbability();
				}
			}
		}
		return error;
	}

	private void setClassiferErrors() {
		for (Iterator<Classifier> iterator = classifiers.iterator(); iterator.hasNext();) {
			Classifier classifier = (Classifier) iterator.next();
			classifier.setError(calculateError(classifier));
		}
		Collections.sort(classifiers, new Comparator<Classifier>() {

			@Override
			public int compare(Classifier o1, Classifier o2) {
				return o1.getError() == o2.getError() ? 0 : (o1.getError() < o2.getError() ? -1 : 1);
			}

		});
	}

	public ArrayList<Classifier> getClassifiers() {
		return classifiers;
	}

	/**
	 * Generate the weak classifiers from the inputs provided
	 */
	private void setClassifiers() {
		classifiers = new ArrayList<Classifier>();
		int elementCounter = 0;
		int iterationCounter = 0;
		classifiers.add(new Classifier(inputs.get(0).getExample() - 0.5f));
		while (iterationCounter < inputs.size() - 1) {
			if (elementCounter < inputs.size() && (elementCounter + 1) < inputs.size()) {
				classifiers.add(new Classifier((inputs.get(elementCounter).getExample() + inputs
					.get(elementCounter + 1).getExample()) / 2));
				iterationCounter++;
			}

			elementCounter = (elementCounter + 1) % n;
		}
		classifiers.add(new Classifier(inputs.get(inputs.size() - 1).getExample() + 0.5f));
	}

	public BinaryAda(int t, int n, ArrayList<ADAInput> inputs) {
		super();
		T = t;
		this.n = n;
		this.inputs = new ArrayList<ADAInput>();
		for (ADAInput adaInput : inputs) {
			this.inputs.add(adaInput);
		}
		boostedClassifier = "";
		bound = 1;
		setClassifiers();
	}
}
