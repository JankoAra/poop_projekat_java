package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;

public class Table {
	// interne liste celija i labela
	private ArrayList<ArrayList<Cell>> data = new ArrayList<ArrayList<Cell>>();
	private ArrayList<ArrayList<CellLabel>> labels = new ArrayList<ArrayList<CellLabel>>();

	// konstante za tabelu
	public static final int NUMBER_OF_COLUMNS = 26;
	public static final int DEFAULT_TABLE_SIZE = 50;

	// za selektovanje celija i prebacivanje fokusa labela
	LinkedList<Cell> selectedCells = new LinkedList<>();
	int clickedLabelRowIndex = -1, clickedLabelColumnIndex = -1;
	CellLabel clickedLabel = null;

	public static class IndexPair {
		public int row;
		public int col;

		public IndexPair(int r, int c) {
			row = r;
			col = c;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			IndexPair other = (IndexPair) obj;
			return col == other.col && row == other.row;
		}
	}

	LinkedList<IndexPair> selectedCellsIndices = new LinkedList<>();

	// vracene vrednosti labela iz JNI metode nakon racunanja formula
	ArrayList<ArrayList<String>> calculatedLabels = new ArrayList<ArrayList<String>>();

	// ucitavanje dll biblioteke
	static {
		System.loadLibrary("POOP_domaci2_jni");
	}

	/**
	 * Prima tabelu u CSV formatu i vraca tabelu u CSV formatu sa izracunatim formulama
	 * @param csvTable - String koji predstavlja glavnu tabelu u CSV formatu
	 * @return Vraca jedan string u CSV formatu koji sadrzi rezultate formula
	 */
	public native String resolveTableFormulas(String csvTable);

	/**
	 * Konstruktor koji stvara praznu tabelu sa jednim redom
	 */
	public Table() {
		addRow();
	}

	/**
	 * Konstruktor koji stvara praznu tabelu sa numOfRows redova
	 * @param numOfRows - Broj redova u novoj tabeli
	 */
	public Table(int numOfRows) {
		for (int i = 0; i < numOfRows; i++) {
			addRow();
		}
	}

	/**
	 * Dodaje jedan prazan red u tabeli kao poslednji
	 */
	public void addRow() {
		data.add(new ArrayList<Cell>());
		labels.add(new ArrayList<CellLabel>());
		for (int x = 0; x < NUMBER_OF_COLUMNS; x++) {
			data.get(data.size() - 1).add(new Cell(this.getNumOfRows() - 1, x));
			labels.get(data.size() - 1).add(new CellLabel());
		}
	}

	/**
	 * Dodaje jedan prazan red u tabeli na indeksu newRowIndex,
	 * redove pocev od newRowIndex pomera jedno mesto udesno
	 * @param newRowIndex - Indeks novog reda u tabeli (prvi indeks je 0)
	 */
	public void addRow(int newRowIndex) {
		ArrayList<Cell> cells = new ArrayList<>();
		ArrayList<CellLabel> newLabels = new ArrayList<>();
		data.add(newRowIndex, cells);
		labels.add(newRowIndex, newLabels);
		for (int x = 0; x < NUMBER_OF_COLUMNS; x++) {
			data.get(newRowIndex).add(new Cell(newRowIndex, x));
			labels.get(newRowIndex).add(new CellLabel());
		}
		for (int i = newRowIndex + 1; i < this.getNumOfRows(); i++) {
			for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
				Cell oldCell = getCell(i, j);
				oldCell.setRow(oldCell.getRow() + 1);
			}
		}
	}

	/**
	 * Brise red sa indeksom rowIndex iz tabele, sve naredne redove pomera jedno mesto ulevo
	 * @param rowIndex - Indeks reda koji se brise (od 0 do getNumOfRows()-1)
	 */
	public void deleteRow(int rowIndex) {
		if (rowIndex < 0 || rowIndex >= getNumOfRows()) {
			return;
		}
		data.remove(rowIndex);
		labels.remove(rowIndex);
		for (int i = rowIndex; i < this.getNumOfRows(); i++) {
			for (int j = 0; j < Table.NUMBER_OF_COLUMNS; j++) {
				Cell c = Main.table.getCell(i, j);
				c.setRow(c.getRow() - 1);
			}
		}
	}

	/**
	 * Ubacuje novu celiju i internu listu celija tabele
	 * @param row - indeks reda celije u tabeli(od 0 do getNumOfRows()-1) 
	 * @param col - indeks kolone celije u tabeli(od 0 do Table.NUMBER_OF_COLUMNS-1)
	 * @param newCell - celija koja se ubacuje u tabelu
	 */
	public void setCell(int row, int col, Cell newCell) {
		if (row < 0 || row >= getNumOfRows() || col < 0 || col >= Table.NUMBER_OF_COLUMNS) {
			System.out.println("Nepostojeca celija (" + row + "," + col + ")");
			return;
		}
		data.get(row).set(col, newCell);

	}

	/**
	 * Primenjuje odgovarajuce stilove labelama u zavisnosti od toga
	 * da li odgovaraju selektovanim ili neselektovanim celijama,
	 * kao i kog su formata odgovarajuce celije
	 */
	public void colorLabels() {
		for (int i = 0; i < getNumOfRows(); i++) {
			for (int j = 0; j < Table.NUMBER_OF_COLUMNS; j++) {
				CellLabel label = labels.get(i).get(j);
				if (selectedCellsIndices.contains(new IndexPair(i, j))) {
					label.selectLabel();
				}
				else {
					label.deselectLabel();
				}
			}
		}
	}

	/**
	 * Vrsi update tekstova labela tabele, tako sto izracuna sve formule pomocu JNI metode,
	 * a zatim svakoj labeli postavi tekst koji odgovara formatiranom sadrzaju odgovarajuce celije
	 */
	public void updateLabels() {
		// racunanje formula zapisanih u tabeli
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
		}
		catch (IOException ex) {
			System.out.println("Greska u citanju rezultata JNI metode.");
		}

		// upisivanje formatiranog teksta u labele
		for (int i = 0; i < getNumOfRows(); i++) {
			for (int j = 0; j < Table.NUMBER_OF_COLUMNS; j++) {
				CellLabel label = labels.get(i).get(j);
				String text = getCell(i, j).getFormattedValue();
				label.setText(text);
			}
		}
	}

	/**
	 * Vraca celiju iz interne liste tabele
	 * @param row - indeks reda celije
	 * @param col - indeks kolone celije
	 * @return Vraca celiju na koordinatama (row,col) iz tabele
	 */
	public Cell getCell(int row, int col) {
		return data.get(row).get(col);
	}

	/**
	 * Vraca labelu iz interne liste tabele.
	 * @param row - indeks reda labele
	 * @param col - indeks kolone labele
	 * @return Vraca labelu koja odgovara celiji na koordinatama (row,col) iz tabele.
	 */
	public CellLabel getLabel(int row, int col) {
		return labels.get(row).get(col);
	}

	/**
	 * @return Vraca broj redova u tabeli
	 */
	public int getNumOfRows() { return data.size(); }

	/**
	 * Selektuje celije kojima je indeks reda i (r1 <= i <= r2)
	 * i indeks kolone j (c1 <= j <= c2). Prethodno selektovane celije prestaju da budu selektovane.
	 * @param r1 - Najmanji indeks reda u opsegu
	 * @param c1 - Najmanji indeks kolone u opsegu
	 * @param r2 - Najveci indeks reda u opsegu
	 * @param c2 - Najveci indeks kolone u opsegu
	 */
	public void setSelectedRange(int r1, int c1, int r2, int c2) {
		LinkedList<Cell> newSelectedCells = new LinkedList<>();
		LinkedList<IndexPair> newSelectedIndices = new LinkedList<>();
		for (int i = r1; i <= r2; i++) {
			for (int j = c1; j <= c2; j++) {
				newSelectedCells.add(getCell(i, j));
				newSelectedIndices.add(new IndexPair(i, j));
			}
		}
		GUI.printlnLog(Cell.tableIndicesToCellRange(r1, c1, r2, c2));
		selectedCells = newSelectedCells;
		selectedCellsIndices = newSelectedIndices;
	}

	/**
	 * Dodaje celije kojima je indeks reda i (r1 <= i <= r2)
	 * i indeks kolone j (c1 <= j <= c2) u listu selektovanih.
	 * @param r1 - Najmanji indeks reda u opsegu
	 * @param c1 - Najmanji indeks kolone u opsegu
	 * @param r2 - Najveci indeks reda u opsegu
	 * @param c2 - Najveci indeks kolone u opsegu
	 */
	public void addToSelectedRange(int r1, int c1, int r2, int c2) {
		for (int i = r1; i <= r2; i++) {
			for (int j = c1; j <= c2; j++) {
				Cell cell = getCell(i, j);
				if (!selectedCells.contains(cell)) {
					selectedCells.add(cell);
				}
				IndexPair pair = new IndexPair(i, j);
				if (!selectedCellsIndices.contains(pair)) {
					selectedCellsIndices.add(pair);
				}
			}
		}
	}

	/**
	 * Prazni listu selektovanih celija (deselektuje sve celije).
	 */
	public void clearSelectedCells() {
		selectedCells = new LinkedList<>();
		selectedCellsIndices = new LinkedList<>();
	}

	/**
	 * Postavlja labelu koja ima fokus
	 * @param row - Indeks reda labele
	 * @param col - Indeks kolone labele
	 */
	public void setClickedLabelIndices(int row, int col) {
		clickedLabelRowIndex = row;
		clickedLabelColumnIndex = col;
		clickedLabel = getLabel(row, col);
	}

	/**
	 * Resetuje labelu koja ima fokus, nijedna labela vise nema fokus
	 */
	public void clearClickedLabel() {
		clickedLabelRowIndex = -1;
		clickedLabelColumnIndex = -1;
		clickedLabel = null;
	}

	/**
	 * @return Vraca labelu koja ima fokus, ili null ako nijedna nema fokus
	 */
	public CellLabel getClickedLabel() { return clickedLabel; }

	/**
	 * Primenjuje odgovarajuci stil za oznacavanje labela koje su selektovane
	 */
	public void markSelectedCells() {
		//		for (Cell c : selectedCells) {
		//			getLabel(c.getRow(), c.getCol()).selectLabel();
		//		}
		for (IndexPair pair : selectedCellsIndices) {
			getLabel(pair.row, pair.col).selectLabel();
		}
	}

	/**
	 * Seletovanim labelama primenjuje default stil
	 */
	public void demarkSelectedCells() {
		//		for (Cell c : selectedCells) {
		//			getLabel(c.getRow(), c.getCol()).deselectLabel();
		//		}
		for (IndexPair pair : selectedCellsIndices) {
			getLabel(pair.row, pair.col).deselectLabel();
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

}
