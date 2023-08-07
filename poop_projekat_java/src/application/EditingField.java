package application;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

public class EditingField extends TextField {

	private int tri, tci;
	private GridPane myGrid;

	public GridPane getMyGrid() {
		return myGrid;
	}

	public EditingField(GridPane grid, int tri, int tci) {
		this.tri = tri;
		this.tci = tci;
		this.myGrid = grid;
		initEditingField(this);
	}

	public EditingField(String arg0, GridPane grid, int tri, int tci) {
		super(arg0);
		this.tri = tri;
		this.tci = tci;
		this.myGrid = grid;
		initEditingField(this);
	}

	private static void initEditingField(EditingField textField) {

		textField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		textField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				if (textField.tri + 1 < Main.table.getNumOfRows()) {
					Main.table.demarkSelectedCells();
					Main.table.setSelectedRange(textField.tri + 1, textField.tci, textField.tri + 1, textField.tci);
					Main.table.setClickedLabelIndices(textField.tri + 1, textField.tci);
					Main.table.markSelectedCells();
					textField.myGrid.requestFocus();
				} else {
					Main.table.demarkSelectedCells();
					Main.table.setSelectedRange(textField.tri, textField.tci, textField.tri, textField.tci);
					Main.table.setClickedLabelIndices(textField.tri, textField.tci);
					Main.table.markSelectedCells();
					textField.myGrid.requestFocus();
				}
				GUI.replaceEditingFieldWithLabel();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				event.consume();
				GUI.replaceEditingFieldWithLabel();
				textField.myGrid.requestFocus();

				Main.table.demarkSelectedCells();
				Main.table.clearClickedLabelIndices();
			}
		});
	}
}
