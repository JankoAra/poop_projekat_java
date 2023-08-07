package application;

import application.GUI.UpdateType;
import application.UndoRedoStack.ActionType;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
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
				for (int i = 0; i < Table.NUMBER_OF_COLUMNS; i++) {
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
			int gri = GridPane.getRowIndex(label);
			int tri = gri - 1;
			Main.table.clearClickedLabelIndices();
			Main.table.demarkSelectedCells();
			if (GUI.activeEditingField != null) {
				if (GUI.activeEditingField.getText().startsWith("=")) {
					GUI.activeEditingField.appendText(Cell.tableIndicesToCellRange(tri, -1, tri, -1));
					GUI.activeEditingField.requestFocus();
					GUI.activeEditingField.positionCaret(GUI.activeEditingField.getText().length());
				} else {
					GUI.replaceEditingFieldWithLabel();
					Main.table.setSelectedRange(tri, 0, tri, Table.NUMBER_OF_COLUMNS - 1);
				}
			} else if (e.getButton() == MouseButton.PRIMARY) {
				if (e.isControlDown()) {
					Main.table.addToSelectedRange(tri, 0, tri, Table.NUMBER_OF_COLUMNS - 1);
				} else {
					Main.table.setSelectedRange(tri, 0, tri, Table.NUMBER_OF_COLUMNS - 1);
				}

			} else if (e.getButton() == MouseButton.SECONDARY) {
				label.optionsMenu.show(label, e.getScreenX(), e.getScreenY());
			}
			Main.table.markSelectedCells();
			e.consume();

		});
		label.setOnDragDetected(e -> {
			int gri = GridPane.getRowIndex(label);
			int tri = gri - 1;
			Dragboard dragboard = label.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			String contentString = "";
			/**
			 * 0 - "row" 1 - startTri 2 - "primary"/"secondary" (mouse button) 3 -
			 * "add"/"set" (ctrl held/not held) 4 - editingField start value / ""(if no
			 * activeEditingField)
			 */
			contentString += "row,";
			contentString += tri + ",";
			if (e.getButton() == MouseButton.PRIMARY) {
				contentString += "primary,";
			} else if (e.getButton() == MouseButton.SECONDARY) {
				contentString += "secondary,";
			}
			if (e.isControlDown()) {
				contentString += "add,";
			} else {
				contentString += "set,";
			}
			if (GUI.activeEditingField != null) {
				if (GUI.activeEditingField.getText().startsWith("=")) {
					contentString += GUI.activeEditingField.getText();
				} else {
					GUI.replaceEditingFieldWithLabel();
//					Main.table.demarkSelectedCells();
//					Main.table.clearClickedLabelIndices();
//					Main.table.setSelectedRange(tri, tci, tri, tci);
//					Main.table.markSelectedCells();
				}
			}
			content.putString(contentString);
			dragboard.setContent(content);
			e.consume();
		});
		label.setOnDragEntered(e -> {
			int gri = GridPane.getRowIndex(label);
			int tri = gri - 1;
//			double mouseX = e.getSceneX();
//			double mouseY = e.getSceneY();
//			double minY = GUI.rootBorderPane.getCenter().getLayoutY();
//			double maxY = minY + GUI.rootBorderPane.getCenter().getLayoutBounds().getHeight();
//			double minX = GUI.rootBorderPane.getCenter().getLayoutX();
//			double maxX = minX + GUI.rootBorderPane.getCenter().getLayoutBounds().getWidth();
//			System.out.println(mouseX + " " + mouseY + " " + minX + " " + maxX+" "+minY+" "+maxY);
			Main.table.clearClickedLabelIndices();
			if (e.getDragboard().hasString()) {
				e.acceptTransferModes(TransferMode.ANY);
				Dragboard dragboard = e.getDragboard();
				String draggedText = dragboard.getString();
				String[] parts = draggedText.split(",", -1);
				if (parts.length != 5) {
					System.out.println("Greska u pravljenju dragboard-a.");
					return;
				}
				if (!parts[0].equals("row")) {
					return;
				}
				int startIndex = Integer.parseInt(parts[1]);
				int minRow = Math.min(startIndex, tri);
				int maxRow = Math.max(startIndex, tri);
				Main.table.demarkSelectedCells();
				if (GUI.activeEditingField != null) {
					String cellRange = Cell.tableIndicesToCellRange(minRow, -1, maxRow, -1);
					GUI.activeEditingField.setText(parts[4] + cellRange);
					GUI.activeEditingField.requestFocus();
					GUI.activeEditingField.positionCaret(GUI.activeEditingField.getText().length());
					Main.table.setSelectedRange(minRow, 0, maxRow, Table.NUMBER_OF_COLUMNS - 1);
				} else {
					if (parts[1].equals("add")) {
						Main.table.addToSelectedRange(minRow, 0, maxRow, Table.NUMBER_OF_COLUMNS - 1);
					} else {
						Main.table.setSelectedRange(minRow, 0, maxRow, Table.NUMBER_OF_COLUMNS - 1);
					}
				}

				Main.table.markSelectedCells();

			}
			e.consume();
		});
		label.setOnDragOver(e -> {
			double mouseX = e.getSceneX();
			double mouseY = e.getSceneY();
			double minY = GUI.rootBorderPane.getCenter().getLayoutY();
			double maxY = minY + GUI.rootBorderPane.getCenter().getLayoutBounds().getHeight();
			double minX = GUI.rootBorderPane.getCenter().getLayoutX();
			double maxX = minX + GUI.rootBorderPane.getCenter().getLayoutBounds().getWidth();
			//System.out.println(mouseX + " " + mouseY + " " + minX + " " + maxX+" "+minY+" "+maxY);
			double deltaX = 50;
			double deltaY = 50;
			double moveX = 0.05;
			double moveY = 0.1;
			ScrollPane sp = GUI.gridScrollPane;
			if(mouseX-minX<deltaX) {
				sp.setHvalue(sp.getHvalue()-moveX);
			}
			else if(maxX-mouseX<deltaX) {
				sp.setHvalue(sp.getHvalue()+moveX);
			}
			if(mouseY-minY<deltaY) {
				sp.setVvalue(sp.getVvalue()-moveY);
			}
			else if(maxY-mouseY<deltaY) {
				sp.setVvalue(sp.getVvalue()+moveY);
			}
			e.consume();
		});
	}
}
