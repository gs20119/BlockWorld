package application.model;

public class BatchNetTest {

	public static void main(String[] args) {
		int batch_size = 500, optAttempt=20000;
		Network NN = new SimpleNet(1,batch_size);
		double[][][] DataSet = new double[300][500][2];
		double[][][] Target = new double[300][500][2];
		
		for(int k=0; k<300; k++)
			for(int i=0; i<500; i++) {
				DataSet[k][i][0] = 1;
				DataSet[k][i][1] = Math.random()*9-2;
				Target[k][i][0] = 1;
				Target[k][i][1] = 0.2*Math.pow(DataSet[k][i][1],3) - Math.pow(DataSet[k][i][1],2) - 2*DataSet[k][i][1] + 1;
		}
		
		
		for(int K=0; K<50; K++) { // 50 epochs
			for(int k=0; k<300; k++) { // 300 mini-batches
				double[][] pred = NN.forward(DataSet[k]);
				double MSELoss = 0;
				for(int j=0; j<500; j++) MSELoss += NN.getLoss(pred[j], Target[k][j]);
				System.out.println(K+" "+k+" "+"Train MSE Loss : " + MSELoss/500);
				NN.backward(pred, Target[k]);
				NN.optimize();
			}
		}
			
		
		double[][] TestX = new double[200][2];
		double[][] TestY = new double[200][2];
		
		for(int i=0; i<200; i++) {
			TestX[i][0] = 1; TestX[i][1] = Math.random()*9-2;
			TestY[i][0] = 1; TestY[i][1] = 0.2*Math.pow(TestX[i][1],3) - Math.pow(TestX[i][1],2) - 2*TestX[i][1] + 1;
		}
		
		for(int i=0; i<200; i++) {
			double[] pred = NN.forward(TestX[i]);
			System.out.println("("+TestX[i][1]+","+pred[1]+")");
		}
		
	}

}
