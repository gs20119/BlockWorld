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
	@FXML Button stopButton;
	@FXML Button stepButton;
	QRDQN Model; GameBoard gb;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		int [] Board= {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,0 };
		gb=new GameBoard(4,root);
		gb.setState(Board); gb.showGameBoard();
		Model = new QRDQN(4, 15, root, gb);
	}
	
	public void start() {  disableButtons(true,false,true); Model.start(20); }
	public void stop() { disableButtons(false,true,false); }
	public void singleEpoch() { }
	
	public void disableButtons(boolean start, boolean stop, boolean step) {
		startButton.setDisable(start);
		stopButton.setDisable(stop);
		stepButton.setDisable(step);
	}
	
	
}
