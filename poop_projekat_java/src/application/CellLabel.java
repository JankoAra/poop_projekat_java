package application;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

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
		setStyle("-fx-background-color:lightgray;-fx-border-color:black;");
	}

	public void deselectLabel() {
		setStyle("-fx-background-color:white;-fx-border-color:black;");
	}

	private static void initCellLabel(CellLabel label) {
		label.setOnDragDetected(e -> {
			// indeksi u gridu, u tabeli su za 1 manji
			int ri = GridPane.getRowIndex(label);
			int ci = GridPane.getColumnIndex(label);
			Dragboard dragboard = label.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			content.putString(ri + "," + ci);
			dragboard.setContent(content);
			e.consume();
		});
		label.setOnDragEntered(e -> {
			// indeksi u gridu, u tabeli su za 1 manji
			int ri = GridPane.getRowIndex(label);
			int ci = GridPane.getColumnIndex(label);
			Main.table.clearClickedLabelIndices();
			if (/* e.getGestureSource() != label && */ e.getDragboard().hasString()) {
				e.acceptTransferModes(TransferMode.ANY);
				Dragboard dragboard = e.getDragboard();
				if (dragboard.hasString()) {
					String draggedText = dragboard.getString();
					String[] parts = draggedText.split(",");
					if (parts.length == 2) {
						int intValue1 = Integer.parseInt(parts[0]);
						int intValue2 = Integer.parseInt(parts[1]);
						int minRow = Math.min(intValue1 - 1, ri - 1);
						int maxRow = Math.max(intValue1 - 1, ri - 1);
						int minCol = Math.min(intValue2 - 1, ci - 1);
						int maxCol = Math.max(intValue2 - 1, ci - 1);
//						System.out.println("Pocetna celija (" + parts[0] + "," + parts[1] + "), Krajnja celija (" + ri
//								+ "," + ci + ")");
						Main.table.demarkSelectedCells();
						Main.table.setSelectedRange(minRow, minCol, maxRow, maxCol);
						Main.table.markSelectedCells();
						// Handle the integers here
					}
				}
			}
			e.consume();
		});
//		label.setOnDragOver(e -> {
//			// indeksi u gridu, u tabeli su za 1 manji
//			int ri = GridPane.getRowIndex(label);
//			int ci = GridPane.getColumnIndex(label);
//			if (/* e.getGestureSource() != label && */ e.getDragboard().hasString()) {
//				e.acceptTransferModes(TransferMode.ANY);
//			}
//			e.consume();
//		});
//		label.setOnDragDropped(e -> {
//			// indeksi u gridu, u tabeli su za 1 manji
//			int ri = GridPane.getRowIndex(label);
//			int ci = GridPane.getColumnIndex(label);
//			Dragboard dragboard = e.getDragboard();
//			if (dragboard.hasString()) {
//				String draggedText = dragboard.getString();
//				String[] parts = draggedText.split(",");
//				if (parts.length == 2) {
//					int intValue1 = Integer.parseInt(parts[0]);
//					int intValue2 = Integer.parseInt(parts[1]);
//					System.out.println("KRAJ! Pocetna celija (" + parts[0] + "," + parts[1] + "), Krajnja celija (" + ri
//							+ "," + ci + ")");
//					// Handle the integers here
//				}
//			}
//			e.setDropCompleted(true);
//			e.consume();
//		});
		label.setOnMouseClicked(e -> {
			// indeksi u gridu, u tabeli su za 1 manji
			int ri = GridPane.getRowIndex(label);
			int ci = GridPane.getColumnIndex(label);
			int tri = ri - 1;
			int tci = ci - 1;
			if (tri != Main.table.clickedLabelRowIndex || tci != Main.table.clickedLabelColumnIndex) {
				// prvi klik na labelu
				Main.table.demarkSelectedCells();
				Main.table.setSelectedRange(tri, tci, tri, tci);
				Main.table.setClickedLabelIndices(tri, tci);
				Main.table.markSelectedCells();
				label.requestFocus();
			} else {
				// drugi klik na labelu
				GUI.replaceLabelWithTextField(GUI.grid, ri, ci);
			}

			e.consume();
		});
		label.setOnKeyPressed(e -> {
			String pressedCharacter = e.getText();

			int ri = GridPane.getRowIndex(label);
			int ci = GridPane.getColumnIndex(label);
			int tri = ri - 1;
			int tci = ci - 1;
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
			} else if (!pressedCharacter.isEmpty()) {
				System.out.println("ima texta");
				// pritisnut nexi printabilni karakter
				GUI.replaceLabelWithTextFieldWithStartValue(GUI.grid, ri, ci, pressedCharacter);
				e.consume();
			} else {
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
					if (tci + 1 < Table.numOfCols) {
						Main.table.demarkSelectedCells();
						Main.table.setSelectedRange(tri, tci + 1, tri, tci + 1);
						Main.table.setClickedLabelIndices(tri, tci + 1);
						Main.table.markSelectedCells();
						Main.table.getClickedLabel().requestFocus();
					}
					e.consume();
					break;
				case ESCAPE:
					Main.table.demarkSelectedCells();
					Main.table.setSelectedRange(0, 0, 0, 0);
					Main.table.clearClickedLabelIndices();
					GUI.grid.requestFocus();
					e.consume();
					break;
				case DELETE:
					Cell oldCell = Main.table.getCell(tri, tci);
					try {
						Cell newCell = new Cell("", oldCell.getFormat(), tri, tci);
						Main.table.setCell(tri, tci, newCell);
						Main.table.updateLabels();
					} catch (Exception ex) {
					}
					e.consume();
					break;
				default:
					break;
				}

			}
		});
		label.setMinWidth(80);
		label.setStyle("-fx-background-color:white;-fx-border-color:black;");
		label.setFont(new Font("Arial", 20));
		label.setPadding(new Insets(5));
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}

}
