package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

	static Table table = new Table();

	public static Table getTable() {
		return table;
	}

	public static void setTable(Table table) {
		Main.table = table;
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("Excel by JANKO");
		GUI.stage = stage;
		// Load startMenu
		FXMLLoader loader = new FXMLLoader(getClass().getResource("startScene.fxml"));
		Scene scene = null;
		try {
			Pane startRoot = loader.load();
			scene = new Scene(startRoot);
			StartSceneController ctrl = loader.getController();
			ctrl.setStage(stage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addAskToSaveOnExit(stage);

		stage.setScene(scene);
		//stage.setMaximized(true);
		stage.show();
	}

	private static void addAskToSaveOnExit(Stage stage) {
		stage.setOnCloseRequest(event -> {
			if (stage.getScene() != GUI.runningScene) {
				return;
			}

			event.consume();// Consume the event to prevent the application from closing immediately
			// Show the custom Alert dialog
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.getDialogPane().getScene().getWindow().setOnCloseRequest(e -> {
				alert.close();
			});
			alert.setTitle("Confirmation");
			alert.setHeaderText("Do you want to save the table before exiting?");
			alert.setContentText("Choose your option.");

			ButtonType saveButton = new ButtonType("Save");
			ButtonType discardButton = new ButtonType("Discard");
			ButtonType cancelButton = new ButtonType("Cancel");

			alert.getButtonTypes().setAll(saveButton, discardButton, cancelButton);

			// Show the dialog and wait for the user's response
			alert.showAndWait().ifPresent(response -> {
				if (response == saveButton) {
					// Save the table here (call a method to handle the save operation)
					// For example: saveTable();
					Controller.saveTable(Main.table, false);
					System.out.println("Table saved!");
					stage.close(); // Close the application after saving
				} else if (response == discardButton) {
					// No need to save, just exit the application
					stage.close();
				} else {
					// User clicked Cancel, do nothing (let the application continue running)
				}
			});

		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}