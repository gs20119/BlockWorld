package application.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Queue;
import java.util.Random;

class Experience{ // S, A, R, nS
	double[] S, nS;
	int A; double R; int d; int sd;
	public Experience(double[] S, int A, double R, double[] nS, int d) {
		this.S = S; this.A = A; this.R = R; this.nS = nS; this.d = d;
	}
}

public class QRDQN {
	
	Env env;
	QRNet NN, Target;
	int size, actions=4, maxBlock=2;
	int supports=16, batch_size=500;
	double invalPenalty = 10;
	double gam=0.99, eps=0.05;
	Random rand = new Random();
	ArrayList<Experience> Replay = new ArrayList<Experience>();
	int capacity = 5000;
	
	public QRDQN(int size, int maxBlock) { 
		this.size = size; this.maxBlock = maxBlock;
		env = new Env(size, maxBlock);
		NN = new QRNet(size*size*maxBlock, actions, supports, batch_size); 
		Target = new QRNet(size*size*maxBlock, actions, supports, batch_size);
	}
	
	public void start(int epochs) {
		for(int i=0; i<epochs; i++) {
			Target.copy(NN);
			System.out.println(i);
			env.Reset(); env.Init(); epoch(i);
		}
	}
	
	private int findBestAction(double[] zPred) {
		double maxQ=-1000; int action=-1; 
		for(int i=0; i<actions; i++) {
			double qPred=0;
			for(int j=1; j<=supports; j++) qPred += zPred[i*supports+j];
			qPred /= supports;
			if(qPred > maxQ) { action = i; maxQ = qPred; }
		}return action;
	}
	
	private void printQ(double[] zPred) {
		for(int i=0; i<actions; i++) {
			double qPred=0;
			for(int j=1; j<=supports; j++) qPred += zPred[i*supports+j];
			System.out.print(qPred/supports + " ");
		}System.out.println("");
	}
	
	private void epoch(int time) {
		while(true) {
			
			double[] pState = env.getHotVec();
			double[] zPred = NN.forward(pState);
			int action = findBestAction(zPred);
			if(time>4000) printQ(zPred);
			
			env.Move(action,(time>4000)?1:0);
			if(env.subTerminal()!=0) {
				action = Math.abs(rand.nextInt())%actions;
				env.Move(action,(time>4000)?1:0); } // Added : Blocking Invalid Moves Efficiently
			double[] nState = env.getHotVec();
			double reward = env.getReward();
			int dead = env.Terminal();
			Experience e = new Experience(pState, action, reward, nState, dead);
			Replay.add(e);
			
			if(Replay.size() > capacity) Replay.remove(0);			
			if(Replay.size() > batch_size) train(); 
			if(dead==1) break;
			
		}
	}
	
	private void train() {
		double meanLoss = 0;
		ArrayList<Experience> Sample = (ArrayList<Experience>) Replay.clone();
		Collections.shuffle(Sample);
		for(int i=0; i<batch_size; i++) {
			Experience e = (Experience)Sample.get(i);
			double[] zPred = NN.forward(e.S); // Z(S,a) for all a
			double[] zTarg = Target.forward(e.nS); // Z(nS,a) for all a
			double[] zPredi = new double[supports+1]; // Z(S,A)
			double[] zTargi = new double[supports+1]; // R+gamZ(nS,a*)
			for(int j=1; j<=supports; j++) zTargi[j] = e.R;
			for(int j=1; j<=supports; j++) zPredi[j]=zPred[j+supports*e.A];
			if(e.d==0) {
				int nA = findBestAction(zTarg);
				for(int j=1; j<=supports; j++) zTargi[j] += gam*zTarg[j+supports*nA];
			}NN.forward(e.S); // we have to train Z(S,A)
			meanLoss += NN.backward(zPredi, zTargi, e.A);
		}NN.optimize(); 
		System.out.println("Average Loss : "+meanLoss/batch_size);
	}
	
}
