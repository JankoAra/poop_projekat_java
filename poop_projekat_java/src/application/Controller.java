package application;

import java.io.File;

import application.GUI.UpdateType;
import application.Table.IndexPair;
import application.UndoRedoStack.ActionType;
import javafx.stage.FileChooser;

public class Controller {
	/**
	 * Otvara dijalog za biranje putanje fajla prilikom snimanja ili ucitavanja tabele.
	 * @param saving - true ako se snima tabela; false ako se ucitava tabela
	 * @return Vraca fajl koji je izabran ako je uspesno, null ako nije ili ako je kliknuto Cancel u dijalogu.
	 */
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
		}
		else {
			// open file
			file = fc.showOpenDialog(GUI.runningScene.getWindow());
		}
		return file;
	}

	/**
	 * Snima tabelu.
	 * @param table - Tabela koja se snima.
	 * @param saveAs - true ako se bira putanja do fajla, false ako se koristi poslednje koriscena putanja
	 */
	public static void saveTable(Table table, boolean saveAs) {
		try {
			File path;
			if (Parser.currentFile.getName().equals("Untitled") || saveAs == true) {
				path = Controller.getFilePath(true);
			}
			else {
				path = Parser.currentFile;
			}
			if (path == null) throw new Exception();
			String extension = Parser.getExtensionFromFilePath(path);
			String fileContent = Parser.convertTableToString(table, extension);
			Parser.createNewSaveFile(path, fileContent);
			Parser.currentFile = path;
			GUI.stage.setTitle("Excel by JANKO - " + Parser.currentFile.getAbsolutePath());
		}
		catch (Exception e) {
			System.out.println("Fatalna greska pri cuvanju tabele");
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Ucitava tabelu iz CSV ili JSON fajla.
	 * @return Vraca ucitanu tabelu.
	 */
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
		}
		else if (extension.equals(".json")) {
			loadedTable = Parser.convertJSONToTable(path);
		}
		else {
			System.out.println("Ekstenzija nije podrzana.");
			return Main.table;
		}
		Parser.currentFile = path;
		GUI.stage.setTitle("Excel by JANKO - " + Parser.currentFile.getAbsolutePath());
		return loadedTable;
	}

	/**
	 * Formatira sve selektovane celije prema zadatom formatu, ukoliko je to moguce.
	 * @param format - Zeljeni format.
	 */
	public static void formatSelectedCells(Format format) {
		if (GUI.activeEditingField != null) {
			GUI.replaceEditingFieldWithLabel();
		}
		if (Main.table.selectedCellsIndices.isEmpty()) {
			GUI.printlnLog("Nijedna celija nije selektovana.");
			return;
		}
		UndoRedoStack.clearRedoStack();
		UndoRedoStack.undoStackType.push(ActionType.CELL_CHANGE);
		UndoRedoStack.undoStackNumber.push(Main.table.selectedCellsIndices.size());
		for (IndexPair pair : Main.table.selectedCellsIndices) {
			Cell c = Main.table.getCell(pair.row, pair.col);
			UndoRedoStack.undoStackCells.push(c);
			Cell.convertCellToFormat(c.getRow(), c.getCol(), format);
		}
		GUI.updateGUI(UpdateType.CELL_CHANGE);
	}
}
