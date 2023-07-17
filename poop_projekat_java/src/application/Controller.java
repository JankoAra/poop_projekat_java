package application;

import java.net.URL;
import java.util.ResourceBundle;

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

public class Controller implements Initializable {

	@FXML
	private Button stampajBtn;

	@FXML
	private Button dodajBtn;

	@FXML
	private Button promeniBtn;

	@FXML
	private TextArea tfRow;

	@FXML
	private TextArea tfCol;

	@FXML
	private GridPane gridPane;

	@FXML
	void dodajRed(GridPane grid) {
		if (tabela == null)
			return;
		tabela.addRow();
		//stampaj(grid);
	}

	public void dodajPolje(int x, int y, Cell polje) {
		tabela.setCell(x, y, polje);
	}

	@FXML
	void resizeGrid(ActionEvent event) {

	}

	public void populateGrid(GridPane grid) {
		grid.getChildren().clear();
		grid.getColumnConstraints().clear();
		for (int i = 0; i < tabela.getNumOfRows(); i++) {
			for (int j = 0; j < Table.numOfCols; j++) {
				String val = tabela.getData().get(i).get(j).getValue();
				val = val==""?"--empty--":val;
				Label label = new Label(val);
				label.setPadding(new Insets(5));
				GridPane.setConstraints(label, j, i);

				// don't forget to add children to gridpane
				grid.getChildren().add(label);
				grid.setGridLinesVisible(true);
			}
		}
	}

	@FXML
	void stampaj(GridPane grid) {
		if (tabela == null)
			return;
		System.out.println("\n--------------------------\n");
		//System.out.println(tabela);
		System.out.println("\n--------------------------\n");
		populateGrid(grid);

	}

	private static Table tabela;

	public static void postaviTabelu(Table t) {
		tabela = t;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}
}
