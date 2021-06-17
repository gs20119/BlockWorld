package application.model;

import java.util.Deque;

class Experience{ // S, A, R, nS
	double[] S, nS;
	int A; double R; int d;
	public Experience(double[] S, int A, double R, double[] nS, int d) {
		this.S = S; this.A = A; this.R = R; this.nS = nS; this.d = d;
	}
}

public class QRDQN {
	
	Env env;
	Network NN, Target;
	int size, actions=4, maxBlock=15;
	int supports=8, batch_size=200;
	double alp, bet, gam;
	Deque<Experience> Replay;
	int capacity = 1000;
	
	public QRDQN(int size) { 
		this.size = size;
		env = new Env(size, maxBlock);
		NN = new QRNet(size*size*maxBlock, actions, supports, batch_size); 
	}
	
	public void start(int epochs) {
		for(int i=0; i<epochs; i++) {
			if(i%100==0) System.out.println(i);
			env.Init(); epoch();
		}
	}
	
	private void epoch() {
		while(true) {
			
			double[] pState = env.getHotVec();
			double[] zPred = NN.forward(pState);
			double maxQ=0; int action=0;
			for(int i=0; i<actions; i++) {
				double qPred=0;
				for(int j=1; j<=supports; j++) qPred += zPred[i*supports+j];
				qPred /= supports; // expected Q(pState,i)
				if(qPred > maxQ) { action = i; maxQ = qPred; }
			}
			
			env.Move(action,0);
			double[] nState = env.getHotVec();
			double reward = env.getReward();
			int dead = env.Terminal();
			Experience e = new Experience(pState, action, reward, nState, dead);
			Replay.addLast(e);
			
			if(Replay.size() > capacity) Replay.pop();			
			if(Replay.size() > batch_size)  train(); 
			if(dead==1) break;
			
		}
	}
	
	private void train() { 
		
	}
	
}
