package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUI {
	static BorderPane rootBorderPane;
	static ScrollPane sp;
	static GridPane grid;
	static Scene scene;
	static Stage primaryStage;
	static TextArea logArea;
	
	public static void printLog(String text) {
		if(logArea.getText().length()>1000) {
			logArea.setText(logArea.getText().substring(250));
		}
		logArea.appendText(text);
	}
	
	public static void printlnLog(String text) {
		printLog(text);printlnLog("\n");
	}

	// create, populate and return the main scene; called in start method of
	// Main(Application) class
	public static Scene makeAndPopulateScene() {
		// create the main layout
		rootBorderPane = new BorderPane();

		// grid in a scroll pane
		sp = new ScrollPane(grid = GUI.populateGrid(Main.table));
		rootBorderPane.setCenter(sp);
		
		// log area on the right
		logArea = new TextArea();
		logArea.setFont(new Font("Arial", 15));
		logArea.setWrapText(true);
		logArea.setEditable(false);
		ScrollPane eastSp = new ScrollPane(logArea);
		eastSp.setFitToWidth(true);
		rootBorderPane.setBottom(eastSp);

		// make the main scene
		scene = new Scene(rootBorderPane, 1000, 800);

		// Create menu bar
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem newMenuItem = new MenuItem("New Table");
		newMenuItem.setOnAction(e -> {
			System.out.println("nova");
		});
		MenuItem openMenuItem = new MenuItem("Open Table");
		openMenuItem.setOnAction(e -> {
			Controller.openTable();
			GUI.grid = populateGrid(Main.table);
			GUI.sp.setContent(GUI.grid);
		});
		MenuItem saveAsMenuItem = new MenuItem("Save As");
		saveAsMenuItem.setOnAction(e -> Controller.saveTable(Main.table, true));

		MenuItem saveMenuItem = new MenuItem("Save");
		saveMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
		saveMenuItem.setOnAction(e -> Controller.saveTable(Main.table, false));

		fileMenu.getItems().addAll(newMenuItem, openMenuItem, new SeparatorMenuItem(), saveAsMenuItem, saveMenuItem);
		menuBar.getMenus().add(fileMenu);

		// pane for menubar and options
		BorderPane northPane = new BorderPane();
		northPane.setTop(menuBar);

		// Create table manipulation options
		FlowPane northMenu = new FlowPane();
		northPane.setCenter(northMenu);

		// Buttons and fields
		Button saveBtn = new Button("Save");
		saveBtn.setOnAction(e -> Controller.saveTable(Main.table, false));

		Button addRowBtn = new Button("Add row");
		addRowBtn.setOnAction(e -> {
			Main.table.addRow();
			GUI.grid = GUI.populateGrid(Main.table);
			GUI.sp.setContent(GUI.grid);
		});

		TextField rowIndexField = new TextField();
		rowIndexField.setPromptText("row");
		TextField columnIndexField = new TextField();
		columnIndexField.setPromptText("column");
		TextField newValueField = new TextField();
		newValueField.setPromptText("new value");
		Button changeValueBtn = new Button("Change value");

		VBox vbox1 = new VBox(saveBtn, addRowBtn);
		VBox vbox2 = new VBox(rowIndexField, columnIndexField, newValueField, changeValueBtn);
		vbox1.setPadding(new Insets(5));
		vbox2.setPadding(new Insets(5));

		// formatting options
		Button formatTextBtn = new Button("Format to text");
		formatTextBtn.setOnAction(e -> {
			Controller.formatSelectedCells(Cell.TEXT_FORMAT);
			repaintGrid();
		});
		VBox vbox4 = new VBox(formatTextBtn);
		vbox4.setPadding(new Insets(5));
		vbox4.setAlignment(Pos.CENTER);
		TextField decimalsField = new TextField();
		decimalsField.setPromptText("number of decimals");
		Button formatNumberBtn = new Button("Format to number");
		formatNumberBtn.setOnAction(e -> {
			int decimals;
			try {
				decimals = Integer.parseInt(decimalsField.getText());
			}
			catch (NumberFormatException ex) {
				decimals = 2;
			}
			Controller.formatSelectedCells(new NumberFormat(decimals));
			repaintGrid();
		});
		VBox vbox3 = new VBox(decimalsField, formatNumberBtn);
		vbox3.setPadding(new Insets(5));
		vbox3.setAlignment(Pos.CENTER);
		Button formatDateBtn = new Button("Format to date");
		formatDateBtn.setOnAction(e -> {
			Controller.formatSelectedCells(Cell.DATE_FORMAT);
			repaintGrid();
		});
		VBox vbox5 = new VBox(formatDateBtn);
		vbox5.setPadding(new Insets(5));
		vbox5.setAlignment(Pos.CENTER);

		northMenu.getChildren().addAll(vbox1, vbox2, vbox4, vbox3, vbox5);

		// populate layout and make scene
		rootBorderPane.setTop(northPane);

		return scene;
	}

	static void replaceLabelWithTextField(GridPane grid, int rowIndex, int columnIndex) {
		// rowIndex i columnIndex su u gridu, u tabeli su za 1 manji
		int tri = rowIndex - 1;
		int tci = columnIndex - 1;
		Label label = Main.table.getLabel(tri, tci);
		grid.getChildren().remove(label);

		TextField textField = new TextField(Main.table.getCell(tri, tci).getValue());
		textField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		textField.setFont(new Font("Arial", 20));
		textField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				System.out.println("enter");
				GUI.grid.requestFocus();
			}
		});
		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				System.out.println("Focus lost from TextField");
				Format oldFormat = Main.table.getCell(rowIndex - 1, columnIndex - 1).getFormat();
				try {
					// promena vrednosti celije u tabeli
					Main.getTable().setCell(rowIndex - 1, columnIndex - 1,
							new Cell(textField.getText(), oldFormat, rowIndex - 1, columnIndex - 1));
				} catch (FormatChangeUnsuccessful e) {
					e.printStackTrace();
				}
				// menjanje textField-a labelom
				CellLabel lab = Main.table.getLabel(tri, tci);
				grid.getChildren().remove(textField);
				grid.getChildren().add(lab);

				lab.requestFocus();
			}
		});
		GridPane.setConstraints(textField, columnIndex, rowIndex);
		grid.getChildren().add(rowIndex * (Table.numOfCols + 1) + columnIndex + 1, textField);
		textField.requestFocus();
		textField.selectAll();
	}

	static void replaceLabelWithTextFieldWithStartValue(GridPane grid, int rowIndex, int columnIndex, String sValue) {
		// rowIndex i columnIndex su u gridu, u tabeli su za 1 manji
		int tri = rowIndex - 1;
		int tci = columnIndex - 1;
		Label label = Main.table.getLabel(tri, tci);
		grid.getChildren().remove(label);

		TextField textField = new TextField(sValue);
		textField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		textField.setFont(new Font("Arial", 20));
		textField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				System.out.println("enter");
				GUI.grid.requestFocus();
			}
		});
		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				System.out.println("Focus lost from TextField");
				Format oldFormat = Main.table.getCell(rowIndex - 1, columnIndex - 1).getFormat();
				try {
					// promena vrednosti celije u tabeli
					Main.getTable().setCell(rowIndex - 1, columnIndex - 1,
							new Cell(textField.getText(), oldFormat, rowIndex - 1, columnIndex - 1));
				} catch (FormatChangeUnsuccessful e) {
					e.printStackTrace();
				}
				// menjanje textField-a labelom
				CellLabel lab = Main.table.getLabel(tri, tci);
				grid.getChildren().remove(textField);
				grid.getChildren().add(lab);

				lab.requestFocus();
			}
		});
		GridPane.setConstraints(textField, columnIndex, rowIndex);
		grid.getChildren().add(rowIndex * (Table.numOfCols + 1) + columnIndex + 1, textField);
		textField.positionCaret(textField.getText().length());
		textField.requestFocus();

	}

	public static GridPane populateGrid(Table table) {
		GridPane grid = new GridPane();

		// Create column constraints and set them to grow always
		for (int j = 0; j <= Table.numOfCols; j++) {
			ColumnConstraints columnConstraints = new ColumnConstraints();
			columnConstraints.setHgrow(Priority.ALWAYS);
			grid.getColumnConstraints().add(columnConstraints);
		}

		// Create row constraints and set them to grow always
		for (int i = 0; i <= table.getNumOfRows(); i++) {
			RowConstraints rowConstraints = new RowConstraints();
			rowConstraints.setVgrow(Priority.ALWAYS);
			grid.getRowConstraints().add(rowConstraints);
		}

		// popunjavanje celija sa sadrzajem (bez prvog reda i prve kolone koji su za
		// indeksiranje)
		for (int i = 0; i < table.getNumOfRows(); i++) {
			for (int j = 0; j < Table.numOfCols; j++) {
				Label label = table.getLabel(i, j);
				GridPane.setConstraints(label, j + 1, i + 1);
				grid.getChildren().add(label);
			}
		}

		// popunjavanje prve kolone
		for (int i = 0; i < table.getNumOfRows(); i++) {
			RowLabel label = new RowLabel("" + (i + 1));
			GridPane.setConstraints(label, 0, i + 1);
			grid.getChildren().add(label);
		}

		// popunjavanje prvog reda
		for (int j = 0; j < Table.numOfCols; j++) {
			ColumnLabel label = new ColumnLabel(String.format("%c", j + 'A'));
			GridPane.setConstraints(label, j + 1, 0);
			grid.getChildren().add(label);
		}

		// gornji levi cosak, za selektovanje cele tabele
		Label firstLabel = new Label();
		firstLabel.setMinWidth(80);
		firstLabel.setStyle("-fx-background-color:white;-fx-border-color:black;");
		firstLabel.setPadding(new Insets(5));
		firstLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		firstLabel.setOnMouseClicked(e->{
			Main.table.demarkSelectedCells();
			Main.table.setSelectedRange(0, 0, Main.table.getNumOfRows()-1, Table.numOfCols-1);
			Main.table.markSelectedCells();
		});
		GridPane.setConstraints(firstLabel, 0, 0);
		grid.getChildren().add(firstLabel);

		return grid;
	}

	public static void repaintGrid() {
		GUI.grid = GUI.populateGrid(Main.table);
		GUI.sp.setContent(GUI.grid);
	}

	public static void addAskToSaveOnExit() {
		Stage primaryStage = GUI.primaryStage;
		primaryStage.setOnCloseRequest(event -> {
			event.consume(); // Consume the event to prevent the application from closing immediately

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
					primaryStage.close(); // Close the application after saving
				} else if (response == discardButton) {
					// No need to save, just exit the application
					primaryStage.close();
				} else {
					// User clicked Cancel, do nothing (let the application continue running)
				}
			});

		});
	}
}
