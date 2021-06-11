package application.model;

public class Network {
	
	private static int depth = 4;
	private static int[] size = {0, 160, 200, 100, 4};
	private static double b1 = 0.9, b2 = 0.999;
	private static double alpha = Math.pow(10,-3), eps = Math.pow(10,-8);
	private double b1n, b2n;
	
	private double[][] layers = new double[depth+1][];
	private double[][][] weights = new double[depth][][]; // weights[i] = layer i -> i+1
	private double[][] delta = new double[depth+1][]; 
	private double[][][] grads = new double[depth][][];
	private double[][][] m = new double[depth][][];
	private double[][][] v = new double[depth][][];
	
	public double He() {
		double dValue = Math.random();
		double r = Math.sqrt(12/(size[1]+size[depth]));
		return dValue*2*r - r;
	}
	
	public Network() { // Network Initialization
		b1n = 1.0; b2n = 1.0;
		for(int i=1; i<=depth; i++) layers[i] = new double[size[i]+1];
		for(int i=1; i<depth; i++) weights[i] = new double[size[i]+1][size[i+1]+1]; 
		for(int i=1; i<=depth; i++) delta[i] = new double[size[i]+1];
		for(int i=1; i<depth; i++) grads[i] = new double[size[i]+1][size[i+1]+1]; 
		for(int i=1; i<depth; i++) m[i] = new double[size[i]+1][size[i+1]+1];
		for(int i=1; i<depth; i++) v[i] = new double[size[i]+1][size[i+1]+1];
		for(int i=1; i<depth; i++) 
			for(int j=0; j<=size[i]; j++) 
				for(int k=1; k<=size[i+1]; i++) weights[i][j][k] = He();
	}
	
	private double relu(double x) { return Math.max(x,0); }
	private double drelu(double x) { return (x>0)?1:0; } // derivative of relu ( a:z )
	private double MSE(double[] y) {
		double loss = 0.0;
		for(int j=1; j<=size[depth]; j++) loss += Math.pow(layers[depth][j]-y[j-1], 2);
		return loss / size[depth]; // MSE = mean of (out-y)^2
	}private double[] dMSE(double[] y) { // derivative of MSE = out-y
		double[] dloss = new double[size[depth]+1]; 
		for(int j=1; j<=size[depth]; j++) dloss[j] = layers[depth][j]-y[j-1];
		return dloss;
	}
	
	public double forward(double[] x, double[] y) { // only forward ( not train )
		for(int i=1; i<=depth; i++) layers[i][0]=1; // bias
		for(int j=1; j<=size[1]; j++) layers[1][j]=x[j-1]; // insert x
		for(int i=1; i<depth; i++) { // for all layers
			for(int j=1; j<=size[i+1]; j++) { // for each perceptron
				layers[i+1][j] = 0;
				for(int k=0; k<=size[i]; k++) // calculate ( z[i+1][j] )
					layers[i+1][j] += weights[i][k][j]*layers[i][k];
				layers[i+1][j] = relu(layers[i+1][j]); // then activate ( a[i+1][j] )
			}
		}return MSE(y);
	}
	
	public void Adam() { 
		for(int i=1; i<depth; i++) for(int j=0; j<size[i]; j++) for(int k=1; k<size[i+1]; k++) {
			m[i][j][k] = b1*m[i][j][k] + (1-b1)*grads[i][j][k];
			v[i][j][k] = b2*v[i][j][k] + (1-b2)*grads[i][j][k]*grads[i][j][k];
			b1n *= b1; b2n *= b2;
			double mDecay = m[i][j][k]/(1.0-b1n), vDecay = v[i][j][k]/(1.0-b2n);
			weights[i][j][k] -= alpha*mDecay/(Math.sqrt(vDecay)+eps);
		}
	}
	
	public void backward(double[] x, double[] y) { // forward + train ( backprop )
		forward(x, y); double[] dloss = dMSE(y);
		for(int j=1; j<=size[depth]; j++) delta[depth][j] = dloss[j];
		for(int i=depth-1; i>1; i--) { // delta = J:z[i][j] 
			for(int j=1; j<=size[i]; j++) {
				delta[i][j] = 0;
				for(int k=1; k<=size[i+1]; k++)
					delta[i][j] += delta[i+1][k]*weights[i][j][k]*drelu(layers[i][j]);
					// J:z[i][j] = sum ( J:z[i+1][k] )*( z[i+1][k]:a[i][j] )*( a[i][j]:z[i][j] )
			}
		}for(int i=1; i<depth; i++) // grads = J:w[i][j][k]
			for(int j=0; j<size[i]; j++)
				for(int k=1; k<size[i+1]; k++) grads[i][j][k] = layers[i][j]*delta[i+1][k];
		Adam(); // Optimize parameters
	}
}
