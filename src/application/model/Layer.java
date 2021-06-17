package application.model;

public abstract class Layer extends Function { 
	public Adam adam = new Adam();
	public double He() { 
		double dValue = Math.random();
		double r = 0.3; // sqrt(12/(in+out))
		return dValue*2*r - r;
	}public abstract void optimize(int K); 
}

class Weight {
	public double w;
	public double grad;
	public double m, v; // for Adam optim
	public Weight(double w) { m=0; v=0; grad=0; this.w=w; }
}

class Adam {
	public static double b1=0.9, b2=0.999;
	public static double alpha=Math.pow(10,-4), eps=Math.pow(10,-8);
	public double b1n, b2n;
	public void timeplus() { b1n *= b1; b2n *= b2; }
	public Adam() { b1n = 1; b2n = 1; }

	public Weight update(Weight W) {
		W.m = b1*W.m + (1-b1)*W.grad;
		W.v = b2*W.v + (1-b2)*Math.pow(W.grad,2);
		double mDecay = W.m/(1-b1n), vDecay = W.v/(1-b2n);
		W.w -= alpha*mDecay/(Math.sqrt(vDecay)+eps);
		W.grad = 0;
		return W;
	}
}

class FullConnect extends Layer{
	
	private Weight[][] W;
	private int In, Out;
	FullConnect(int insize, int outsize){ 
		this.In = insize; this.Out = outsize;
		W = new Weight[In+1][Out+1];
		for(int i=0; i<=In; i++)
			for(int j=1; j<=Out; j++) W[i][j] = new Weight(He());
		
	}FullConnect(double[][] weights){
		this.In = weights.length-1;
		this.Out = weights[0].length-1;
		W = new Weight[In+1][Out+1];
		for(int i=0; i<=In; i++) 
			for(int j=1; j<=Out; j++) W[i][j] = new Weight(weights[i][j]);
	}
	
	@Override
	public double[] forward(double[] x) {
		X = x.clone();
		double[] y = new double[Out+1]; y[0]=1; // bias
		for(int i=0; i<=In; i++) 
			for(int j=1; j<=Out; j++) y[j] += X[i]*W[i][j].w;
		return y;
	}

	@Override
	public double[] backward(double[] dJdy) {
		double[] dJdx = new double[In+1];
		for(int i=0; i<=In; i++)
			for(int j=1; j<=Out; j++) {
				dJdx[i] += dJdy[j]*W[i][j].w;
				W[i][j].grad += dJdy[j]*X[i];
		}return dJdx;
	}

	@Override
	public void optimize(int K) {
		adam.timeplus();
		for(int i=0; i<=In; i++)
			for(int j=1; j<=Out; j++) {
				W[i][j].grad /= K; 
				W[i][j] = adam.update(W[i][j]);
		}
	}
	
}

class BatchNormal extends Layer{
	
	private double beta, gamma;
	@Override
	public double[] forward(double[] x) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] backward(double[] dJdy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void optimize(int K) {
		// TODO Auto-generated method stub
		
	}
	
}