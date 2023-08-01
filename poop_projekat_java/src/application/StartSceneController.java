package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class StartSceneController {
	@FXML
	private Button newTableStartBtn;

	@FXML
	private Button openTableStartBtn;

	private static Stage stage = null;

	public void setStage(Stage s) {
		stage = s;
	}

	@FXML
	void createNewTableFromStartMenu(ActionEvent event) {
		Main.table = new Table(50);
		GUI.rebuildGrid();
		Main.table.updateLabels();
		stage.setScene(GUI.runningScene);
		stage.setTitle("Excel by JANKO - " + Parser.currentFile.getAbsolutePath());
		//stage.setMaximized(true);
//		stage.hide();
//		stage.show();
	}

	@FXML
	void openTableFromStartMenu(ActionEvent event) {
		Main.table = Controller.openTable();
		GUI.rebuildGrid();
		Main.table.updateLabels();
		stage.setScene(GUI.runningScene);
		stage.setTitle("Excel by JANKO - " + Parser.currentFile.getAbsolutePath());
		stage.setMaximized(true);
//		stage.hide();
//		stage.show();
	}
}
