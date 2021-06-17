package application.model;

public abstract class Activation extends Function { }

class ReLU extends Activation {

	@Override
	public double[] forward(double[] x) {
		X = x.clone();
		double[] y = new double[X.length];
		for(int i=0; i<X.length; i++) y[i]=Math.max(0, X[i]);
		return y;
	}

	@Override
	public double[] backward(double[] dJdy) {
		double[] dJdx = new double[X.length];
		double[] dydx = new double[X.length];
		for(int i=0; i<X.length; i++) dydx[i]=(X[i]>0)?1:0;
		for(int i=0; i<X.length; i++) dJdx[i]=dJdy[i]*dydx[i];
		return dJdx;
	}
	
}

class Sigmoid extends Activation {

	private double sigm(double x){ return 1/(1+Math.exp(-x)); }
	
	@Override
	public double[] forward(double[] x) {
		X = x.clone();
		double[] y = new double[X.length];
		for(int i=0; i<X.length; i++) y[i]=sigm(X[i]);
		return y;
	}

	@Override
	public double[] backward(double[] dJdy) {
		double[] dJdx = new double[X.length];
		double[] dydx = new double[X.length];
		for(int i=0; i<X.length; i++) dydx[i]=sigm(X[i])*(1-sigm(X[i]));
		for(int i=0; i<X.length; i++) dJdx[i]=dJdy[i]*dydx[i];
		return dJdx;
	}
	
}