package application;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;

public class ColumnLabel extends Label {

	public ColumnLabel() {
		initColumnLabel(this);
	}

	public ColumnLabel(String arg0) {
		super(arg0);
		initColumnLabel(this);
	}

	public ColumnLabel(String arg0, Node arg1) {
		super(arg0, arg1);
		initColumnLabel(this);
	}

	private static void initColumnLabel(ColumnLabel label) {
		label.setMinWidth(80);
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		label.setOnMouseClicked(e -> {
			int ci = GridPane.getColumnIndex(label);
			int tci = ci - 1;
			Main.table.clearClickedLabelIndices();
			Main.table.demarkSelectedCells();
			if (e.isControlDown()) {
				Main.table.addToSelectedRange(0, tci, Main.table.getNumOfRows() - 1, tci);
			} else {
				Main.table.setSelectedRange(0, tci, Main.table.getNumOfRows() - 1, tci);
			}
			Main.table.markSelectedCells();
		});
		label.setOnDragDetected(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				int ci = GridPane.getColumnIndex(label);
				int tci = ci - 1;
				Dragboard dragboard = label.startDragAndDrop(TransferMode.ANY);
				ClipboardContent content = new ClipboardContent();
				String ctrlHeld = e.isControlDown() ? "add" : "set";
				content.putString("column" + "," + ctrlHeld + "," + tci);
				dragboard.setContent(content);
			}

			e.consume();
		});
		label.setOnDragEntered(e -> {
			// indeksi u gridu, u tabeli su za 1 manji
			int ci = GridPane.getColumnIndex(label);
			int tci = ci - 1;
			Main.table.clearClickedLabelIndices();
			if (/* e.getGestureSource() != label && */ e.getDragboard().hasString()) {
				e.acceptTransferModes(TransferMode.ANY);
				Dragboard dragboard = e.getDragboard();
				if (dragboard.hasString()) {
					String draggedText = dragboard.getString();
					String[] parts = draggedText.split(",");
					if (!parts[0].equals("column")) {
						return;
					}
					try {
						int startIndex = Integer.parseInt(parts[2]);
						int minCol = Math.min(startIndex, tci);
						int maxCol = Math.max(startIndex, tci);
						Main.table.demarkSelectedCells();
						if (parts[1].equals("add")) {
							Main.table.addToSelectedRange(0, minCol, Main.table.getNumOfRows() - 1, maxCol);
						} else {
							Main.table.setSelectedRange(0, minCol, Main.table.getNumOfRows() - 1, maxCol);
						}
						Main.table.markSelectedCells();
					} catch (NumberFormatException ex) {
						return;
					}
				}
			}
			e.consume();
		});
	}

}
