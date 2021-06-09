package application.model;

import java.math.*;

public class Network {
	
	private static int depth = 4;
	private static int[] size = {160, 200, 100, 4};
	
	private double[][] layers = new double[depth][];
	private double[][][] weights = new double[depth-1][][]; // weights[i] = i -> i+1
	private double[][][] delta = new double[depth-1][][];
	
	public Network() {
		for(int i=0; i<depth; i++) layers[i] = new double[size[i]+1];
		for(int i=0; i<depth-1; i++) weights[i] = new double[size[i]+1][size[i+1]]; 
		for(int i=0; i<depth-1; i++) delta[i] = new double[size[i]+1][size[i+1]];
	}
	
	public double relu(double x) { return Math.max(x,0); }
	public double drelu(double x) { return (x>0)?1:0; } // derivative of relu
	
	void forward(int[] input) {
		for(int i=0; i<depth; i++) layers[i][0]=1; // bias
		for(int j=1; j<=size[0]; j++) layers[0][j]=input[j]; // 1st layer
		for(int i=0; i<depth-1; i++) { // for all layers
			for(int j=1; j<=size[i+1]; j++) { // for each perceptron
				layers[i+1][j] = 0;
				for(int k=0; k<=size[i]; k++) // calculate
					layers[i+1][j] += weights[i][k][j]*layers[i][k];
				layers[i+1][j] = relu(layers[i+1][j]); // then activate
			}
		}
	}
	
	//void backward(int[] output) {
	//	for(int j=1; j<=size)
	//}
}
