package application;

import java.io.File;

import javafx.stage.FileChooser;

public class Controller {
	public static File getFilePath(boolean saving) {
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File("savedTables"));
		FileChooser.ExtensionFilter allExtensionFilter = new FileChooser.ExtensionFilter("All files", "*.*");
		FileChooser.ExtensionFilter csvExtensionFilter = new FileChooser.ExtensionFilter("CSV files", "*.csv");
		FileChooser.ExtensionFilter jsonExtensionFilter = new FileChooser.ExtensionFilter("JSON files", "*.json");
		fc.getExtensionFilters().addAll(csvExtensionFilter, jsonExtensionFilter, allExtensionFilter);
		fc.setSelectedExtensionFilter(allExtensionFilter);
		File file;
		if (saving) {
			// save file
			file = fc.showSaveDialog(GUI.runningScene.getWindow());
		} else {
			// open file
			file = fc.showOpenDialog(GUI.runningScene.getWindow());
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
			GUI.stage.setTitle("Excel by JANKO - " + Parser.currentFile.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("Fatalna greska pri cuvanju tabele");
			System.out.println(e.getMessage());
		}
	}

	public static Table openTable() {
		File path = getFilePath(false);
		if (path == null) {
			System.out.println("Fajl ne postoji ili je akcija prekinuta");
			return Main.table;
		}
		String extension = Parser.getExtensionFromFilePath(path);
		Table loadedTable = null;
		if (extension.equals(".csv")) {
			loadedTable = Parser.convertCSVToTable(path);
		} else if (extension.equals(".json")) {
			loadedTable = Parser.convertJSONToTable(path);
		} else {
			System.out.println("Ekstenzija nije podrzana.");
			return Main.table;
		}
		Parser.currentFile = path;
		GUI.stage.setTitle("Excel by JANKO - " + Parser.currentFile.getAbsolutePath());
		return loadedTable;
	}
	
	public static void formatSelectedCells(Format format) {
		System.out.println("Changing format");
		for(Cell c:Main.table.selectedCells) {
			Cell.convertCellToFormat(c.getRow(), c.getCol(), format);
		}
		Main.table.updateLabels();
	}
}
