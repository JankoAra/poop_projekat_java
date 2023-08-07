package application;

import application.GUI.UpdateType;
import application.UndoRedoStack.ActionType;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;

public class RowLabel extends Label {

	private class RightClickMenu extends ContextMenu {
		public RightClickMenu(RowLabel label) {
			super();
			MenuItem addRowAboveItem = new MenuItem("Add row above");
			MenuItem addRowBelowItem = new MenuItem("Add row below");
			MenuItem deleteRowItem = new MenuItem("Delete row");

			addRowAboveItem.setOnAction(e -> {
				int ri = GridPane.getRowIndex(label);
				UndoRedoStack.clearRedoStack();
				UndoRedoStack.undoStackType.push(ActionType.ROW_ADDED);
				UndoRedoStack.undoStackNumber.push(ri - 1);
				Main.table.addRow(ri - 1);
				GUI.updateGUI(UpdateType.TABLE_CHANGE);
			});
			addRowBelowItem.setOnAction(e -> {
				int ri = GridPane.getRowIndex(label);
				UndoRedoStack.clearRedoStack();
				UndoRedoStack.undoStackType.push(ActionType.ROW_ADDED);
				UndoRedoStack.undoStackNumber.push(ri);
				Main.table.addRow(ri);
				GUI.updateGUI(UpdateType.TABLE_CHANGE);
			});
			deleteRowItem.setOnAction(e -> {
				int ri = GridPane.getRowIndex(label);
				UndoRedoStack.clearRedoStack();
				UndoRedoStack.undoStackType.push(ActionType.ROW_DELETED);
				UndoRedoStack.undoStackNumber.push(ri - 1);
				for (int i = 0; i < Table.numOfCols; i++) {
					Cell c = Main.table.getCell(ri - 1, i);
					UndoRedoStack.undoStackCells.push(c);
				}
				Main.table.deleteRow(ri - 1);
				GUI.updateGUI(UpdateType.TABLE_CHANGE);
			});

			this.getItems().addAll(addRowAboveItem, addRowBelowItem, deleteRowItem);
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
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		label.setOnMouseClicked(e -> {
			int ri = GridPane.getRowIndex(label);
			int tri = ri - 1;
			if (e.getButton() == MouseButton.PRIMARY) {
				Main.table.clearClickedLabelIndices();
				Main.table.demarkSelectedCells();
				if (e.isControlDown()) {
					Main.table.addToSelectedRange(tri, 0, tri, Table.numOfCols - 1);
				} else {
					Main.table.setSelectedRange(tri, 0, tri, Table.numOfCols - 1);
				}

				Main.table.markSelectedCells();
			} else if (e.getButton() == MouseButton.SECONDARY) {
				label.optionsMenu.show(label, e.getScreenX(), e.getScreenY());
			}

		});
		label.setOnDragDetected(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				int ri = GridPane.getRowIndex(label);
				int tri = ri - 1;
				Dragboard dragboard = label.startDragAndDrop(TransferMode.ANY);
				ClipboardContent content = new ClipboardContent();
				String ctrlHeld = e.isControlDown() ? "add" : "set";
				content.putString("row" + "," + ctrlHeld + "," + tri);
				dragboard.setContent(content);
			}

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
					String[] parts = draggedText.split(",");
					if (!parts[0].equals("row")) {
						return;
					}
					try {
						int startIndex = Integer.parseInt(parts[2]);
						int minRow = Math.min(startIndex, tri);
						int maxRow = Math.max(startIndex, tri);
						Main.table.clearClickedLabelIndices();
						Main.table.demarkSelectedCells();
						if(parts[1].equals("add")) {
							Main.table.addToSelectedRange(minRow, 0, maxRow, Table.numOfCols - 1);
						}
						else {
							Main.table.setSelectedRange(minRow, 0, maxRow, Table.numOfCols - 1);
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
