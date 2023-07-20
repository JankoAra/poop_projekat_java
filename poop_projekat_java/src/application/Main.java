package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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

//		String currentDirectory = System.getProperty("user.dir");
//		System.out.println("Current Directory: " + currentDirectory);
		// table = Parser.loadCSVTable("test1.csv");
		table = new Table(5);

		// Create the scene and set it on the stage
		primaryStage.setScene(GUI.makeAndPopulateScene());
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

//@Override
//public void start(Stage primaryStage) throws IOException {
//	FXMLLoader loader = new FXMLLoader(getClass().getResource("tabela.fxml"));
//
//	Parent root = loader.load();
//
//	Scene scene = new Scene(root);
//	primaryStage.setScene(scene);
//	primaryStage.setTitle("Excel by Janko");
//	primaryStage.show();
//}

//
//	private TableView<String[]> table; // Replace String[] with your data model
//
//	private GridPane gridPane;
//	private int rowCount = 2;
//	private final int COLUMN_COUNT = 26;
//
//	// indeksiranje pocinje od 0
//	public Node getElementOfGrid(GridPane grid, int rowIndex, int colIndex) {
//		rowIndex++;
//		colIndex++;
//		int index = rowIndex * Table.numOfCols + colIndex;
//		if (index < 0 || index > grid.getChildren().size())
//			return null;
//		return grid.getChildren().get(index);
//	}
//
//	public void replaceElementInGrid(GridPane grid, Node newElem, int rowIndex, int colIndex) {
//		rowIndex++;
//		colIndex++;
//		int index = rowIndex * Table.numOfCols + colIndex;
//		if (index < 0 || index > grid.getChildren().size())
//			return;
//		grid.getChildren().set(index, newElem);
//	}
//
//	@Override
//	public void start(Stage primaryStage) {
//		primaryStage.setTitle("Excel Program");
//		Controller cont = new Controller();
//		String currentDirectory = System.getProperty("user.dir");
//		System.out.println("Current Directory: " + currentDirectory);
//		Table t = Parser.loadCSVTable("test1.csv");
//		Controller.postaviTabelu(t);
//
//		// Create menu bar
//		MenuBar menuBar = new MenuBar();
//		Menu fileMenu = new Menu("File");
//		MenuItem newMenuItem = new MenuItem("New Table");
//		newMenuItem.setOnAction(e -> {
//			System.out.println("nova");
//		});
//		MenuItem loadMenuItem = new MenuItem("Load Table");
//		fileMenu.getItems().addAll(newMenuItem, loadMenuItem);
//		menuBar.getMenus().add(fileMenu);
//
//		// Create grid pane
//		gridPane = GUI.populateGrid(t);
//		ScrollPane sp = new ScrollPane(gridPane);
//
//		// Create the main layout
//		BorderPane root = new BorderPane();
//
//		// Create a button for saving the table
//		Button dodajredbtn = new Button("dodaj red");
//		dodajredbtn.setOnAction(e -> cont.dodajRed(gridPane));
//
//		Button printbtn = new Button("stampaj");
//		// printbtn.setOnAction(e -> cont.stampaj(gridPane));
//		printbtn.setOnAction(e -> {
//			sp.setContent(GUI.populateGrid(t));
//		});
//
//		TextField red = new TextField();
//		red.setPromptText("red");
//		TextField kolona = new TextField();
//		kolona.setPromptText("kolona");
//		TextField sadrzaj = new TextField();
//		sadrzaj.setPromptText("sadrzaj");
//
//		Button dodajpolje = new Button("dodaj polje");
//		dodajpolje.setOnAction(e -> cont.dodajPolje(Integer.parseInt(red.getText()), Integer.parseInt(kolona.getText()),
//				new Cell(sadrzaj.getText())));
//
//		Button replacebtn = new Button("replace");
//		replacebtn.setOnAction(e -> {
//			Label l = new Label("zamenjeno");
//			replaceElementInGrid(gridPane, l, Integer.parseInt(red.getText()), Integer.parseInt(kolona.getText()));
//		});
//
//		// Create a VBox to hold the save button
//		VBox buttonBox = new VBox(10);
//		buttonBox.getChildren().add(dodajredbtn);
//		buttonBox.getChildren().add(printbtn);
//		buttonBox.getChildren().addAll(red, kolona, sadrzaj);
//		buttonBox.getChildren().add(dodajpolje);
//		buttonBox.getChildren().add(replacebtn);
//		buttonBox.setPadding(new Insets(10));
//
//		// Add the VBox to the right side of the main layout
//		root.setRight(buttonBox);
//		root.setCenter(sp);
//
//		// Create the scene and set it on the stage
//		Scene scene = new Scene(root, 800, 600);
//		primaryStage.setScene(scene);
//		primaryStage.show();
//	}
//
////	@Override
////	public void start(Stage primaryStage) throws IOException {
////		FXMLLoader loader = new FXMLLoader(getClass().getResource("tabela.fxml"));
////
////		Parent root = loader.load();
////
////		Scene scene = new Scene(root);
////		primaryStage.setScene(scene);
////		primaryStage.setTitle("Excel by Janko");
////		primaryStage.show();
////	}
//
//	private void saveTable() {
//		// Implement the logic to save the table in the desired format
//		System.out.println("Cuvam");
//	}
//
//	public static void main(String[] args) {
//		launch(args);
//	}

//// Create grid pane
//gridPane = GUI.populateGrid(table);
//ScrollPane sp = new ScrollPane(gridPane);
//
//// Create a button for saving the table
//Button dodajredbtn = new Button("dodaj red");
//dodajredbtn.setOnAction(e -> cont.dodajRed(gridPane));
//
//Button printbtn = new Button("stampaj");
//// printbtn.setOnAction(e -> cont.stampaj(gridPane));
//printbtn.setOnAction(e -> {
//	sp.setContent(GUI.populateGrid(table));
//});
//
//TextField red = new TextField();
//red.setPromptText("red");
//TextField kolona = new TextField();
//kolona.setPromptText("kolona");
//TextField sadrzaj = new TextField();
//sadrzaj.setPromptText("sadrzaj");
//
//Button dodajpolje = new Button("dodaj polje");
//dodajpolje.setOnAction(e -> cont.dodajPolje(Integer.parseInt(red.getText()), Integer.parseInt(kolona.getText()),
//		new Cell(sadrzaj.getText())));
//
//Button replacebtn = new Button("replace");
//replacebtn.setOnAction(e -> {
//	Label l = new Label("zamenjeno");
//	replaceElementInGrid(gridPane, l, Integer.parseInt(red.getText()), Integer.parseInt(kolona.getText()));
//});
//
//// Create a VBox to hold the save button
//VBox buttonBox = new VBox(10);
//buttonBox.getChildren().add(dodajredbtn);
//buttonBox.getChildren().add(printbtn);
//buttonBox.getChildren().addAll(red, kolona, sadrzaj);
//buttonBox.getChildren().add(dodajpolje);
//buttonBox.getChildren().add(replacebtn);
//buttonBox.setPadding(new Insets(10));
//
//// Add the VBox to the right side of the main layout
//root.setRight(buttonBox);
//root.setCenter(sp);
