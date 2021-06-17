package application.model;

public abstract class Function {
	double[] X; // stores x
	public abstract double[] forward(double[] x);
	public abstract double[] backward(double[] dJdy);
}
