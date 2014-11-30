package sagar.machinelearning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * @author Sagar
 * 
 */
public class BinaryAda {
	protected int T;
	protected int n;
	protected ArrayList<ADAInput> inputs;

	protected ArrayList<Classifier> classifiers;
	protected ArrayList<Classifier> boostedClassifier;
	protected double bound;

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

	public String getBoostedClassifier() {
		String booster = "";
		for (Classifier classifier : boostedClassifier) {
			booster += classifier.getWeight() + " * I(x " + (classifier.isLeftPositive() ? "<" : ">") + " "
				+ classifier.getClassifierValue() + ") + ";
		}
		return booster.substring(0, booster.length() - 3);
	}

	public BinaryAda() {
		this.boostedClassifier = new ArrayList<Classifier>();
		this.bound = 1;
		this.inputs = new ArrayList<ADAInput>();
	}

	public StringBuilder boost() {
		StringBuilder sb = new StringBuilder();
		for (int iterationCounter = 0; iterationCounter < T; iterationCounter++) {
			setClassiferErrors();
			Classifier classifier = classifiers.get(0);// 1. Select a weak classifier ht
			sb.append("\nIteration" + (iterationCounter + 1));
			sb.append("\nThe selected weak classifier:\n\th" + (iterationCounter + 1) + " = {\t1\tif e "
				+ (classifier.isLeftPositive() ? "<" : ">") + " " + classifier.getClassifierValue()
				+ "\n\t     {\t-1\tif e " + (classifier.isLeftPositive() ? ">" : "<") + " "
				+ classifier.getClassifierValue());

			initializeInputErrors();
			setErroneous(classifier);// 2. Find errors in the classifier (epsilon)t
			sb.append("\n\tThe error of h" + (iterationCounter + 1) + ": " + classifier.getError());

			classifier.setWeight(calculateWeight(classifier.getError()));// 3. Calculate goodness weight at
			sb.append("\n\tThe weight of h" + (iterationCounter + 1) + ": " + classifier.getWeight());

			// Calculate Pre-Normalization factors qi(Wrong) and qi(Right)
			classifier.setWrongPreNormalization(calculatePreNormalizationFactor(classifier.getWeight()));
			classifier.setRightPreNormalization(calculatePreNormalizationFactor(-1 * classifier.getWeight()));

			calculatePreNormalizationProbabilities(classifier);// Calculate Pre-Normalization probabilities pi.qi

			classifier.setNormalizationFactor(calculateNormalizationFactor());// 4. Calculate Normalization factor Zt
			sb.append("\n\tThe probabilities normalization factor: " + classifier.getNormalizationFactor());

			sb.append("\n\tThe probabilities after normalization: ");
			calculateNewProbabilities(classifier, sb);// 5. Calculate new probabilities (pi.qi)/Zt

			calculateBoostedClassifier(classifier);// 6. Formulate the new boosted classifier ft
			sb.append("\n\tThe boosted classifier: ft = " + getBoostedClassifier());

			// 7. The error of the boosted classifier Et
			sb.append("\n\tThe error of the boosted classifier: " + calculateBoostedClassifierError());
			sb.append("\n\tThe bound on E" + (iterationCounter + 1) + ": " + calculateBound(classifier));// 8. Calculate
																											// the bound
																											// on error

			classifiers.set(0, classifier);// Replace the classifier with new error

		}

		return sb;
	}

	protected void initializeInputErrors() {
		for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
			ADAInput input = (ADAInput) iterator.next();
			input.setErroneous(false);
		}
	}

	/**
	 * Find the wrongly classified examples by the classifier
	 * 
	 * @param classifier
	 */
	protected void setErroneous(Classifier classifier) {
		if (classifier.isLeftPositive()) {
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
		} else {
			for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
				ADAInput input = (ADAInput) iterator.next();

				if (input.getExample() < classifier.getClassifierValue()) {
					if (input.isPositive()) {
						input.setErroneous(true);
					}
				} else {
					if (!input.isPositive()) {
						input.setErroneous(true);
					}
				}
			}
		}
	}

	/**
	 * @param classifier
	 * @return the product of bounds
	 */
	protected double calculateBound(Classifier classifier) {
		bound *= classifier.getNormalizationFactor();
		return bound;
	}

	/**
	 * Find the ratio of the wrongly classified examples over all the examples by new boosted classifier
	 * 
	 * @param classifier
	 * @return
	 */
	protected double calculateBoostedClassifierError() {
		double boostedClassifierError = 0;
		for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
			ADAInput input = (ADAInput) iterator.next();

			this.calculateBoostedWeight(input);
			if (!xnor(input.getBoostedWeight() > 0, input.isPositive())) {
				boostedClassifierError += 1;
			}
		}
		return boostedClassifierError / n;
	}

	public void calculateBoostedWeight(ADAInput input) {
		input.setBoostedWeight(0.0f);
		for (Classifier classifier : boostedClassifier) {
			double weight = classifier.getWeight();
			double threshold = classifier.getClassifierValue();
			input.setBoostedWeight(input.getBoostedWeight()
				+ (weight * classify(input, threshold, classifier.isLeftPositive())));
		}
	}

	protected boolean xnor(boolean b, boolean inputValue) {
		return b == inputValue;
	}

	/**
	 * Classify the input via new boosted classifier
	 * 
	 * @param input
	 * @param threshold
	 * @param leftPositive
	 * @return
	 */
	private int classify(ADAInput input, double threshold, boolean leftPositive) {
		if ((leftPositive && (input.getExample() < threshold)) || (!leftPositive && (input.getExample() > threshold)))
			return 1;
		return -1;
	}

	/**
	 * Find the combined classifier formed after ADA Boosting
	 * 
	 * @param classifier
	 */
	protected void calculateBoostedClassifier(Classifier classifier) {
		Classifier boosted = new Classifier(classifier); 
		boostedClassifier.add(boosted);
	}

	/**
	 * Calculate new probabilities using the normalization factor
	 * 
	 * @param classifier
	 * @param sb
	 */
	protected void calculateNewProbabilities(Classifier classifier, StringBuilder sb) {
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
	protected double calculateNormalizationFactor() {
		double z = 0.0f;
		for (ADAInput input : inputs) {
			z += input.getPreNormalizedProbability();
		}
		return z;
	}

	/**
	 * Calculate pi.qi
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
	 * (qi(wrong) and qi(right))
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
	 * Left is +ve And Right is -ve for odd classifiers
	 * Right is +ve And Left is -ve for even classifiers
	 * 
	 * @param classifier
	 * @return sum of probabilities of wrongly classified inputs
	 */
	private double calculateError(Classifier classifier) {
		double error = 0.0f;
		if (classifier.isLeftPositive()) {
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
		} else {
			for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
				ADAInput input = (ADAInput) iterator.next();

				if (input.getExample() < classifier.getClassifierValue()) {
					if (input.isPositive()) {
						error += input.getProbability();
					}
				} else {
					if (!input.isPositive()) {
						error += input.getProbability();
					}
				}
			}
		}
		return error;
	}

	public void setClassiferErrors() {
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
	protected void setClassifiers() {
		classifiers = new ArrayList<Classifier>();
		int elementCounter = 0;
		int iterationCounter = 0;
		classifiers.add(new Classifier(inputs.get(0).getExample() - 0.5f, true));
		classifiers.add(new Classifier(inputs.get(0).getExample() - 0.5f, false));
		while (iterationCounter < inputs.size() - 1) {
			if (elementCounter < inputs.size() && (elementCounter + 1) < inputs.size()) {
				classifiers.add(new Classifier((inputs.get(elementCounter).getExample() + inputs
					.get(elementCounter + 1).getExample()) / 2, true));
				classifiers.add(new Classifier((inputs.get(elementCounter).getExample() + inputs
					.get(elementCounter + 1).getExample()) / 2, false));
				iterationCounter++;
			}

			elementCounter = (elementCounter + 1) % n;
		}
		classifiers.add(new Classifier(inputs.get(inputs.size() - 1).getExample() + 0.5f, true));
		classifiers.add(new Classifier(inputs.get(inputs.size() - 1).getExample() + 0.5f, false));
	}

	public BinaryAda(int t, int n, ArrayList<ADAInput> inputs) {
		super();
		T = t;
		this.n = n;
		this.inputs = new ArrayList<ADAInput>();
		for (ADAInput adaInput : inputs) {
			this.inputs.add(adaInput);
		}
		boostedClassifier = new ArrayList<Classifier>();
		bound = 1;
		setClassifiers();
	}
}
