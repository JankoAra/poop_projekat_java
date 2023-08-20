package application;

import application.GUI.UpdateType;
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

	public void setStage(Stage s) { stage = s; }

	final static boolean maximized = true;

	private enum StartType { NEW_TABLE, OPEN_TABLE }

	private void changeScene(StartType type) {
		if (type == StartType.NEW_TABLE) {
			Main.table = new Table(Table.DEFAULT_TABLE_SIZE);
			UndoRedoStack.clearUndoRedoStack();
		}
		else if (type == StartType.OPEN_TABLE) {
			Main.table = Controller.openTable();
			UndoRedoStack.clearUndoRedoStack();
		}
		else return;
		GUI.updateGUI(UpdateType.TABLE_CHANGE);
		stage.setScene(GUI.runningScene);
		stage.setTitle("Excel by JANKO - " + Parser.currentFile.getAbsolutePath());
		stage.setMaximized(maximized);
		//		stage.hide();
		//		stage.show();
	}

	@FXML
	void createNewTableFromStartMenu(ActionEvent event) {
		changeScene(StartType.NEW_TABLE);
	}

	@FXML
	void openTableFromStartMenu(ActionEvent event) {
		changeScene(StartType.OPEN_TABLE);
	}
}
