package application;

import application.GUI.UpdateType;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class RowLabel extends Label {

	private class RightClickMenu extends ContextMenu {
		public RightClickMenu(RowLabel label) {
			super();
			MenuItem addRowAboveItem = new MenuItem("Add row above");
			MenuItem addRowBelowItem = new MenuItem("Add row below");

			addRowAboveItem.setOnAction(e -> {
				int ri = GridPane.getRowIndex(label);
				Main.table.addRow(ri - 1);
				GUI.updateGUI(UpdateType.TABLE_CHANGE);
			});
			addRowBelowItem.setOnAction(e -> {
				int ri = GridPane.getRowIndex(label);
				Main.table.addRow(ri);
				GUI.updateGUI(UpdateType.TABLE_CHANGE);
			});

			this.getItems().addAll(addRowAboveItem, addRowBelowItem);
		}
	}

	private RightClickMenu optionsMenu = new RightClickMenu(this);

	public RowLabel() {
		initRowLabel(this);
	}

	public RowLabel(String arg0) {
		super(arg0);
		initRowLabel(this);
	}

	public RowLabel(String arg0, Node arg1) {
		super(arg0, arg1);
		initRowLabel(this);
	}

	private static void initRowLabel(RowLabel label) {
		label.setMinWidth(80);
		// label.setStyle("-fx-background-color:white;-fx-border-color:black;-fx-alignment:center;");
		// label.setFont(new Font("Arial", 20));
		// label.setPadding(new Insets(5));
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		label.setOnMouseClicked(e -> {
			int ri = GridPane.getRowIndex(label);
			int tri = ri - 1;
			if (e.getButton() == MouseButton.PRIMARY) {
				Main.table.clearClickedLabelIndices();
				Main.table.demarkSelectedCells();
				Main.table.setSelectedRange(tri, 0, tri, Table.numOfCols - 1);
				Main.table.markSelectedCells();
			} else if (e.getButton() == MouseButton.SECONDARY) {
				label.optionsMenu.show(label, e.getScreenX(), e.getScreenY());
			}

		});
		label.setOnDragDetected(e -> {
			int ri = GridPane.getRowIndex(label);
			int tri = ri - 1;
			Dragboard dragboard = label.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			content.putString("row:" + tri);
			dragboard.setContent(content);
			e.consume();
		});
		label.setOnDragEntered(e -> {
			// indeksi u gridu, u tabeli su za 1 manji
			int ri = GridPane.getRowIndex(label);
			int tri = ri - 1;
			Main.table.clearClickedLabelIndices();
			if (/* e.getGestureSource() != label && */ e.getDragboard().hasString()) {
				e.acceptTransferModes(TransferMode.ANY);
				Dragboard dragboard = e.getDragboard();
				if (dragboard.hasString()) {
					String draggedText = dragboard.getString();
					String[] parts = draggedText.split(":");
					if (!parts[0].equals("row")) {
						return;
					}
					try {
						int startIndex = Integer.parseInt(parts[1]);
						int minRow = Math.min(startIndex, tri);
						int maxRow = Math.max(startIndex, tri);
						Main.table.demarkSelectedCells();
						Main.table.setSelectedRange(minRow, 0, maxRow, Table.numOfCols - 1);
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
