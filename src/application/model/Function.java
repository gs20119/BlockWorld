package application.model;

public abstract class Function {
	double[] X; // stores x
	double[][] Xbatch;
	public abstract double[] forward(double[] x);
	public abstract double[] backward(double[] dJdy);
	public abstract double[][] batchforward(double[][] xbatch);
	public abstract double[][] batchbackward(double[][] dJdybatch);
}
