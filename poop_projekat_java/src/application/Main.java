package application;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

	static Table table;

	public static Table getTable() {
		return table;
	}

	public static void setTable(Table table) {
		Main.table = table;
	}

	// indeksiranje pocinje od 0
	public Node getElementOfGrid(GridPane grid, int rowIndex, int colIndex) {
		rowIndex++;
		colIndex++;
		int index = rowIndex * Table.numOfCols + colIndex;
		if (index < 0 || index > grid.getChildren().size())
			return null;
		return grid.getChildren().get(index);
	}

	public void replaceElementInGrid(GridPane grid, Node newElem, int rowIndex, int colIndex) {
		rowIndex++;
		colIndex++;
		int index = rowIndex * Table.numOfCols + colIndex;
		if (index < 0 || index > grid.getChildren().size())
			return;
		grid.getChildren().set(index, newElem);
	}

	@Override
	public void start(Stage primaryStage) {
		GUI.primaryStage = primaryStage;
		primaryStage.setTitle("Excel by JANKO - " + Parser.currentFile.getAbsolutePath());
		GUI.addAskToSaveOnExit();
		table = new Table(3);

		// Create the scene and set it on the stage
		primaryStage.setScene(GUI.makeAndPopulateScene());
		//System.out.println(table.resolveTableFormulas("jani"));
		//table.setCell(0, 0, new Cell("Janko pise u tabeli",0,0));
		//System.out.println(Table.resolvedFormulasCsvString);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}