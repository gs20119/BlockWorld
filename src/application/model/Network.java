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
	
	public double[][] forward(double[][] x){
		for(int i=0; i<depth; i++) {
			x = layers[i].batchforward(x);
			x = activs[i].batchforward(x);
		}x = layers[depth].batchforward(x);
		return x;
	}
	
	public void copy(Network NN) {
		for(int i=0; i<NN.layers.length; i++)
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
	
	public void backward(double[][] pred, double[][] targ) {
		double[][] dJdx = new double[pred.length][];
		for(int k=0; k<pred.length; k++) {
			J.getLoss(pred[k], targ[k]);
			dJdx[k] = J.backward();
		}dJdx = layers[depth].batchbackward(dJdx);
		for(int i=depth-1; i>=0; i--) {
			dJdx = activs[i].batchbackward(dJdx);
			dJdx = layers[i].batchbackward(dJdx);
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
		layers = new Layer[depth+1]; 
		activs = new Activation[depth];
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

class SimpleBatch extends Network {
	
	int input_size;
	
	public SimpleBatch(int input, int b) {
		this.input_size = input;
		this.batch_size = b; depth = 3;
		layers = new Layer[5]; activs = new Activation[3];
		layers[0] = new FullConnect(input_size,64);
		activs[0] = new ReLU();
		layers[1] = new FullConnect(64,64);
		layers[2] = new BatchNormal();
		activs[1] = new ReLU();
		layers[3] = new FullConnect(64,64);
		activs[2] = new ReLU();
		layers[4] = new FullConnect(64,1);
		J = new MSELoss();
	}
	
	@Override
	public double[][] forward(double[][] x){
		x = layers[0].batchforward(x); 
		x = activs[0].batchforward(x);
		x = layers[1].batchforward(x); 
		x = layers[2].batchforward(x);
		x = activs[1].batchforward(x);
		x = layers[3].batchforward(x);
		x = activs[2].batchforward(x);
		x = layers[4].batchforward(x);
		return x;
	}
	
	@Override
	public void backward(double[][] pred, double[][] targ) {
		double[][] dJdx = new double[pred.length][];
		for(int k=0; k<pred.length; k++) {
			J.getLoss(pred[k], targ[k]);
			dJdx[k] = J.backward();
		}dJdx = layers[4].batchbackward(dJdx); 
		dJdx = activs[2].batchbackward(dJdx);
		dJdx = layers[3].batchbackward(dJdx);
		dJdx = activs[1].batchbackward(dJdx);
		dJdx = layers[2].batchbackward(dJdx);
		dJdx = layers[1].batchbackward(dJdx);
		dJdx = activs[0].batchbackward(dJdx);
		dJdx = layers[0].batchbackward(dJdx);
	}
	
	
}



class QRBatch extends Network {
	
	int num_support, state_size, action_size;
	
	public QRBatch(int s, int a, int sup, int b) {
		this.num_support = sup;
		this.state_size = s;
		this.action_size = a;
		this.batch_size = b; depth = 3;
		layers = new Layer[2*depth+1]; 
		activs = new Activation[depth];
		layers[0] = new FullConnect(state_size,64);
		layers[1] = new BatchNormal(); activs[0] = new ReLU();
		layers[2] = new FullConnect(64,64);
		layers[3] = new BatchNormal(); activs[1] = new ReLU();
		layers[4] = new FullConnect(64,64);
		layers[5] = new BatchNormal(); activs[2] = new ReLU();
		layers[6] = new FullConnect(64,action_size*num_support);
		J = new QuantHuberLoss(num_support);
	}
	
	public double[][] forward(double[][] x){
		for(int i=0; i<depth; i++) {
			x = layers[2*i].batchforward(x);
			x = layers[2*i+1].batchforward(x);
			x = activs[i].batchforward(x);
		}x = layers[2*depth].batchforward(x);
		return x;
	}
	
	public double backward(double[][] pred, double[][] targ, int[] actions) { 
		double meanLoss = 0;
		double[][] dJdx = new double[pred.length][1+action_size*num_support];
		for(int k=0; k<pred.length; k++) {
			meanLoss += J.getLoss(pred[k], targ[k]); // pred, targ = Z(S,A)
			double[] dJdx_a = J.backward();
			for(int i=1; i<=num_support; i++) 
				dJdx[k][i+num_support*actions[k]]=dJdx_a[i];
		}
		dJdx = layers[2*depth].batchbackward(dJdx);
		for(int i=depth-1; i>=0; i--) {
			dJdx = activs[i].batchbackward(dJdx);
			dJdx = layers[2*i+1].batchbackward(dJdx);
			dJdx = layers[2*i].batchbackward(dJdx);
		}return meanLoss/pred.length;
	}
	
	
}
