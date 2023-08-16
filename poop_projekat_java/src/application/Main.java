package application;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;



/*
 * Uputstva za podesavanje okruzenja:
 * 
 * 1. Mora postojati folder savedTables u projektu (van src, u root folderu projekta)
 * 2. U Build Path napravi user biblioteke za JavaFX libove i za Jackson libove
 * 3. Dodaj user biblioteke u build path
 * 4. JRE system library -> Native library location ukazuje na dll biblioteku
 * 5. Run -> Run configurations - za Main klasu programa dodaj VM arguments
 * --module-path "path\to\javafx\lib\folder" --add-modules javafx.controls,javafx.fxml
 * 6. Napravi application paket u src folderu
 */

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
			e.printStackTrace();
		}
		addAskToSaveOnExit(stage);

		stage.setScene(scene);
		stage.show();
	}

	private static void addAskToSaveOnExit(Stage stage) {
		stage.setOnCloseRequest(event -> {
			if (stage.getScene() != GUI.runningScene) {
				return;
			}

			event.consume();
			
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

			alert.showAndWait().ifPresent(response -> {
				if (response == saveButton) {
					Controller.saveTable(Main.table, false);
					System.out.println("Table saved!");
					stage.close();
				} else if (response == discardButton) {
					stage.close();
				} else {
					// response == Cancel, program nastavlja sa radom
				}
			});

		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}