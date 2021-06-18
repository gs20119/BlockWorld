package application.model;

public abstract class Network {
	
	Layer[] layers;
	Activation[] activs; Loss J;
	int depth, batch_size;
	
	public double[] forward(double[] x) {
		for(int i=0; i<depth; i++) { 
			x = layers[i].forward(x);
			x = activs[i].forward(x);
		}x = layers[depth].forward(x);
		return x;
	}
	
	public void copy(Network NN) {
		for(int i=0; i<=NN.depth; i++)
			layers[i].copy(NN.layers[i]);
	}
	
	public double getLoss(double[] pred, double[] targ) { return J.getLoss(pred, targ); }
	
	public void backward(double[] pred, double[] targ) {
		J.getLoss(pred, targ);
		double[] dJdx = J.backward();
		dJdx = layers[depth].backward(dJdx);
		for(int i=depth-1; i>=0; i--) {
			dJdx = activs[i].backward(dJdx);
			dJdx = layers[i].backward(dJdx);
		}
	}
	
	public void optimize() { 
		for(int i=0; i<=depth; i++) 
			layers[i].optimize(batch_size); 
	}
	
}

class QRNet extends Network {
	
	int num_support, state_size, action_size;
	
	public QRNet(int s, int a, int sup, int b) {
		this.num_support = sup;
		this.state_size = s;
		this.action_size = a;
		this.batch_size = b; depth = 3;
		layers = new Layer[4]; activs = new Activation[3];
		layers[0] = new FullConnect(state_size,64);
		layers[1] = new FullConnect(64,64);
		layers[2] = new FullConnect(64,64);
		layers[3] = new FullConnect(64,action_size*num_support);
		for(int i=0; i<3; i++) activs[i] = new ReLU();
		J = new QuantHuberLoss(num_support);
	}
	
	public double backward(double[] pred, double[] targ, int action) { 
		J.getLoss(pred, targ); // pred, targ = Z(S,A)
		double[] dJdx_a = J.backward();
		double[] dJdx = new double[1+action_size*num_support];
		for(int i=1; i<=num_support; i++) dJdx[i+num_support*action]=dJdx_a[i];
		dJdx = layers[depth].backward(dJdx);
		for(int i=depth-1; i>=0; i--) {
			dJdx = activs[i].backward(dJdx);
			dJdx = layers[i].backward(dJdx);
		}return J.getLoss(pred, targ);
	}
	
	
}

class SimpleNet extends Network {

	int input_size;
	
	public SimpleNet(int input, int b) {
		this.input_size = input;
		this.batch_size = b; depth = 3;
		layers = new Layer[4]; activs = new Activation[3];
		layers[0] = new FullConnect(input_size,64);
		layers[1] = new FullConnect(64,64);
		layers[2] = new FullConnect(64,64);
		layers[3] = new FullConnect(64,1);
		for(int i=0; i<3; i++) activs[i] = new ReLU();
		J = new MSELoss();
	}
	
}