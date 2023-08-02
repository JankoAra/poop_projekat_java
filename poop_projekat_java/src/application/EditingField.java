package application;

import application.MyExceptions.FormatChangeUnsuccessful;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

public class EditingField extends TextField {

	private int tri, tci;
	private GridPane myGrid;

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
				// System.out.println("enter");
				if (textField.tri + 1 < Main.table.getNumOfRows()) {
					Main.table.demarkSelectedCells();
					Main.table.setSelectedRange(textField.tri + 1, textField.tci, textField.tri + 1, textField.tci);
					Main.table.setClickedLabelIndices(textField.tri + 1, textField.tci);
					Main.table.markSelectedCells();
					textField.myGrid.requestFocus();
				}
				else {
					Main.table.demarkSelectedCells();
					Main.table.setSelectedRange(textField.tri, textField.tci, textField.tri, textField.tci);
					Main.table.setClickedLabelIndices(textField.tri, textField.tci);
					Main.table.markSelectedCells();
					textField.myGrid.requestFocus();
				}

			} else if (event.getCode() == KeyCode.ESCAPE) {
				// System.out.println("esc");
				event.consume();
				textField.myGrid.requestFocus();
			}

		});

		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			// System.out.println(runningScene.getFocusOwner());
			if (!newValue) {
				// System.out.println("Focus lost from TextField");
				Format oldFormat = Main.table.getCell(textField.tri, textField.tci).getFormat();
				try {
					// promena vrednosti celije u tabeli
					Main.getTable().setCell(textField.tri, textField.tci,
							new Cell(textField.getText(), oldFormat, textField.tri, textField.tci));
				} catch (FormatChangeUnsuccessful e) {
					GUI.printlnLog("Upisana vrednost ne odgovara formatu celije");
					// e.printStackTrace();
				}
				Main.table.updateLabels();
				// menjanje textField-a labelom
				CellLabel lab = Main.table.getLabel(textField.tri, textField.tci);
				textField.myGrid.getChildren().remove(textField);
				textField.myGrid.getChildren().add(lab);

				// lab.requestFocus();
				Main.table.getClickedLabel().requestFocus();
			}
		});
	}

}
