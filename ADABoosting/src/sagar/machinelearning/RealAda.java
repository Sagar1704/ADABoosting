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

	private double prPlus = 0.0f;
	private double pwMinus = 0.0f;
	private double prMinus = 0.0f;
	private double pwPlus = 0.0f;

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
			sb.append("\n\nIteration " + (iterationCounter + 1));
			/*
			 * sb.append("\nThe selected weak classifier:\n\th" + (iterationCounter + 1) + " = {\t1\tif e "
			 * + (classifier.isLeftPositive() ? "<" : ">") + " " + classifier.getClassifierValue()
			 * + "\n\t     {\t-1\tif e " + (classifier.isLeftPositive() ? ">" : "<") + " "
			 * + classifier.getClassifierValue());
			 */

			sb.append("\nClassifier h = I(x " + (classifier.isLeftPositive() ? "<" : ">") + " "
				+ classifier.getClassifierValue() + ")");

			initializeInputErrors();
			setErroneous(classifier);
			setClassifierCounts(classifier);
			sb.append("\nG error = " + classifier.getError());// 2.
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
			/*
			 * sb.append("\n\tThe weights\n\t\tc" + (iterationCounter + 1) + "+: " +
			 * classifier.getRightPreNormalization()
			 * + "\n\t\tc" + (iterationCounter + 1) + "-: " + classifier.getWrongPreNormalization());
			 */
			sb.append("\nC_Plus = " + classifier.getRightPreNormalization() + ", C_Minus = "
				+ classifier.getWrongPreNormalization());

			calculatePreNormalizationProbabilities(classifier);// Calculate Pre-Normalization probabilities (pi)*(e
																// raised to (-yi * gt(xi)))

			classifier.setNormalizationFactor(calculateNormalizationFactor());// 4. Calculate Normalization factor Zt
			sb.append("\nNormalization Factor Z = " + classifier.getNormalizationFactor());

			sb.append("\nPi after normalization = ");
			calculateNewProbabilities(classifier, sb);// 5. Calculate new probabilities (pi.qi)/Zt

			calculateBoostedClassifier(classifier);
			sb.append("\nf(x) =");// 6. The values of ft(xi)
			double boostedClassifierError = calculateBoostedClassifierError();

			for (ADAInput input : inputs) {
				sb.append(" " + input.getBoostedWeight() + ",");
			}
			sb.delete(sb.length() - 1, sb.length());

			// 7. The error of the boosted classifier Et
			sb.append("\nBoosted Classifier Error = " + boostedClassifierError);

			sb.append("\nBound on Error = " + calculateBound(classifier));// 8. Calculate
																			// the bound
																			// on error

			classifiers.set(0, classifier);// Replace the classifier with new error
		}
		return sb;
	}

	@Override
	public void calculateBoostedWeight(ADAInput input) {
		input.setBoostedWeight(0.0f);
		for (Classifier classifier : boostedClassifier) {
			if (classifier.isLeftPositive()) {
				if (input.getExample() < classifier.getClassifierValue())
					input.setBoostedWeight(input.getBoostedWeight() + classifier.getRightPreNormalization());
				else
					input.setBoostedWeight(input.getBoostedWeight() + classifier.getWrongPreNormalization());
			} else {
				if (input.getExample() > classifier.getClassifierValue())
					input.setBoostedWeight(input.getBoostedWeight() + classifier.getRightPreNormalization());
				else
					input.setBoostedWeight(input.getBoostedWeight() + classifier.getWrongPreNormalization());
			}
		}
	}

	/**
	 * Calculate pi.qi
	 * 
	 * @param classifier
	 */
	private void calculatePreNormalizationProbabilities(Classifier classifier) {
		if (classifier.isLeftPositive()) {
			for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
				ADAInput input = (ADAInput) iterator.next();

				if (input.getExample() < classifier.getClassifierValue())
					input.setPreNormalizedProbability(input.getProbability()
						* Math.pow(Math.E, -1 * (input.isPositive() ? 1 : -1) * classifier.getRightPreNormalization()));
				else
					input.setPreNormalizedProbability(input.getProbability()
						* Math.pow(Math.E, -1 * (input.isPositive() ? 1 : -1) * classifier.getWrongPreNormalization()));
			}
		} else {
			for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
				ADAInput input = (ADAInput) iterator.next();

				if (input.getExample() > classifier.getClassifierValue())
					input.setPreNormalizedProbability(input.getProbability()
						* Math.pow(Math.E, -1 * (input.isPositive() ? 1 : -1) * classifier.getRightPreNormalization()));
				else
					input.setPreNormalizedProbability(input.getProbability()
						* Math.pow(Math.E, -1 * (input.isPositive() ? 1 : -1) * classifier.getWrongPreNormalization()));
			}
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
		prPlus = 0.0f;
		pwMinus = 0.0f;
		prMinus = 0.0f;
		pwPlus = 0.0f;
		for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
			ADAInput input = (ADAInput) iterator.next();

			if (input.getExample() < classifier.getClassifierValue() && input.isPositive()) {
				prPlus += input.getProbability();
			} else if (input.getExample() < classifier.getClassifierValue() && (!input.isPositive())) {
				pwMinus += input.getProbability();
			} else if (input.getExample() > classifier.getClassifierValue() && (!input.isPositive())) {
				prMinus += input.getProbability();
			} else if (input.getExample() > classifier.getClassifierValue() && input.isPositive()) {
				pwPlus += input.getProbability();
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
		double prPlus = 0.0f;
		double prMinus = 0.0f;
		double pwPlus = 0.0f;
		double pwMinus = 0.0f;
		for (Iterator<ADAInput> iterator = inputs.iterator(); iterator.hasNext();) {
			ADAInput input = (ADAInput) iterator.next();

			if (input.getExample() < classifier.getClassifierValue() && input.isPositive()) {
				prPlus += input.getProbability();
			} else if (input.getExample() < classifier.getClassifierValue() && (!input.isPositive())) {
				pwMinus += input.getProbability();
			} else if (input.getExample() > classifier.getClassifierValue() && (!input.isPositive())) {
				prMinus += input.getProbability();
			} else if (input.getExample() > classifier.getClassifierValue() && input.isPositive()) {
				pwPlus += input.getProbability();
			}
		}

		return Math.sqrt(prPlus * pwMinus) + Math.sqrt(pwPlus * prMinus);
	}

}
