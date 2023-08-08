package application;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
			int gci = GridPane.getColumnIndex(label);
			int tci = gci - 1;
			Main.table.clearClickedLabel();
			Main.table.demarkSelectedCells();
			if (GUI.activeEditingField != null) {
				if (GUI.activeEditingField.getText().startsWith("=")) {
					GUI.activeEditingField.appendText(Cell.tableIndicesToCellRange(-1, tci,-1,tci));
					GUI.activeEditingField.requestFocus();
					GUI.activeEditingField.positionCaret(GUI.activeEditingField.getText().length());
				} else {
					GUI.replaceEditingFieldWithLabel();
					Main.table.setSelectedRange(0, tci, Main.table.getNumOfRows() - 1, tci);
				}
			} else if (e.isControlDown()) {
				Main.table.addToSelectedRange(0, tci, Main.table.getNumOfRows() - 1, tci);
			} else {
				Main.table.setSelectedRange(0, tci, Main.table.getNumOfRows() - 1, tci);
			}
			Main.table.markSelectedCells();
			e.consume();
		});

		label.setOnDragDetected(e -> {
			int gci = GridPane.getColumnIndex(label);
			int tci = gci - 1;
			Dragboard dragboard = label.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			String contentString = "";
			/**
			 * 0 - "column" 1 - startTci 2 - "primary"/"secondary" (mouse button) 3 -
			 * "add"/"set" (ctrl held/not held) 4 - editingField start value / ""(if no
			 * activeEditingField)
			 */
			contentString += "column,";
			contentString += tci + ",";
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
			int gci = GridPane.getColumnIndex(label);
			int tci = gci - 1;
//			double mouseX = e.getSceneX();
//			double mouseY = e.getSceneY();
//			double minY = GUI.rootBorderPane.getCenter().getLayoutY();
//			double maxY = minY + GUI.rootBorderPane.getCenter().getLayoutBounds().getHeight();
//			double minX = GUI.rootBorderPane.getCenter().getLayoutX();
//			double maxX = minX + GUI.rootBorderPane.getCenter().getLayoutBounds().getWidth();
//			System.out.println(mouseX + " " + mouseY + " " + minX + " " + maxX+" "+minY+" "+maxY);
			Main.table.clearClickedLabel();
			if (e.getDragboard().hasString()) {
				e.acceptTransferModes(TransferMode.ANY);
				Dragboard dragboard = e.getDragboard();
				String draggedText = dragboard.getString();
				String[] parts = draggedText.split(",", -1);
				if (parts.length != 5) {
					System.out.println("Greska u pravljenju dragboard-a.");
					return;
				}
				if (!parts[0].equals("column")) {
					return;
				}
				int startIndex = Integer.parseInt(parts[1]);
				int minCol = Math.min(startIndex, tci);
				int maxCol = Math.max(startIndex, tci);
				Main.table.demarkSelectedCells();
				if (GUI.activeEditingField != null) {
					String cellRange = Cell.tableIndicesToCellRange(-1, minCol, -1, maxCol);
					GUI.activeEditingField.setText(parts[4] + cellRange);
					GUI.activeEditingField.requestFocus();
					GUI.activeEditingField.positionCaret(GUI.activeEditingField.getText().length());
					Main.table.setSelectedRange(0, minCol, Main.table.getNumOfRows() - 1, maxCol);
				} else {
					if (parts[3].equals("add")) {
						Main.table.addToSelectedRange(0, minCol, Main.table.getNumOfRows() - 1, maxCol);
					} else {
						Main.table.setSelectedRange(0, minCol, Main.table.getNumOfRows() - 1, maxCol);
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
