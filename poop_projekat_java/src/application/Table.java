package application;

import java.util.ArrayList;
import java.util.LinkedList;

public class Table {
	ArrayList<ArrayList<Cell>> data = new ArrayList<ArrayList<Cell>>();
	ArrayList<ArrayList<CellLabel>> labels = new ArrayList<ArrayList<CellLabel>>();
	public static final int numOfCols = 26;

	LinkedList<Cell> selectedCells = new LinkedList<>();
	int clickedLabelRowIndex = -1, clickedLabelColumnIndex = -1;

	class Selector {
		public int r1, r2, c1, c2;
	}

	Selector selector = new Selector();

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
		for (int x = 0; x < numOfCols; x++) {
			data.get(data.size() - 1).add(new Cell(this.getNumOfRows() - 1, x));
			labels.get(data.size() - 1).add(new CellLabel());
		}
	}

	public void setCell(int row, int col, Cell newCell) {
		if (row < 0 || row >= getNumOfRows() || col < 0 || col > 25) {
			System.out.println("Nepostojeca celija (" + row + "," + col + ")");
			return;
		}
		data.get(row).set(col, newCell);
		labels.get(row).get(col).setText(newCell.getFormattedValue());
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
		selector.r1 = r1;
		selector.r2 = r2;
		selector.c1 = c1;
		selector.c2 = c2;
	}

	public Selector getSelectedRange() {
		return selector;
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
		for (int i = selector.r1; i <= selector.r2; i++) {
			for (int j = selector.c1; j <= selector.c2; j++) {
				getLabel(i, j).selectLabel();
			}
		}
	}

	public void demarkSelectedCells() {
		for (int i = selector.r1; i <= selector.r2; i++) {
			for (int j = selector.c1; j <= selector.c2; j++) {
				getLabel(i, j).deselectLabel();
			}
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numOfCols; i++) {
			if (i == 0) {
				sb.append(String.format("%3c ", ' '));
			}
			sb.append(String.format("%9c ", 'A' + i));
		}
		sb.append("\n");
		int cnt = 0;
		for (ArrayList<Cell> l : data) {
			sb.append(String.format("%3d ", cnt++));
			for (Object o : l) {
				Cell c = (Cell) o;
				String s = c.getValue();

				if (s.equals("")) {
					s = "-empty- ";
				} else {
					s += " ";
				}
				s = String.format("%10s", s);
				sb.append(s);

			}

			sb.append("\n");

		}

		return sb.toString();
	}

	public static void main(String[] args) {
//		Table t = new Table(7);
//		t.setCell(0, 4, new Cell("janko"));
//		t.setCell(3, 4, new Cell("123.2"));
//		System.out.println(t);

	}
}
