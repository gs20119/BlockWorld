package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	
	public static void main(String[] args) { launch(args); }
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Parent root = FXMLLoader.load(getClass().getResource("model/Root.fxml"));
		Scene scene = new Scene(root); // You can change the Resource directory 
		
		primaryStage.setTitle("Event");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	} 
}
