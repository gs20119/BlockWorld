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

class QRLoss extends Loss{

	@Override
	public double getLoss(double[] pred, double[] targ) {
		Pred = pred.clone(); Targ = targ.clone();
		return 0;
	}

	@Override
	public double[] backward() {
		// TODO Auto-generated method stub
		return null;
	}
	
}