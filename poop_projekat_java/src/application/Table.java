package application;

import java.util.ArrayList;

public class Table {
	ArrayList<ArrayList<Cell>> data = new ArrayList<ArrayList<Cell>>();
	public static final int numOfCols = 26;

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
		for (int x = 0; x < numOfCols; x++) {
			data.get(data.size() - 1).add(new Cell());
		}
	}

	public void addRow(int index) {
		// TODO ovo
	}

	public void removeRow(int index) {
		data.remove(index);
	}

	public void setCell(int x, int y, Cell newCell) {
		if (x < 0 || x >= getNumOfRows() || y < 0 || y > 25) {
			System.out.println("Nepostojeca celija (" + x + "," + y + ")");
			return;
		}
		data.get(x).set(y, newCell);
	}
	
	public Cell getCell(int row, int col) {
		return data.get(row).get(col);
	}

	public int getNumOfColumns() {
		return data.get(0).size();
	}

	public int getNumOfRows() {
		return data.size();
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
		for (ArrayList l : data) {
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
		Table t = new Table(7);
		t.setCell(0, 4, new Cell("janko"));
		t.setCell(3, 4, new Cell("123.2"));
		t.removeRow(2);
		System.out.println(t);

	}
}
