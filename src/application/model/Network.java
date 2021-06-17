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
		J = new QRLoss();
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