package application;

import java.io.File;
import java.util.Optional;

import application.GUI.UpdateType;
import application.MyExceptions.FormatChangeUnsuccessful;
import application.UndoRedoStack.ActionType;
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

	/**
	 * Radi update izgleda tabele u GUI-u, na osnovu tipa promene koji se desio
	 * 
	 * @param type - Tip promene (TABLE_CHANGE, CELL_CHANGE, CELLS_SELECTION)
	 */
	public static void updateGUI(UpdateType type) {
		switch (type) {
		case TABLE_CHANGE:
			// dodat novi red
			Main.table.updateLabels();
			GUI.rebuildGrid();
			break;
		case CELL_CHANGE:
			// promena vrednosti ili formata celija
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

	/**
	 * Pravi novu scenu koja je aktivna dok program radi u rezimu editovanja tabele
	 * 
	 * @return Vraca napravljenu scenu sa glavnom tabelom i opcijama za editovanje
	 */
	private static Scene makeRunningScene() {
		// Main layout manager
		rootBorderPane = new BorderPane();

		// Table grid in a scroll pane
		gridScrollPane = new ScrollPane(grid = GUI.populateGrid(Main.table));
		rootBorderPane.setCenter(gridScrollPane);
		gridScrollPane.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.DELETE) {
				UndoRedoStack.clearRedoStack();
				UndoRedoStack.undoStackType.push(ActionType.CELL_CHANGE);
				UndoRedoStack.undoStackNumber.push(Main.table.selectedCells.size());
				for (Cell c : Main.table.selectedCells) {
					try {
						UndoRedoStack.undoStackCells.push(c);
						Cell newCell = new Cell("", c.getFormat(), c.getRow(), c.getCol());
						Main.table.setCell(c.getRow(), c.getCol(), newCell);
					} catch (FormatChangeUnsuccessful e1) {
						//nece se desiti
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

		Button undoBtn = new Button("Undo");
		undoBtn.setOnAction(e -> {
			UndoRedoStack.undo();
			Main.table.clearClickedLabelIndices();
			GUI.updateGUI(UpdateType.CELL_CHANGE);
		});

		Button redoBtn = new Button("Redo");
		redoBtn.setOnAction(e -> {
			UndoRedoStack.redo();
			Main.table.clearClickedLabelIndices();
			GUI.updateGUI(UpdateType.CELL_CHANGE);
		});

		// Unused
		TextField rowIndexField = new TextField();
		rowIndexField.setPromptText("row");
		TextField columnIndexField = new TextField();
		columnIndexField.setPromptText("column");
		TextField newValueField = new TextField();
		newValueField.setPromptText("new value");
		Button changeValueBtn = new Button("Change value");

		VBox vbox1 = new VBox(saveBtn, addRowBtn, undoBtn, redoBtn);
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

	/**
	 * Menja labelu u tabeli poljem za upisivanje teksta, koje omogucava promenu
	 * sadrzaja celije na tom mestu
	 * 
	 * @param grid   - GridPane layout u kom se nalazi labela
	 * @param gri    - Redni broj reda u gridu (od 0)
	 * @param gci    - Redni broj kolone u gridu (od 0 do Table.numOfCols)
	 * @param sValue - pocetna vrednost polja ako promena nastaje pisanjem sa
	 *               tastature, ukoliko nastaje dvoklikom na labelu onda je null i
	 *               pocetna vrednost je sadrzaj celije
	 */
	static void replaceLabelWithEditingField(GridPane grid, int gri, int gci, String sValue) {
		// indeksi u tabeli
		int tri = gri - 1;
		int tci = gci - 1;

		Label label = Main.table.getLabel(tri, tci);
		grid.getChildren().remove(label);

		EditingField textField = null;
		if (sValue == null) {
			textField = new EditingField(Main.table.getCell(tri, tci).getValue(), grid, tri, tci);
		} else {
			textField = new EditingField(sValue, grid, tri, tci);
		}
		GridPane.setConstraints(textField, gci, gri);
		grid.getChildren().add(textField);
		textField.requestFocus();
		if (sValue == null) {
			textField.selectAll();
		} else {
			textField.positionCaret(textField.getText().length());
		}

	}

	/**
	 * Pravi novi GridPane na osnovu labela iz zadate tabele. Takodje pravi i labele
	 * za indeksiranje i selektovanje redova i kolona
	 * 
	 * @param table - Tabela cije se labele iz interne liste labela postavljaju u
	 *              novi grid
	 * @return Napravljeni GridPane
	 */
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

	/**
	 * Stvara novi GridPane, popunjava ga labelama iz Main.table i postavlja ga kao
	 * glavni grid(GUI.grid)
	 */
	public static void rebuildGrid() {
		GUI.grid = GUI.populateGrid(Main.table);
		GUI.gridScrollPane.setContent(GUI.grid);
	}

}
