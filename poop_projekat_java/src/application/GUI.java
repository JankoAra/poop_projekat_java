package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class GUI {
	static BorderPane rootBorderPane;
	static ScrollPane sp;
	static GridPane grid;
	static Scene scene;
	private static TextField activeTextField = null;

	// create, populate and return the main scene; called in start method of
	// Main(Application) class
	public static Scene makeAndPopulateScene() {
		// create the main layout
		rootBorderPane = new BorderPane();

		// grid in a scroll pane
		sp = new ScrollPane(grid = GUI.populateGrid(Main.table));
		rootBorderPane.setCenter(sp);

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
			Main.table = Parser.loadCSVTable(Controller.getFilePath(false));
			GUI.grid = populateGrid(Main.table);
			GUI.sp.setContent(GUI.grid);
		});
		MenuItem saveAsMenuItem = new MenuItem("Save As");
		MenuItem saveMenuItem = new MenuItem("Save");
		saveMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));

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
		saveBtn.setOnAction(e -> {
			FileChooser fc = new FileChooser();
			fc.setSelectedExtensionFilter(new ExtensionFilter("tekstualni", "*.txt"));
			File file = fc.showSaveDialog(scene.getWindow());
			if (file != null) {

				PrintWriter writer;
				try {
					writer = new PrintWriter(file);
					writer.println("cuvam fajl");
					writer.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		Button addRowBtn = new Button("Add row");

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
		VBox vbox4 = new VBox(formatTextBtn);
		vbox4.setPadding(new Insets(5));
		vbox4.setAlignment(Pos.CENTER);
		TextField decimalsField = new TextField();
		decimalsField.setPromptText("number of decimals");
		Button formatNumberBtn = new Button("Format to number");
		VBox vbox3 = new VBox(decimalsField, formatNumberBtn);
		vbox3.setPadding(new Insets(5));
		vbox3.setAlignment(Pos.CENTER);
		Button formatDateBtn = new Button("Format to date");
		VBox vbox5 = new VBox(formatDateBtn);
		vbox5.setPadding(new Insets(5));
		vbox5.setAlignment(Pos.CENTER);

		northMenu.getChildren().addAll(vbox1, vbox2, vbox4, vbox3, vbox5);

		// populate layout and make scene
		rootBorderPane.setTop(northPane);

		return scene;
	}

	private static void replaceLabelWithTextField(GridPane grid, int columnIndex, int rowIndex) {
		if (activeTextField != null) {
			int ri = GridPane.getRowIndex(activeTextField);
			int ci = GridPane.getColumnIndex(activeTextField);
			Main.getTable().setCell(ri - 1, ci - 1, new Cell(activeTextField.getText()));
			grid.getChildren().remove(activeTextField);

			Label label = new Label(activeTextField.getText());
			label.setFont(new Font("Arial", 20));
			label.setPadding(new Insets(5));
			label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			GridPane.setConstraints(label, GridPane.getColumnIndex(activeTextField),
					GridPane.getRowIndex(activeTextField));
			activeTextField = null;

			grid.getChildren().add(label);
		}
		Label label = (Label) grid.getChildren().get((rowIndex * (Table.numOfCols + 1) + columnIndex + 1));
		grid.getChildren().remove(label);

		TextField textField = new TextField(label.getText());
		textField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		textField.setFont(new Font("Arial", 20));
		textField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				System.out.println("enter");
				Main.getTable().setCell(rowIndex - 1, columnIndex - 1, new Cell(textField.getText()));
				GUI.grid = GUI.populateGrid(Main.table);
				GUI.sp.setContent(GUI.grid);
				activeTextField = null;
			}
		});
		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				System.out.println("Focus lost from TextField");
				Main.getTable().setCell(rowIndex - 1, columnIndex - 1, new Cell(textField.getText()));
				GUI.grid = GUI.populateGrid(Main.table);
				GUI.sp.setContent(GUI.grid);
				activeTextField = null;
			}
		});
		GridPane.setConstraints(textField, columnIndex, rowIndex);
		grid.getChildren().add(rowIndex * (Table.numOfCols + 1) + columnIndex + 1, textField);
		textField.requestFocus();
		textField.selectAll();
		activeTextField = textField;
	}

	public static GridPane populateGrid(Table table) {
		GridPane grid = new GridPane();

		// Create column constraints and set them to grow always
		for (int j = 0; j <= table.getNumOfColumns(); j++) {
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

		for (int i = 0; i <= table.getNumOfRows(); i++) {
			for (int j = 0; j <= table.getNumOfColumns(); j++) {
				String val = "";
				Label label = new Label();
				GridPane.setConstraints(label, j, i);
				if (i == 0 && j == 0) {

				} else if (i == 0) {
					val = String.format("%c", 'A' + j - 1);
					label.setAlignment(Pos.CENTER);
					label.setOnMouseClicked(e -> {
						System.out.println(label.getText());
					});
				} else if (j == 0) {
					val = String.format("%d", i);
				} else {
					val = table.getData().get(i - 1).get(j - 1).getValue();
					label.setOnMouseClicked(e -> {
						int ri = GridPane.getRowIndex(label);
						int ci = GridPane.getColumnIndex(label);
						// indeksi su u gridu
						System.out.println("red " + ri + "/kolona " + ci);
						replaceLabelWithTextField(grid, ci, ri);
					});

				}

				label.setText(val);
				label.setFont(new Font("Arial", 20));
				label.setPadding(new Insets(5));
				label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

				grid.getChildren().add(label);
				grid.setGridLinesVisible(true);
			}
		}
		return grid;
	}

}
