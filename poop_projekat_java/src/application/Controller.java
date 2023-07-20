package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileNameExtensionFilter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;

public class Controller {
	public static File getFilePath(boolean saving) {
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File("savedTables"));
		FileChooser.ExtensionFilter allExtensionFilter = new FileChooser.ExtensionFilter("All files", "*.*");
		FileChooser.ExtensionFilter csvExtensionFilter = new FileChooser.ExtensionFilter("CSV files", "*.csv");
		FileChooser.ExtensionFilter jsonExtensionFilter = new FileChooser.ExtensionFilter("JSON files", "*.json");
		fc.setSelectedExtensionFilter(allExtensionFilter);
		fc.getExtensionFilters().addAll(csvExtensionFilter, jsonExtensionFilter, allExtensionFilter);
		File file;
		if (saving) {
			// save file
			file = fc.showSaveDialog(GUI.scene.getWindow());
		} else {
			// open file
			file = fc.showOpenDialog(GUI.scene.getWindow());
		}
		return file;
	}

	public static void saveTable(Table table, boolean saveAs) {
		try {
			File path;
			if (Parser.currentFile.getName().equals("Untitled") || saveAs == true) {
				path = Controller.getFilePath(true);
			} else {
				path = Parser.currentFile;
			}
			if (path == null)
				throw new Exception();
			String extension = Parser.getExtensionFromFilePath(path);
			String fileContent = Parser.convertTableToString(table, extension);
			Parser.createNewSaveFile(path, fileContent);
			Parser.currentFile = path;
			GUI.primaryStage.setTitle("Excel by JANKO - " + Parser.currentFile.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("Fatalna greska pri cuvanju tabele");
		}
	}
	
	public static void openTable() {
		File path = getFilePath(false);
		if(path==null) {
			System.out.println("Fajl ne postoji ili je akcija prekinuta");
			return;
		}
		String extension = Parser.getExtensionFromFilePath(path);
		if(extension.equals(".csv")) {
			Main.table = Parser.convertCSVToTable(path);
		}
		else if(extension.equals(".json")) {
			Main.table = Parser.convertJSONToTable(path);
		}
		else {
			System.out.println("Ekstenzija nije podrzana.");
			return;
		}
		Parser.currentFile = path;
		GUI.primaryStage.setTitle("Excel by JANKO - " + Parser.currentFile.getAbsolutePath());
	}
}
