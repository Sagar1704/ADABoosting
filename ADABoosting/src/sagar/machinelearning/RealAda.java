package sagar.machinelearning;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * @author Sagar
 * 
 */
public class RealAda extends BinaryAda {
	private double epsilon;

	private int prPlus = 0;
	private int pwMinus = 0;
	private int prMinus = 0;
	private int pwPlus = 0;

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public RealAda() {
		super();
	}

	public StringBuilder boost() {
		StringBuilder sb = new StringBuilder();
		setClassifiers();
		for (int iterationCounter = 0; iterationCounter < T; iterationCounter++) {// 1. Select a weak classifier
			setClassiferErrors();

			Classifier classifier = classifiers.get(0);// 1. Select a weak classifier ht
			sb.append("\nIteration" + (iterationCounter + 1));
			sb.append("\nThe selected weak classifier:\n\th" + (iterationCounter + 1) + " = {\t1\tif e < "
				+ classifier.getClassifierValue() + "\n\t     {\t-1\tif e > " + classifier.getClassifierValue());

			setErroneous(classifier);
			setClassifierCounts(classifier);
			sb.append("\n\tThe G error value of of h" + (iterationCounter + 1) + ": " + classifier.getError());// 2.
																												// Find
																												// errors
																												// in
																												// the
																												// classifier
																												// Gt

			// 3. Calculate Pre-Normalization factors
			// gt(xi) =
			// { ct+ if ht(xi) = 1
			// { ct- if ht(xi) = -1
			classifier.setWrongPreNormalization(calculateCMinus());
			classifier.setRightPreNormalization(calculateCPlus());
			sb.append("\n\tThe weights\n\t\tc" + (iterationCounter + 1) + "+: " + classifier.getRightPreNormalization()
				+ "\n\t\tc" + (iterationCounter + 1) + "-: " + classifier.getWrongPreNormalization());

			calculatePreNormalizationProbabilities(classifier);// Calculate Pre-Normalization probabilities (pi)*(e
																// raised to (-yi * gt(xi)))

			classifier.setNormalizationFactor(calculateNormalizationFactor());// 4. Calculate Normalization factor Zt
			sb.append("\n\tThe probabilities normalization factor: " + classifier.getNormalizationFactor());

			sb.append("\n\tThe probabilities after normalization: ");
			calculateNewProbabilities(classifier, sb);// 5. Calculate new probabilities (pi.qi)/Zt

			calculateBoostedClassifier(classifier);
			sb.append("\n\tThe values ft(xi) for each one of the examples: ");// 6. The values of ft(xi)
			double boostedClassifierError = calculateBoostedClassifierError();

			for (ADAInput input : inputs) {
				sb.append("\t" + input.getBoostedWeight());
			}

			// 7. The error of the boosted classifier Et
			sb.append("\n\tThe error of the boosted classifier: " + boostedClassifierError);

			sb.append("\n\tThe bound on E" + (iterationCounter + 1) + ": " + calculateBound(classifier));// 8. Calculate
																											// the bound
																											// on error

			classifiers.set(0, classifier);// Replace the classifier with new error
		}
		return sb;
	}

	/**
	 * Calculate pi.qi
	 * 
	 * @param classifier
	 */
	private void calculatePreNormalizationProbabilities(Classifier classifier) {
		for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
			ADAInput input = (ADAInput) iterator.next();

			if (input.getExample() < classifier.getClassifierValue())
				input.setPreNormalizedProbability(input.getProbability()
					* Math.pow(Math.E, -1 * (input.isPositive() ? 1 : -1) * classifier.getRightPreNormalization()));
			else
				input.setPreNormalizedProbability(input.getProbability()
					* Math.pow(Math.E, -1 * (input.isPositive() ? 1 : -1) * classifier.getWrongPreNormalization()));
		}
	}

	/**
	 * Calculate the pre-normalization weight c+
	 * 
	 * @param weight
	 * @return
	 */
	private double calculateCPlus() {
		return (Math.log((prPlus + epsilon) / (pwMinus + epsilon))) / 2;
	}

	/**
	 * Calculate the pre-normalization weight c+
	 * 
	 * @param weight
	 * @return
	 */
	private double calculateCMinus() {
		return (Math.log((pwPlus + epsilon) / (prMinus + epsilon))) / 2;
	}

	/**
	 * Find the rightly and wrongly classified examples by the classifier
	 * 
	 * @param classifier
	 */
	private void setClassifierCounts(Classifier classifier) {
		prPlus = 0;
		pwMinus = 0;
		prMinus = 0;
		pwPlus = 0;
		for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
			ADAInput input = (ADAInput) iterator.next();

			if (input.getExample() < classifier.getClassifierValue() && input.isPositive()) {
				prPlus++;
			} else if (input.getExample() < classifier.getClassifierValue() && (!input.isPositive())) {
				pwMinus++;
			} else if (input.getExample() > classifier.getClassifierValue() && (!input.isPositive())) {
				prMinus++;
			} else if (input.getExample() > classifier.getClassifierValue() && input.isPositive()) {
				pwPlus++;
			}
		}
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

	/**
	 * Left is +ve And Right is -ve
	 * 
	 * @param classifier
	 * @return G = Sqrt(Pr+ * Pw-) + Sqrt(Pw+ * Pr-)
	 */
	private double calculateError(Classifier classifier) {
		int prPlus = 0;
		int prMinus = 0;
		int pwPlus = 0;
		int pwMinus = 0;
		for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
			ADAInput input = (ADAInput) iterator.next();

			if (input.getExample() < classifier.getClassifierValue() && input.isPositive()) {
				prPlus++;
			} else if (input.getExample() < classifier.getClassifierValue() && (!input.isPositive())) {
				pwMinus++;
			} else if (input.getExample() > classifier.getClassifierValue() && (!input.isPositive())) {
				prMinus++;
			} else if (input.getExample() > classifier.getClassifierValue() && input.isPositive()) {
				pwPlus++;
			}
		}

		return Math.sqrt(prPlus * pwMinus) + Math.sqrt(pwPlus * prMinus);
	}

}
