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
		FileChooser.ExtensionFilter csvExtensionFilter = new FileChooser.ExtensionFilter("CSV files", "*.csv");
		FileChooser.ExtensionFilter jsonExtensionFilter = new FileChooser.ExtensionFilter("JSON files", "*.json");
		FileChooser.ExtensionFilter allExtensionFilter = new FileChooser.ExtensionFilter("All files", "*.*");
		fc.getExtensionFilters().addAll(csvExtensionFilter,jsonExtensionFilter,allExtensionFilter);
		File file;
		if(saving) {
			//save file
			file = fc.showSaveDialog(GUI.scene.getWindow());
		}else {
			//open file
			file = fc.showOpenDialog(GUI.scene.getWindow());
		}
		return file;
	}
}
