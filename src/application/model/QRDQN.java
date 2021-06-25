package application.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import application.view.GameBoard;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.BorderPane;

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
	double gam=0.99, eps=0.01; // added action noise
	GameBoard gb;
	Random rand = new Random();
	ArrayList<Experience> Replay = new ArrayList<Experience>();
	int capacity = 5000;
	
	public QRDQN(int size, int maxBlock, BorderPane root, GameBoard gb) { 
		this.size = size; this.maxBlock = maxBlock;
		env = new Env(size, maxBlock); this.gb = gb;
		NN = new QRNet(size*size*maxBlock, actions, supports, batch_size); 
		Target = new QRNet(size*size*maxBlock, actions, supports, batch_size);
	}
	
	private int findBestAction(double[] zPred) {
		double maxQ=-10000000; int action=-1; 
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
	
	public void start(int epochs) {
		EpochThread t = new EpochThread(epochs);
		t.setDaemon(true); t.start();
	}
	
	public class EpochThread extends Thread{ 	
		
		int epochs;
		public EpochThread(int epochs) {
			this.epochs = epochs;
		}
		
		@Override
		public void run() {
			for(int i=0; i<epochs; i++) {
				Target.copy(NN);
				env.Reset(); env.Init();
				while(true) {
					int dead = step((i>100)?1:0);
					int[] Board = env.getState();
					Platform.runLater(() -> {
						gb.setState(Board);
						gb.showGameBoard();
					});
					try { Thread.sleep(1); }
					catch(Exception e) { }
					if(dead==1) break;
				}
			}
		}
	};

	
	int step(int print) {
		
		double[] pState = env.getHotVec();
		double[] zPred = NN.forward(pState);
		int action = findBestAction(zPred); 
		if(Math.random()<eps) action = Math.abs(rand.nextInt())%actions; // exploration
		if(print>=1) printQ(zPred); print=0;
		
		env.Move(action,print);
		if(env.notMoved()!=0) {
			action = Math.abs(rand.nextInt())%actions;
			env.Move(action,print); } // Added : Blocking Invalid Moves Efficiently
		
		double[] nState = env.getHotVec();
		double reward = env.getReward(); // System.out.println(reward);
		int dead = env.Terminal();
		Experience e = new Experience(pState, action, reward, nState, dead);
		Replay.add(e);
		
		if(Replay.size() > capacity) Replay.remove(0);			
		if(Replay.size() > batch_size) train(); 
		if(dead==1) return 1; return 0;
		
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
			}//NN.forward(e.S); // we have to train Z(S,A)
			meanLoss += NN.backward(zPredi, zTargi, e.A);
		}NN.optimize(); 
		if(env.Terminal()!=0) System.out.println("Average Loss : "+meanLoss/batch_size);
	}
	
}