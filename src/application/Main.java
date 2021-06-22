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
	public static void main(String[] args) { launch(args); }
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("view/Root.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setTitle("Event");
		primaryStage.setScene(scene);
		primaryStage.show();		
	}
}
