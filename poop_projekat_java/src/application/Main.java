package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Main extends Application {

	private TableView<String[]> table; // Replace String[] with your data model

	private GridPane gridPane;
	private int rowCount = 2;
	private final int COLUMN_COUNT = 26;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Excel Program");
		Controller cont = new Controller();
		Table t = new Table(3);
		Controller.postaviTabelu(t);

		// Create menu bar
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem newMenuItem = new MenuItem("New Table");
		newMenuItem.setOnAction(e -> {
			System.out.println("nova");
		});
		MenuItem loadMenuItem = new MenuItem("Load Table");
		fileMenu.getItems().addAll(newMenuItem, loadMenuItem);
		menuBar.getMenus().add(fileMenu);

		// Create grid pane
		gridPane = new GridPane();

		// Create the main layout
		BorderPane root = new BorderPane();

		// Create a button for saving the table
		Button saveButton = new Button("dodaj red");
		saveButton.setOnAction(e -> cont.dodajRed(gridPane));

		Button printbtn = new Button("stampaj");
		printbtn.setOnAction(e -> cont.stampaj(gridPane));

		TextField red = new TextField();
		red.setPromptText("red");
		TextField kolona = new TextField();
		kolona.setPromptText("kolona");
		TextField sadrzaj = new TextField();
		sadrzaj.setPromptText("sadrzaj");

		Button dodajpolje = new Button("dodaj polje");
		dodajpolje.setOnAction(e -> cont.dodajPolje(Integer.parseInt(red.getText()), Integer.parseInt(kolona.getText()),
				new Cell(sadrzaj.getText())));

		// Create a VBox to hold the save button
		VBox buttonBox = new VBox(10);
		buttonBox.getChildren().add(saveButton);
		buttonBox.getChildren().add(printbtn);
		buttonBox.getChildren().addAll(red, kolona, sadrzaj);
		buttonBox.getChildren().add(dodajpolje);
		buttonBox.setPadding(new Insets(10));

		ScrollPane sp = new ScrollPane(gridPane);
		// Add the VBox to the right side of the main layout
		root.setRight(buttonBox);
		root.setCenter(sp);

		// Create the scene and set it on the stage
		Scene scene = new Scene(root, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

//	@Override
//	public void start(Stage primaryStage) throws IOException {
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("tabela.fxml"));
//
//		Parent root = loader.load();
//
//		Scene scene = new Scene(root);
//		primaryStage.setScene(scene);
//		primaryStage.setTitle("Excel by Janko");
//		primaryStage.show();
//	}

	private void saveTable() {
		// Implement the logic to save the table in the desired format
		System.out.println("Cuvam");
	}

	public static void main(String[] args) {
		launch(args);
	}
}
