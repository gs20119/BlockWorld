package application.model;

import java.util.ArrayList;
import java.util.Random;
import java.util.Random;

public class Env {
	private int[] Board, Mv;
	private int[][] mvIdx;
	private int size=4, cap=16, Max=10;
	private int time=0, score=0, Advntge=0;
	private static int genMax=3;
	private static double pa, pb, pc;
	private static String[] Actions = {"LEFT", "UP", "RIGHT", "DOWN"};
	Random rand = new Random();
	
	public Env(int size, int maxBlock) { 
		this.size=size; 
		this.cap=size*size;
		this.Max=maxBlock;
		Board = new int[this.cap];
		Mv = new int[this.cap];
		mvIdx = new int[this.size][];
		for(int i=0; i<this.size; i++) mvIdx[i] = new int[this.cap];
		for(int j=0; j<this.cap; j++) mvIdx[0][j] = j; // left
		for(int j=0; j<this.cap; j++) mvIdx[1][j] = size*(j%size) + ((int)j/size); // up
		for(int j=0; j<this.cap; j++) mvIdx[2][j] = j + (size-1) - 2*(j%size); // right
		for(int j=0; j<this.cap; j++) mvIdx[3][j] = size*(size-1-j%size) + ((int)j/size); // down
	}
	
	public void Init() {
		time=0; score=0; 
		System.out.println("-Game Starts-");
		Generate(); Show();
	}
	
	public void Show() {
		System.out.println("---- " + time + " ----");
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) System.out.print(Board[4*i+j]+" ");
			System.out.println("");
		}
	}
	
	public void Reset() {
		System.out.println("Game Over : " + score);
		Mv = new int[cap]; Board = new int[cap];
		/* JavaFX UI Work Here */
	}
	
	private void ShowUI() { 
		/* Get Board and Mv, Show this to UI */
	}
	
	private void Generate() { 
		ArrayList<Integer> Line = new ArrayList<>();
		for(int i=0; i<cap; i++) if(Board[i]==0) Line.add(i);
		int Randi = Math.abs(rand.nextInt())%Line.size();
		int Randv = Math.abs(rand.nextInt())%(genMax-1)+1; // 1¿¡¼­ genMax
		Board[Line.get(Randi)] = Randv;
	}
	
	public void Move(int act, int show) { 
		Mv = new int[cap]; Advntge=0;
		for(int j=0; j<size; j++) {
			int top=0, topv=0; 
			for(int i=0; i<size; i++) {
				int here = mvIdx[act][i+j*size];
				if(Board[here]==0) continue;
				if(topv==Board[here]) { 
					Mv[here]=i-top; score+=topv; Advntge+=topv; if(topv<Max) topv++; 
					Board[mvIdx[act][top+j*size]]=topv; 
					top++; topv=0; Board[here]=0;
				}else { 
					if(topv!=0) { Board[mvIdx[act][top+j*size]]=topv; top++; }
					Mv[here]=i-top; topv=Board[here]; Board[here]=0;
				}
			}Board[mvIdx[act][top+j*size]]=topv;
			
		}System.out.println("Selected : " + Actions[act]);
		Generate(); time++;
		if(show!=0) { Show(); ShowUI(); } 
	}
	
	public int Terminal() {
		for(int i=0; i<cap; i++) if(Board[i]==0) return 0;
		for(int i=0; i<cap-size; i++) if(Board[i]==Board[i+4]) return 0;
		for(int i=0; i<cap-1; i++) if(Board[i]==Board[i+1] && (i+1)%size!=0) return 0;
		return 1;
	}
	
	public int[] getState() { return Board; }
	
	public double[] getHotVec() {
		double[] oneHot = new double[1+cap*Max];
		for(int i=0; i<cap; i++)
			for(int j=1; j<=Max; j++) if(Board[i]==j) oneHot[Max*i+j]=1;
		return oneHot;
	}
	
	public double getReward() {
		double mvPenalty=0, remainBlank=0;
		for(int i=0; i<cap; i++) if(Board[i]==0) remainBlank++;
		for(int i=0; i<cap; i++) mvPenalty += Mv[i];
		return Advntge*pa + remainBlank*pb + mvPenalty*pc;
	}
	
}