package poop_projekat_java;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Excel by JANKO");
		BorderPane root = new BorderPane();
		
		ToolBar toolbar = new ToolBar();
		Button saveButton = new Button("Save");
		Button formatText = new Button("Format to text");
		Button formatNumber = new Button("Format to number");
		Button formatDate = new Button("Format to date");
		
		toolbar.getItems().addAll(saveButton, formatText, formatNumber, formatDate);
		
		root.setTop(toolbar);
		Table table = new Table(root);
		// Retrieve screen dimensions
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();
        
		Scene scene = new Scene(root, screenWidth-100, screenHeight-100);
		
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

}
