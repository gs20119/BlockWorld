package application.model;

public abstract class Loss{
	double[] Pred, Targ;
	public abstract double getLoss(double[] pred, double[] targ);
	public abstract double[] backward();
}

class MSELoss extends Loss{

	@Override
	public double getLoss(double[] pred, double[] targ) {
		Pred = pred.clone(); Targ = targ.clone();
		double Loss = 0;
		for(int i=1; i<Pred.length; i++) Loss += 0.5*Math.pow(Pred[i]-Targ[i], 2);
		return Loss;
	}

	@Override
	public double[] backward() {
		double[] dJdP = new double[Pred.length];
		for(int i=1; i<Pred.length; i++) dJdP[i] = Pred[i]-Targ[i];
		return dJdP;
	}
	
}

class QuantHuberLoss extends Loss{

	int N; double[][] ErrorMatrix;
	double[] Quantiles;
	public QuantHuberLoss(int num_supports) { 
		N = num_supports;
		ErrorMatrix = new double[N+1][N+1];
		Quantiles = new double[N+1];
		for(int i=1; i<=N; i++) Quantiles[i]=(i/N)-1/(2*N);
	}
	
	private double HuberLoss(double x) {
		if(Math.abs(x)>1) return Math.abs(x)-0.5;
		else return 0.5*Math.pow(x,2);
	}
	
	public double getLoss(double[] pred, double[] targ) { // Error of Z(s,a) = N Supports
		Pred = pred.clone(); Targ = targ.clone();
		double Loss = 0;
		for(int i=1; i<=N; i++)
			for(int j=1; j<=N; j++) {
				ErrorMatrix[i][j] = Targ[j]-Pred[i];
				if(ErrorMatrix[i][j]>0) Loss += HuberLoss(ErrorMatrix[i][j])*Quantiles[i];
				else Loss += HuberLoss(ErrorMatrix[i][j])*(1-Quantiles[i]);
		}return Loss/N;
	}

	@Override
	public double[] backward() {
		double[] dJdP = new double[N+1];
		for(int i=1; i<=N; i++)
			for(int j=1; j<=N; j++) {
				double E = ErrorMatrix[i][j];
				if(Math.abs(E)>1) dJdP[i] += ((E>0)?-Quantiles[i]:1-Quantiles[i]);
				else dJdP[i] += -E*((E>0)?Quantiles[i]:(1-Quantiles[i]));
		}for(int i=1; i<=N; i++) dJdP[i] /= N;
		return dJdP;
	}
	
}