package sagar.machinelearning;

import java.util.ArrayList;

public class RealAda extends BinaryAda {
	private double epsilon;

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public RealAda(int t, int n, ArrayList<Double> x, ArrayList<Boolean> y, ArrayList<Double> p, double epsilon) {
		super(t, n, x, y, p);
		this.epsilon = epsilon;
	}

	public RealAda() {
	}

	public void boost() {
		
	}
}
