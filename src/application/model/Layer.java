package application.model;

public abstract class Layer extends Function { 
	public Adam adam = new Adam();
	public double He() { 
		double dValue = Math.random();
		double r = 0.1; // sqrt(12/(in+out))
		return dValue*2*r - r;
	}public abstract void copy(Layer L);
	public abstract void optimize(int K); 
}

class Weight {
	public double w;
	public double grad;
	public double m, v; // for Adam optim
	public Weight(double w) { m=0; v=0; grad=0; this.w=w; }
}

class Adam {
	public static double b1=0.9, b2=0.999;
	public static double alpha=Math.pow(10,-4), eps=Math.pow(10,-6);
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
	public void copy(Layer L) {
		FullConnect FC = (FullConnect)L;
		for(int i=0; i<=In; i++)
			for(int j=1; j<=Out; j++) W[i][j] = new Weight(FC.W[i][j].w);
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
	@Override
	public double[][] batchforward(double[][] xbatch) {
		Xbatch = new double[xbatch.length][xbatch[0].length];
		double[][] ybatch = new double[xbatch.length][Out+1];
		for(int k=0; k<Xbatch.length; k++) ybatch[k][0]=1;
		for(int i=0; i<Xbatch.length; i++) 
			for(int j=0; j<Xbatch[0].length; j++) Xbatch[i][j] = xbatch[i][j];
		for(int k=0; k<Xbatch.length; k++)
			for(int i=0; i<=In; i++)
				for(int j=1; j<=Out; j++) ybatch[k][j] += Xbatch[k][i]*W[i][j].w;
		return ybatch;
	}
	
	@Override
	public double[][] batchbackward(double[][] dJdy) {
		double[][] dJdx = new double[Xbatch.length][In+1];
		for(int k=0; k<Xbatch.length; k++)
			for(int i=0; i<=In; i++)
				for(int j=1; j<=Out; j++) {
					dJdx[k][i] += dJdy[k][j]*W[i][j].w;
					W[i][j].grad += dJdy[k][j]*Xbatch[k][i];
		}return dJdx;
	}

	
}

class BatchNormal extends Layer{
	
	private Weight beta, gamma;
	private double eps = Math.pow(10,-6);
	public BatchNormal() {
		beta = new Weight(0.1);
		gamma = new Weight(0.1);
	}
	
	public double[] forward(double[] x) { return null; } // not defined
	public double[] backward(double[] dJdy) { return null; }

	@Override
	public void optimize(int K) {
		adam.timeplus();
		beta.grad /= K; gamma.grad /= K;
		beta = adam.update(beta);
		gamma = adam.update(gamma);
	}
	
	@Override
	public void copy(Layer L) {
		BatchNormal B = (BatchNormal)L;
		beta = new Weight(B.beta.w);
		gamma = new Weight(B.gamma.w);
	}

	double[] mean, vari;
	double[][] Xshift, Ybatch;

	@Override
	public double[][] batchforward(double[][] xbatch) {
		
		Xbatch = new double[xbatch.length][xbatch[0].length];
		Xshift = new double[xbatch.length][xbatch[0].length];
		Ybatch = new double[xbatch.length][xbatch[0].length];
		mean = new double[Xbatch[0].length]; 
		vari = new double[Xbatch[0].length];
		
		for(int i=0; i<Xbatch.length; i++)
			for(int j=1; j<Xbatch[0].length; j++) Xbatch[i][j] = xbatch[i][j];
		
		for(int i=0; i<Xbatch.length; i++)
			for(int j=1; j<Xbatch[0].length; j++) Xshift[i][j] = Xbatch[i][j];
		
		for(int i=1; i<Xbatch[0].length; i++)
			for(int j=0; j<Xbatch.length; j++) mean[i] += Xbatch[j][i];
		for(int i=1; i<Xbatch[0].length; i++) mean[i] /= Xbatch.length;
		for(int i=1; i<Xbatch[0].length; i++)
			for(int j=0; j<Xbatch.length; j++) Xshift[j][i] -= mean[i];
		
		for(int i=1; i<Xbatch[0].length; i++)
			for(int j=0; j<Xbatch.length; j++) vari[i] += Math.pow(Xshift[j][i], 2);
		for(int i=1; i<Xbatch[0].length; i++) vari[i] /= Xbatch.length;
		
		for(int i=1; i<Xbatch[0].length; i++)
			for(int j=0; j<Xbatch.length; j++) Xshift[j][i] /= (vari[i]+eps);
		
		for(int i=0; i<Xbatch.length; i++)
			for(int j=1; j<Xbatch[0].length; j++) 
				Ybatch[i][j] = gamma.w*Xshift[i][j]+beta.w; 
		
		for(int i=0; i<Xbatch.length; i++) Ybatch[i][0]=1;
		return Ybatch;
	}

	@Override
	public double[][] batchbackward(double[][] dJdy) {
		
		beta.grad=0; gamma.grad=0;
		double[][] dJdxs = new double[Xbatch.length][Xbatch[0].length];
		double[] dJdm = new double[Xbatch[0].length];
		double[] dJdv = new double[Xbatch[0].length];
		double[][] dJdx = new double[Xbatch.length][Xbatch[0].length];
		
		for(int i=0; i<Xbatch.length; i++)
			for(int j=1; j<Xbatch[0].length; j++) {
				beta.grad += dJdy[i][j];
				gamma.grad += Xshift[i][j]*dJdy[i][j];
				dJdxs[i][j] = gamma.w*dJdy[i][j];
				dJdv[j] += -0.5 * dJdxs[i][j] * (Xbatch[i][j]-mean[j]) 
						* Math.pow(vari[j]+eps,-1.5);
				dJdm[j] += -1 * dJdxs[i][j] / Math.sqrt(vari[j]+eps);
				dJdx[i][j] = dJdxs[i][j] / Math.sqrt(vari[j]+eps)
						+ dJdv[j] * 2 * (Xshift[i][j]-mean[j]) / Xbatch.length
						+ dJdm[j] / Xbatch.length;
		}
		
		return dJdx;
	}
	
}