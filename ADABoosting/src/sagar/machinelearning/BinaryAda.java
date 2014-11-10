package sagar.machinelearning;

import java.util.ArrayList;
import java.util.Collections;

public class BinaryAda {
	private int T;
	private int n;
	private ArrayList<Double> x;
	private ArrayList<Boolean> y;
	private ArrayList<Double> p;

	private ArrayList<Double> classifiers;

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

	public ArrayList<Double> getX() {
		return x;
	}

	public void setX(ArrayList<Double> x) {
		this.x = x;
		Collections.sort(this.x);
	}

	public ArrayList<Boolean> getY() {
		return y;
	}

	public void setY(ArrayList<Boolean> y) {
		this.y = y;
	}

	public ArrayList<Double> getP() {
		return p;
	}

	public void setP(ArrayList<Double> p) {
		this.p = p;
	}

	public BinaryAda(int t, int n, ArrayList<Double> x, ArrayList<Boolean> y, ArrayList<Double> p) {
		super();
		T = t;
		this.n = n;
		this.x = x;
		this.y = y;
		this.p = p;
		setClassifiers();
	}

	public BinaryAda() {
		setClassifiers();
	}

	public void boost() {
		for (int iterationCounter = 0; iterationCounter < classifiers.size(); iterationCounter++) {// 1. Select a weak
																									// classifier
			
		}
	}

	public ArrayList<Double> getClassifiers() {
		return classifiers;
	}

	private void setClassifiers() {
		int elementCounter = 0;
		int iterationCounter = 0;
		classifiers.add(x.get(0) - 1);
		while (iterationCounter < T - 2) {
			if (elementCounter < x.size() && (elementCounter + 1) < x.size()) {
				classifiers.add((x.get(elementCounter) + x.get(elementCounter + 1)) / 2);
				iterationCounter++;
			}

			elementCounter = (elementCounter + 1) % n;
		}
		classifiers.add(x.get(x.size()) + 1);
	}
}
