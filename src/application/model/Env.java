package application.model;

import java.util.ArrayList;
import java.util.Random;
import java.util.Random;

public class Env {
	private int[] Board;
	private int[][] mvIdx = new int[4][];
	private int size=4, cap=16;
	private int time=0;
	private static int Max=10, genMax=3;
	Random rand = new Random();
	
	public Env(int size) { 
		this.size=size; 
		this.cap=size*size;
		Board = new int[this.cap];
		for(int i=0; i<4; i++) mvIdx[i] = new int[this.cap];
		for(int j=0; j<this.cap; j++) mvIdx[0][j] = j; // left
		for(int j=0; j<this.cap; j++) mvIdx[1][j] = size*(j%size) + ((int)j/size); // up
		for(int j=0; j<this.cap; j++) mvIdx[2][j] = j + (size-1) - 2*(j%size); // right
		for(int j=0; j<this.cap; j++) mvIdx[3][j] = size*(size-1-j%size) + ((int)j/size); // down
		for(int j=0; j<this.cap; j++) System.out.print(mvIdx[3]);
		System.out.println("");
		Generate();
	}
	
	public void Show() {
		System.out.println("---- " + time + " ----");
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) System.out.print(Board[4*i+j]+" ");
			System.out.println("");
		}System.out.println("-----------");
	}
	
	private void Generate() { 
		ArrayList<Integer> Line = new ArrayList<>();
		for(int i=0; i<cap; i++) if(Board[i]==0) Line.add(i);
		int Randi = Math.abs(rand.nextInt())%Line.size();
		int Randv = Math.abs(rand.nextInt())%(genMax-1)+1;
		Board[Randi] = Randv;
	}
	
	private int[] Eval(int[] Cur) { // 0 방향으로 밀기
		int[] Mv = new int[size];
		int top=-1, topv=0;
		for(int i=0; i<size; i++) {
			if(Cur[i]==0) continue;
			if(topv==Cur[i]) { Mv[i]=i-top; top++; topv=0; }
			else { top++; Mv[i]=i-top; topv=Cur[i]; }
		}return Mv;
	}
	
	public void Move(int action) {
		
	}
	
	public int[] getState() { return Board; }
	
}