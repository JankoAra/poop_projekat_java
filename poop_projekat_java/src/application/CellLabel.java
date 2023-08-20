package application;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;

/**
 * Labela koja prikazuje sadrzaj neke od celija tabele.
 * @author janko
 *
 */
public class CellLabel extends Label {

	/**
	 * Podrazumevani konstruktor. Stvara labelu sa praznim tekstom i postavljenim osluskivacima.
	 */
	public CellLabel() {
		initCellLabel(this);
	}

	/**
	 * Konstruktor koji stvara labelu sa zadatim tekstom i postavljenim osluskivacima.
	 * @param text - Tekst labele.
	 */
	public CellLabel(String text) {
		super(text);
		initCellLabel(this);
	}

	public CellLabel(String text, Node arg1) {
		super(text, arg1);
		initCellLabel(this);
	}

	/**
	 * Primenjuje stilove labeli kada je selektovana.
	 */
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

	/**
	 * Primenjuje stilove labeli kada nije selektovana.
	 */
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

	/**
	 * Postavlja osluskivace labeli.
	 * @param label - Labela kojoj se postavljaju osluskivaci.
	 */
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
		label.setOnDragOver(new GUI.AutoScrollOnDrag());

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
				// pritisnut nexi printabilni karakter
				if (e.isControlDown()) {
					GUI.printlnLog("Ctrl pritisnut");
				}
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
				default:
					break;
				}

			}
		});
	}

}
