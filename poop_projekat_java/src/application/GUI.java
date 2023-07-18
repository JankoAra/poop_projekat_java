package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class GUI {
	private static TextField activeTextField = null;

	private static void replaceLabelWithTextField(GridPane grid, int columnIndex, int rowIndex) {
		if(activeTextField!=null) {
			int ri = GridPane.getRowIndex(activeTextField);
			int ci = GridPane.getColumnIndex(activeTextField);
			Main.getTable().setCell(ri - 1, ci - 1, new Cell(activeTextField.getText()));
			grid.getChildren().remove(activeTextField);

            Label label = new Label(activeTextField.getText());
            label.setFont(new Font("Arial", 20));
            label.setPadding(new Insets(5));
            label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            GridPane.setConstraints(label, GridPane.getColumnIndex(activeTextField), GridPane.getRowIndex(activeTextField));
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
				Main.setGrid(GUI.populateGrid(Main.getTable()));
				Main.getSp().setContent(Main.getGrid());
				activeTextField = null;
			}
		});
		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				System.out.println("Focus lost from TextField");
				Main.getTable().setCell(rowIndex - 1, columnIndex - 1, new Cell(textField.getText()));
				Main.setGrid(GUI.populateGrid(Main.getTable()));
				Main.getSp().setContent(Main.getGrid());
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
