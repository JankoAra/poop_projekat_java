package application;

import java.io.File;
import java.util.Optional;

import application.MyExceptions.FormatChangeUnsuccessful;
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
	// Main stage(window)
	static Stage stage;

	public enum UpdateType {
		TABLE_CHANGE, CELL_CHANGE, CELLS_SELECTION
	}

	public static void updateGUI(UpdateType type) {
		switch (type) {
		case TABLE_CHANGE:
			Main.table.updateLabels();
			GUI.rebuildGrid();
			break;
		case CELL_CHANGE:
			Main.table.updateLabels();
			break;
		case CELLS_SELECTION:
			break;
		default:
			break;
		}

	}

	// Components of GUI when it's in editing mode (a table is active)
	static Scene runningScene;
	static BorderPane rootBorderPane;
	static ScrollPane gridScrollPane;
	static GridPane grid;
	static TextArea logArea;
	static MenuBar menubar;

	// Components of GUI when the program starts
	// (choosing whether to open a table or to create new one)
	static Scene startScene;

	static {
		GUI.startScene = makeStartScene();
		GUI.runningScene = makeRunningScene();
	}

	public static void printLog(String text) {
		if (logArea.getText().length() > 1000) {
			logArea.setText(logArea.getText().substring(250));
		}
		logArea.appendText(text);
	}

	public static void printlnLog(String text) {
		printLog(text);
		printLog("\n");
	}

	// Populate and return the running scene
	private static Scene makeRunningScene() {
		// Main layout manager
		rootBorderPane = new BorderPane();

		// Table grid in a scroll pane
		gridScrollPane = new ScrollPane(grid = GUI.populateGrid(Main.table));
		rootBorderPane.setCenter(gridScrollPane);
		gridScrollPane.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.DELETE) {
				for (Cell c : Main.table.selectedCells) {
					try {
						Cell newCell = new Cell("", c.getFormat(), c.getRow(), c.getCol());
						Main.table.setCell(c.getRow(), c.getCol(), newCell);
					} catch (FormatChangeUnsuccessful e1) {
						e1.printStackTrace();
					}
				}
				updateGUI(UpdateType.CELL_CHANGE);
			} else if (e.getCode() == KeyCode.ESCAPE) {
				Main.table.demarkSelectedCells();
				Main.table.setSelectedRange(0, 0, 0, 0);
				Main.table.clearClickedLabelIndices();
				menubar.requestFocus();
				e.consume();
			}
			e.consume();
		});

		// log area on the bottom
		logArea = new TextArea();
		logArea.setFont(new Font("Arial", 15));
		logArea.setWrapText(true);
		logArea.setEditable(false);
		ScrollPane southSp = new ScrollPane(logArea);
		southSp.setFitToWidth(true);
		rootBorderPane.setBottom(southSp);

		Scene scene = new Scene(rootBorderPane, 1000, 800);

		// Create menu bar
		MenuBar menuBar = new MenuBar();
		menubar = menuBar;
		Menu fileMenu = new Menu("File");
		MenuItem newMenuItem = new MenuItem("New Table");
		newMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
		newMenuItem.setOnAction(e -> {
			Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
			confirmationAlert.setTitle("Confirmation Dialog");
			confirmationAlert.setHeaderText("Do you want to save the current table?");
			confirmationAlert.setContentText("Choose an option.");

			ButtonType saveButtonType = new ButtonType("Save");
			ButtonType noSaveButtonType = new ButtonType("Don't Save");
			ButtonType cancelButtonType = new ButtonType("Cancel");
			confirmationAlert.getButtonTypes().setAll(saveButtonType, noSaveButtonType, cancelButtonType);

			Optional<ButtonType> result = confirmationAlert.showAndWait();
			if (result.isPresent() && result.get() == saveButtonType) {
				// User clicked OK, perform the action
				Controller.saveTable(Main.table, false);
			} else if (result.isPresent() && result.get() == cancelButtonType) {
				// User clicked Cancel, handle accordingly
				return;
			}

			Main.table = new Table(50);
			rebuildGrid();
			Main.table.updateLabels();
			Parser.currentFile = new File("Untitled");
			GUI.stage.setTitle("Excel by JANKO - " + Parser.currentFile.getAbsolutePath());
		});
		MenuItem openMenuItem = new MenuItem("Open Table");
		openMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
		openMenuItem.setOnAction(e -> {
			Main.table = Controller.openTable();
			rebuildGrid();
			Main.table.updateLabels();
		});
		MenuItem saveAsMenuItem = new MenuItem("Save As");
		saveAsMenuItem.setOnAction(e -> Controller.saveTable(Main.table, true));

		MenuItem saveMenuItem = new MenuItem("Save");
		saveMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
		saveMenuItem.setOnAction(e -> Controller.saveTable(Main.table, false));

		fileMenu.getItems().addAll(newMenuItem, openMenuItem, new SeparatorMenuItem(), saveAsMenuItem, saveMenuItem);
		menuBar.getMenus().add(fileMenu);

		// Pane for menubar and table options
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
			rebuildGrid();
			Main.table.updateLabels();
		});

		// Unused
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

		// Formatting options
		Button formatTextBtn = new Button("Format to text");
		formatTextBtn.setOnAction(e -> {
			Controller.formatSelectedCells(Cell.TEXT_FORMAT);
			rebuildGrid();
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
			} catch (NumberFormatException ex) {
				decimals = 2;
			}
			Controller.formatSelectedCells(new NumberFormat(decimals));
			rebuildGrid();
		});
		VBox vbox3 = new VBox(decimalsField, formatNumberBtn);
		vbox3.setPadding(new Insets(5));
		vbox3.setAlignment(Pos.CENTER);

		Button formatDateBtn = new Button("Format to date");
		formatDateBtn.setOnAction(e -> {
			Controller.formatSelectedCells(Cell.DATE_FORMAT);
			rebuildGrid();
		});
		VBox vbox5 = new VBox(formatDateBtn);
		vbox5.setPadding(new Insets(5));
		vbox5.setAlignment(Pos.CENTER);

		northMenu.getChildren().addAll(vbox1, vbox2, vbox4, vbox3, vbox5);

		rootBorderPane.setTop(northPane);

		scene.getStylesheets().add(GUI.class.getResource("labelStyles.css").toExternalForm());

		return scene;
	}

	// Populate and return start scene
	private static Scene makeStartScene() {
		return null;
	}

//	static void replaceLabelWithTextField(GridPane grid, int rowIndex, int columnIndex) {
//		// rowIndex i columnIndex su u gridu, u tabeli su za 1 manji
//		int tri = rowIndex - 1;
//		int tci = columnIndex - 1;
//		Label label = Main.table.getLabel(tri, tci);
//		grid.getChildren().remove(label);
//
//		TextField textField = new TextField(Main.table.getCell(tri, tci).getValue());
//		textField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//		textField.setFont(new Font("Arial", 20));
//		textField.setOnKeyPressed(event -> {
//			if (event.getCode() == KeyCode.ENTER) {
//				System.out.println("enter");
//				GUI.grid.requestFocus();
//			}
//		});
//		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
//			if (!newValue) {
//				System.out.println("Focus lost from TextField");
//				Format oldFormat = Main.table.getCell(rowIndex - 1, columnIndex - 1).getFormat();
//				try {
//					// promena vrednosti celije u tabeli
//					Main.getTable().setCell(rowIndex - 1, columnIndex - 1,
//							new Cell(textField.getText(), oldFormat, rowIndex - 1, columnIndex - 1));
//				} catch (FormatChangeUnsuccessful e) {
//					e.printStackTrace();
//				}
//				Main.table.updateLabels();
//				// menjanje textField-a labelom
//				CellLabel lab = Main.table.getLabel(tri, tci);
//				grid.getChildren().remove(textField);
//				grid.getChildren().add(lab);
//
//				lab.requestFocus();
//			}
//		});
//		GridPane.setConstraints(textField, columnIndex, rowIndex);
//		grid.getChildren().add(textField);
//		textField.requestFocus();
//		textField.selectAll();
//	}

	static void replaceLabelWithTextFieldWithStartValue(GridPane grid, int rowIndex, int columnIndex, String sValue) {
		// rowIndex i columnIndex su u gridu, u tabeli su za 1 manji
		int tri = rowIndex - 1;
		int tci = columnIndex - 1;
		Label label = Main.table.getLabel(tri, tci);
		grid.getChildren().remove(label);
		TextField textField = null;
		if (sValue == null) {
			textField = new TextField(Main.table.getCell(tri, tci).getValue());
		} else {
			textField = new TextField(sValue);
		}
		final TextField tf = textField;
		textField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		//textField.setFont(new Font("Arial", 20));
		textField.getStyleClass().add("active-text-field");
		textField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				// System.out.println("enter");
				if (tri + 1 < Main.table.getNumOfRows()) {
					Main.table.demarkSelectedCells();
					Main.table.setSelectedRange(tri + 1, tci, tri + 1, tci);
					Main.table.setClickedLabelIndices(tri + 1, tci);
					Main.table.markSelectedCells();
					// Main.table.getClickedLabel().requestFocus();
				}
				GUI.grid.requestFocus();
			}
		});
		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			//System.out.println(runningScene.getFocusOwner());
			if (!newValue) {
				// System.out.println("Focus lost from TextField");
				Format oldFormat = Main.table.getCell(rowIndex - 1, columnIndex - 1).getFormat();
				try {
					// promena vrednosti celije u tabeli
					Main.getTable().setCell(rowIndex - 1, columnIndex - 1,
							new Cell(tf.getText(), oldFormat, rowIndex - 1, columnIndex - 1));
				} catch (FormatChangeUnsuccessful e) {
					GUI.printlnLog("Upisana vrednost ne odgovara formatu celije");
					// e.printStackTrace();
				}
				Main.table.updateLabels();
				// menjanje textField-a labelom
				CellLabel lab = Main.table.getLabel(tri, tci);
				grid.getChildren().remove(tf);
				grid.getChildren().add(lab);

				// lab.requestFocus();
				Main.table.getClickedLabel().requestFocus();
			}
		});
		GridPane.setConstraints(textField, columnIndex, rowIndex);
		try {
//			grid.getChildren().add(rowIndex * (Table.numOfCols + 1) + columnIndex + 1, textField);
			grid.getChildren().add(textField);
		} catch (Exception ex) {
			System.out.println("greska pri dodavanju fielda");
		}
		textField.requestFocus();
		if (sValue == null) {
			textField.selectAll();
		} else {
			textField.positionCaret(textField.getText().length());
		}

	}

	// Fills a GridPane with labels displaying table content and
	// labels for numerating rows and columns;
	// Returns newly made grid
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
				CellLabel label = table.getLabel(i, j);
				GridPane.setConstraints(label, j + 1, i + 1);
				grid.getChildren().add(label);
				label.deselectLabel();
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
		firstLabel.getStyleClass().add("corner-label");
//		firstLabel.setStyle("-fx-background-color:white;-fx-border-color:black;");
//		firstLabel.setPadding(new Insets(5));
		firstLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		firstLabel.setOnMouseClicked(e -> {
			Main.table.demarkSelectedCells();
			Main.table.setSelectedRange(0, 0, Main.table.getNumOfRows() - 1, Table.numOfCols - 1);
			Main.table.markSelectedCells();
		});
		GridPane.setConstraints(firstLabel, 0, 0);
		grid.getChildren().add(firstLabel);

		return grid;
	}

	// Populates a new GridPane with labels and sets it as main grid
	public static void rebuildGrid() {
		GUI.grid = GUI.populateGrid(Main.table);
		GUI.gridScrollPane.setContent(GUI.grid);
	}

}
