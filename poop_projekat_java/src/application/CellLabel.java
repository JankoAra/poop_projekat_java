package application;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;

public class CellLabel extends Label {

	public CellLabel() {
		initCellLabel(this);
	}

	public CellLabel(String arg0) {
		super(arg0);
		initCellLabel(this);
	}

	public CellLabel(String arg0, Node arg1) {
		super(arg0, arg1);
		initCellLabel(this);
	}

	public void selectLabel() {
		int ri = GridPane.getRowIndex(this);
		int ci = GridPane.getColumnIndex(this);
		int tri = ri - 1;
		int tci = ci - 1;
		Format f = Main.table.getCell(tri, tci).getFormat();
		getStyleClass().clear();
		switch (f.getDescription()) {
		case "N":
			getStyleClass().add("selected-number");
			if (getText().equals("ERROR")) {
				getStyleClass().add("error-label");
			}
			break;
		case "D":
			getStyleClass().add("selected-date");
			break;
		default:
			getStyleClass().add("selected-label");
			break;
		}
	}

	public void deselectLabel() {
		int ri = GridPane.getRowIndex(this);
		int ci = GridPane.getColumnIndex(this);
		int tri = ri - 1;
		int tci = ci - 1;
		Format f = Main.table.getCell(tri, tci).getFormat();
		getStyleClass().clear();
		switch (f.getDescription()) {
		case "N":
			getStyleClass().add("number-label");
			if (getText().equals("ERROR")) {
				getStyleClass().add("error-label");
			}
			break;
		case "D":
			getStyleClass().add("date-label");
			break;
		default:
			getStyleClass().add("default-label");
			break;
		}
	}

	private static void initCellLabel(CellLabel label) {
		label.setMinWidth(80);
		label.getStyleClass().add("default-label");
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		label.setOnMouseClicked(e -> {
			int gri = GridPane.getRowIndex(label);
			int gci = GridPane.getColumnIndex(label);
			int tri = gri - 1;
			int tci = gci - 1;
			if (GUI.activeEditingField != null) {
				if (GUI.activeEditingField.getText().startsWith("=")) {
					GUI.activeEditingField.appendText(Cell.tableIndexToCellName(tri, tci));
					GUI.activeEditingField.requestFocus();
					GUI.activeEditingField.positionCaret(GUI.activeEditingField.getText().length());
				}
				else {
					Main.table.demarkSelectedCells();
					GUI.replaceEditingFieldWithLabel();
					Main.table.setClickedLabelIndices(tri, tci);
					Main.table.setSelectedRange(tri, tci, tri, tci);
					Main.table.markSelectedCells();
					Main.table.getClickedLabel().requestFocus();
				}
			}
			else if (tri != Main.table.clickedLabelRowIndex || tci != Main.table.clickedLabelColumnIndex) {
				// prvi klik na labelu
				Main.table.demarkSelectedCells();
				if (e.isControlDown()) {
					Main.table.addToSelectedRange(tri, tci, tri, tci);
					Main.table.clearClickedLabel();
				}
				else {
					Main.table.setSelectedRange(tri, tci, tri, tci);
					Main.table.setClickedLabelIndices(tri, tci);
				}

				Main.table.markSelectedCells();
				label.requestFocus();
			}
			else {
				// drugi klik na labelu
				GUI.replaceLabelWithEditingField(GUI.grid, gri, gci, null);
			}
			e.consume();
		});
		label.setOnDragDetected(e -> {
			int gri = GridPane.getRowIndex(label);
			int gci = GridPane.getColumnIndex(label);
			int tri = gri - 1;
			int tci = gci - 1;
			Dragboard dragboard = label.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			String contentString = "";
			/**
			 * 0 - startTri
			 * 1 - startTci
			 * 2 - "primary"/"secondary" (mouse button)
			 * 3 - "add"/"set" (ctrl held/not held)
			 * 4 - editingField start value / ""(if no activeEditingField)
			 */
			contentString += tri + ",";
			contentString += tci + ",";
			if (e.getButton() == MouseButton.PRIMARY) {
				contentString += "primary,";
			}
			else if (e.getButton() == MouseButton.SECONDARY) {
				contentString += "secondary,";
			}
			if (e.isControlDown()) {
				contentString += "add,";
			}
			else {
				contentString += "set,";
			}
			if (GUI.activeEditingField != null) {
				if (GUI.activeEditingField.getText().startsWith("=")) {
					contentString += GUI.activeEditingField.getText();
				}
				else {
					GUI.replaceEditingFieldWithLabel();
					Main.table.demarkSelectedCells();
					Main.table.setClickedLabelIndices(tri, tci);
					Main.table.setSelectedRange(tri, tci, tri, tci);
					Main.table.markSelectedCells();
				}
			}
			content.putString(contentString);
			dragboard.setContent(content);
			e.consume();
		});
		label.setOnDragEntered(e -> {
			int gri = GridPane.getRowIndex(label);
			int gci = GridPane.getColumnIndex(label);
			int tri = gri - 1;
			int tci = gci - 1;
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
				int startTri;
				int startTci;
				try {
					startTri = Integer.parseInt(parts[0]);
					startTci = Integer.parseInt(parts[1]);
				}
				catch (NumberFormatException ex) {
					return;
				}
				int minRow = Math.min(startTri, tri);
				int maxRow = Math.max(startTri, tri);
				int minCol = Math.min(startTci, tci);
				int maxCol = Math.max(startTci, tci);
				Main.table.demarkSelectedCells();
				if (GUI.activeEditingField != null) {
					String cellRange = Cell.tableIndicesToCellRange(minRow, minCol, maxRow, maxCol);
					GUI.activeEditingField.setText(parts[4] + cellRange);
					GUI.activeEditingField.requestFocus();
					GUI.activeEditingField.positionCaret(GUI.activeEditingField.getText().length());
					Main.table.setSelectedRange(minRow, minCol, maxRow, maxCol);
					Main.table.clearClickedLabel();
				}
				else {
					if (parts[3].equals("set")) {
						Main.table.setSelectedRange(minRow, minCol, maxRow, maxCol);
						if (startTci == tci && startTri == tri) {
							Main.table.setClickedLabelIndices(tri, tci);
							Main.table.getClickedLabel().requestFocus();
						}
						else {
							Main.table.clearClickedLabel();
						}
					}
					else {
						Main.table.addToSelectedRange(minRow, minCol, maxRow, maxCol);
						Main.table.clearClickedLabel();
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
			// System.out.println(mouseX + " " + mouseY + " " + minX + " " + maxX+" "+minY+"
			// "+maxY);
			double deltaX = 50;
			double deltaY = 50;
			double moveX = 0.05;
			double moveY = 0.1;
			ScrollPane sp = GUI.gridScrollPane;
			if (mouseX - minX < deltaX) {
				sp.setHvalue(sp.getHvalue() - moveX);
			}
			else if (maxX - mouseX < deltaX) {
				sp.setHvalue(sp.getHvalue() + moveX);
			}
			if (mouseY - minY < deltaY) {
				sp.setVvalue(sp.getVvalue() - moveY);
			}
			else if (maxY - mouseY < deltaY) {
				sp.setVvalue(sp.getVvalue() + moveY);
			}
			e.consume();
		});

		label.setOnKeyPressed(e -> {
			String pressedCharacter = e.getText();
			int gri = GridPane.getRowIndex(label);
			int gci = GridPane.getColumnIndex(label);
			int tri = gri - 1;
			int tci = gci - 1;
			if (e.getCode() == KeyCode.ENTER) {
				if (tri == Main.table.clickedLabelRowIndex && tci == Main.table.clickedLabelColumnIndex) {
					if (tri + 1 < Main.table.getNumOfRows()) {
						Main.table.demarkSelectedCells();
						Main.table.setSelectedRange(tri + 1, tci, tri + 1, tci);
						Main.table.setClickedLabelIndices(tri + 1, tci);
						Main.table.markSelectedCells();
						Main.table.getClickedLabel().requestFocus();
					}
					e.consume();
				}
			}
			else if (!pressedCharacter.isEmpty()) {
				// System.out.println("ima texta");
				// pritisnut nexi printabilni karakter
				if (tri == Main.table.clickedLabelRowIndex && tci == Main.table.clickedLabelColumnIndex) {
					GUI.replaceLabelWithEditingField(GUI.grid, gri, gci, pressedCharacter);
					e.consume();
				}

			}
			else {
				KeyCode keycode = e.getCode();
				switch (keycode) {
				case UP:
					if (tri > 0) {
						Main.table.demarkSelectedCells();
						Main.table.setSelectedRange(tri - 1, tci, tri - 1, tci);
						Main.table.setClickedLabelIndices(tri - 1, tci);
						Main.table.markSelectedCells();
						Main.table.getClickedLabel().requestFocus();
					}
					e.consume();
					break;
				case DOWN:
					if (tri + 1 < Main.table.getNumOfRows()) {
						Main.table.demarkSelectedCells();
						Main.table.setSelectedRange(tri + 1, tci, tri + 1, tci);
						Main.table.setClickedLabelIndices(tri + 1, tci);
						Main.table.markSelectedCells();
						Main.table.getClickedLabel().requestFocus();
					}
					e.consume();
					break;
				case LEFT:
					if (tci > 0) {
						Main.table.demarkSelectedCells();
						Main.table.setSelectedRange(tri, tci - 1, tri, tci - 1);
						Main.table.setClickedLabelIndices(tri, tci - 1);
						Main.table.markSelectedCells();
						Main.table.getClickedLabel().requestFocus();
					}
					e.consume();
					break;
				case RIGHT:
					if (tci + 1 < Table.NUMBER_OF_COLUMNS) {
						Main.table.demarkSelectedCells();
						Main.table.setSelectedRange(tri, tci + 1, tri, tci + 1);
						Main.table.setClickedLabelIndices(tri, tci + 1);
						Main.table.markSelectedCells();
						Main.table.getClickedLabel().requestFocus();
					}
					e.consume();
					break;
				//				case ESCAPE:
				//					Main.table.demarkSelectedCells();
				//					Main.table.setSelectedRange(0, 0, 0, 0);
				//					Main.table.clearClickedLabelIndices();
				//					GUI.grid.requestFocus();
				//					e.consume();
				//					break;
				//				case DELETE:
				//					Cell oldCell = Main.table.getCell(tri, tci);
				//					try {
				//						Cell newCell = new Cell("", oldCell.getFormat(), tri, tci);
				//						Main.table.setCell(tri, tci, newCell);
				//						Main.table.updateLabels();
				//					} catch (Exception ex) {
				//					}
				//					e.consume();
				//					break;
				default:
					break;
				}

			}
		});
	}

}
