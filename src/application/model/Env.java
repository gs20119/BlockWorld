package application.model;
import java.util.ArrayList;
import java.util.Random;

public class Env {
	private int Board[];
	private int size=4, cap=16;
	private int time=0;
	private static int max=10, genMax=3;
	Random rand = new Random();
	
	public Env(int size) { 
		this.size=size; 
		this.cap=size*size;
		Board = new int[this.cap];
		Generate();
	}
	
	public void Show() {
		System.out.println("---------- " + time + " ----------");
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) System.out.print(Board[4*i+j]+" ");
			System.out.println("");
		}System.out.println("--------------------");
	}
	
	private void Generate() { 
		
	}
	
	public void Move() {
		
	}
	
	public void getState() {
		
	}
	
}
