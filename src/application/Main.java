package application;
	
import application.model.Env;
import application.model.QRDQN;
import application.view.GameBoard;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			BorderPane root = new BorderPane(); // (BorderPane)FXMLLoader.load(getClass().getResource("view/Root.fxml"));
			
			int [] Board= {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,0 };
			GameBoard gb=new GameBoard(4);
			
			gb.setState(Board);
			gb.showGameBoard(root); // Print Game Board
			
			Scene scene = new Scene(root);
			
			primaryStage.setTitle("Event");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			QRDQN Model = new QRDQN(4,15,root,gb);
			Model.start(2);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
