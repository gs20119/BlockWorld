package application.view;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import application.model.QRDQN;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML; // @FXML »ç¿ë
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;

public class Root implements Initializable{ 

	@FXML BorderPane root;
	@FXML Button startButton;
	QRDQN Model; GameBoard gb;
	Random rand = new Random();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		int [] Board= new int[4*4];
		for(int i=0; i<4*4; i++) Board[i]=Math.abs(rand.nextInt())%10;
		gb=new GameBoard(4,root);
		gb.setState(Board); gb.showGameBoard();
		Model = new QRDQN(4, 15, root, gb);
	}
	
	public void start() { Model.start(5000); }
	
}
