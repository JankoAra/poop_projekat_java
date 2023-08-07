package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;

public class Table {
	ArrayList<ArrayList<Cell>> data = new ArrayList<ArrayList<Cell>>();
	ArrayList<ArrayList<CellLabel>> labels = new ArrayList<ArrayList<CellLabel>>();
	public static final int NUMBER_OF_COLUMNS = 26;
	public static final int DEFAULT_TABLE_SIZE = 10;

	LinkedList<Cell> selectedCells = new LinkedList<>();
	int clickedLabelRowIndex = -1, clickedLabelColumnIndex = -1;

	static ArrayList<ArrayList<String>> calculatedLabels = new ArrayList<ArrayList<String>>();


	public ArrayList<ArrayList<Cell>> getData() {
		return data;
	}

	public Table() {
		addRow();
	}

	public Table(int numOfRows) {
		for (int i = 0; i < numOfRows; i++) {
			addRow();
		}
	}

	public void addRow() {
		data.add(new ArrayList<Cell>());
		labels.add(new ArrayList<CellLabel>());
		for (int x = 0; x < NUMBER_OF_COLUMNS; x++) {
			data.get(data.size() - 1).add(new Cell(this.getNumOfRows() - 1, x));
			labels.get(data.size() - 1).add(new CellLabel());
		}
	}
	public void addRow(int newRowIndex) {
		try {
			ArrayList<Cell> cells = new ArrayList<>();
			ArrayList<CellLabel> newLabels = new ArrayList<>();
			data.add(newRowIndex, cells);
			labels.add(newRowIndex, newLabels);
			for (int x = 0; x < NUMBER_OF_COLUMNS; x++) {
				data.get(newRowIndex).add(new Cell(newRowIndex, x));
				labels.get(newRowIndex).add(new CellLabel());
			}
			for(int i=newRowIndex+1;i<this.getNumOfRows();i++) {
				for(int j=0;j<NUMBER_OF_COLUMNS;j++) {
					Cell oldCell = getCell(i, j);
					oldCell.setRow(oldCell.getRow()+1);
				}
			}
//			UndoRedoStack.undoStackType.push(ActionType.ROW_ADDED);
//			UndoRedoStack.undoStackNumber.push(newRowIndex);
		}
		catch(Exception e) {
			System.out.println("Uhvacen");
		}
		
	}
	
	public void deleteRow(int rowIndex) {
		data.remove(rowIndex);
		labels.remove(rowIndex);
		for (int i = rowIndex; i < this.getNumOfRows(); i++) {
			for (int j = 0; j < Table.NUMBER_OF_COLUMNS; j++) {
				Cell c = Main.table.getCell(i, j);
				c.setRow(c.getRow() - 1);
			}
		}
	}

	public void setCell(int row, int col, Cell newCell) {
		if (row < 0 || row >= getNumOfRows() || col < 0 || col >= Table.NUMBER_OF_COLUMNS) {
			System.out.println("Nepostojeca celija (" + row + "," + col + ")");
			return;
		}
		data.get(row).set(col, newCell);
	}
	
	public void colorLabels() {
		for (int i = 0; i < getNumOfRows(); i++) {
			for (int j = 0; j < Table.NUMBER_OF_COLUMNS; j++) {
				CellLabel label = labels.get(i).get(j);
				if(selectedCells.contains(getCell(i, j))) {
					label.selectLabel();
				}
				else {
					label.deselectLabel();
				}
			}
		}
	}

	public void updateLabels() {
		String resolvedFormulasCsvString = resolveTableFormulas(Parser.convertTableToCSVString(Main.table));
		calculatedLabels = new ArrayList<ArrayList<String>>();
		try {
			BufferedReader reader = new BufferedReader(new StringReader(resolvedFormulasCsvString));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] cells = line.split(",", -1);
				ArrayList<String> row = new ArrayList<String>();
				for (String s : cells) {
					row.add(s);
				}
				calculatedLabels.add(row);
			}
			reader.close();
		} catch (IOException ex) {
			System.out.println("greska");
		}

		for (int i = 0; i < getNumOfRows(); i++) {
			for (int j = 0; j < Table.NUMBER_OF_COLUMNS; j++) {
				CellLabel label = labels.get(i).get(j);
				String text = getCell(i, j).getFormattedValue();
				label.setText(text);
				if(text.equals("ERROR")) {
					if(!label.getStyleClass().contains("error-label")) {
						label.getStyleClass().add("error-label");
					}
				}
				else {
					if(label.getStyleClass().contains("error-label")) {
						label.getStyleClass().remove("error-label");
					}
				}
			}
		}
		//bojenje mora biti zasebno
		//colorLabels();
	}

	public Cell getCell(int row, int col) {
		return data.get(row).get(col);
	}

	public CellLabel getLabel(int row, int col) {
		return labels.get(row).get(col);
	}

	public int getNumOfRows() {
		return data.size();
	}

	public void setSelectedRange(int r1, int c1, int r2, int c2) {
		LinkedList<Cell> newSelectedCells = new LinkedList<>();
		GUI.printLog("\n");
		for (int i = r1; i <= r2; i++) {
			for (int j = c1; j <= c2; j++) {
				newSelectedCells.add(getCell(i, j));
				char chr = (char) (j + 65);
				String s = chr + "" + (i + 1) + ",";
				GUI.printLog(s);
			}
		}
		selectedCells = newSelectedCells;
	}
	
	public void addToSelectedRange(int r1, int c1, int r2, int c2) {
		for(int i=r1;i<=r2;i++) {
			for(int j=c1;j<=c2;j++) {
				Cell cell = getCell(i, j);
				if(!selectedCells.contains(cell)) {
					selectedCells.add(cell);
				}
			}
		}
	}

	public void setClickedLabelIndices(int row, int col) {
		clickedLabelRowIndex = row;
		clickedLabelColumnIndex = col;
	}

	public void clearClickedLabelIndices() {
		setClickedLabelIndices(-1, -1);
	}

	public CellLabel getClickedLabel() {
		return getLabel(clickedLabelRowIndex, clickedLabelColumnIndex);
	}

	public void markSelectedCells() {
		for (Cell c : selectedCells) {
			getLabel(c.getRow(), c.getCol()).selectLabel();
		}
	}

	public void demarkSelectedCells() {
		for (Cell c : selectedCells) {
			getLabel(c.getRow(), c.getCol()).deselectLabel();
		}
	}

//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < numOfCols; i++) {
//			if (i == 0) {
//				sb.append(String.format("%3c ", ' '));
//			}
//			sb.append(String.format("%9c ", 'A' + i));
//		}
//		sb.append("\n");
//		int cnt = 0;
//		for (ArrayList<Cell> l : data) {
//			sb.append(String.format("%3d ", cnt++));
//			for (Object o : l) {
//				Cell c = (Cell) o;
//				String s = c.getValue();
//
//				if (s.equals("")) {
//					s = "-empty- ";
//				} else {
//					s += " ";
//				}
//				s = String.format("%10s", s);
//				sb.append(s);
//
//			}
//
//			sb.append("\n");
//
//		}
//
//		return sb.toString();
//	}

	static {
		System.loadLibrary("POOP_domaci2_jni");
	}

	public native String resolveTableFormulas(String csvTable);

}
