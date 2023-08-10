package application;

import application.MyExceptions.FormatChangeUnsuccessful;

public class Cell {
	private String value = "";

	private Format format;

	public static final TextFormat TEXT_FORMAT = new TextFormat();
	public static final DateFormat DATE_FORMAT = new DateFormat();

	private int row, col;

	/**
	 * Konstruktor celije. Stvara praznu celiju tekstualnog formata.
	 * @param r - Indeks reda celije u tabeli.
	 * @param c - Indeks kolone celije u tabeli.
	 */
	public Cell(int r, int c) {
		format = TEXT_FORMAT;
		row = r;
		col = c;
	}

	/**
	 * Konstruktor celije. Stvara celiju sa zadatim vrednostima.
	 * @param value - Sadrzaj celije.
	 * @param format - Format celije.
	 * @param r - Indeks reda celije u tabeli.
	 * @param c - Indeks kolone celije u tabeli.
	 * @throws FormatChangeUnsuccessful Baca izuzetak ako sadrzaj ne odgovara formatu.
	 */
	public Cell(String value, Format format, int r, int c) throws FormatChangeUnsuccessful {
		if (format.stringFitsFormat(value) == false) throw new FormatChangeUnsuccessful();
		this.value = value;
		this.format = format;
		row = r;
		col = c;
	}

	/**
	 * Konstruktor celije. Stvara celiju tekstualnog formata sa zadatim sadrzajem.
	 * @param value - Sadrzaj celije.
	 * @param r - Indeks reda celije u tabeli.
	 * @param c - Indeks kolone celije u tabeli.
	 */
	public Cell(String value, int r, int c) {
		this.value = value;
		this.format = TEXT_FORMAT;
		row = r;
		col = c;
	}

	/**
	 * @return Vraca indeks reda celije u tabeli.
	 */
	public int getRow() { return row; }

	/**
	 * Postavlja indeks reda celije u tabeli.
	 * @param row - Indeks reda.
	 */
	public void setRow(int row) { this.row = row; }

	/**
	 * @return Vraca indeks kolone celije u tabeli.
	 */
	public int getCol() { return col; }

	/**
	 * Postavlja indeks kolone celije u tabeli.
	 * @param col - Indeks kolone.
	 */
	public void setCol(int col) { this.col = col; }

	/**
	 * Postavlja sadrzaj celije.
	 * @param val - Sadrzaj koji se postavlja.
	 */
	public void setValue(String val) { value = val; }

	/**
	 * @return Vraca sadrzaj celije.
	 */
	public String getValue() { return value; }

	/**
	 * @return Vraca format celije.
	 */
	public Format getFormat() { return format; }

	/**
	 * Postavlja format celije.
	 * @param format - Fomrat koji se postavlja
	 */
	public void setFormat(Format format) { this.format = format; }

	/**
	 * @return Vraca sadrzaj celije formatiran u celijinom formatu.
	 */
	public String getFormattedValue() {
		String inputVal = value;
		if (format.getDescription().equals("N")) {
			inputVal = Main.table.calculatedLabels.get(row).get(col);
		}
		return format.formattedValue(inputVal);
	}

	/**
	 * Menja format celije na zadatim indeksima u zadati format.
	 * Ako je novi format jednak starom formatu, nista se ne menja.
	 * Ako postojeci sadrzaj celije ne odgovara novom formatu, nista se ne menja i ispisuje se poruka o gresci.
	 * @param rowIndex - Indeks reda celije u tabeli.
	 * @param colIndex - Indeks kolone celije u tabeli.
	 * @param newFormat - Format koji se postavlja.
	 */
	public static void convertCellToFormat(int rowIndex, int colIndex, Format newFormat) {
		Cell oldCell = Main.table.getCell(rowIndex, colIndex);
		if (oldCell.getFormat().equals(newFormat)) {
			// nema promene formata
			return;
		}
		Cell newCell = null;
		try {
			newCell = new Cell(oldCell.value, newFormat, oldCell.getRow(), oldCell.getCol());
		}
		catch (FormatChangeUnsuccessful e) {
			String cellName = tableIndexToCellName(rowIndex, colIndex);
			GUI.printlnLog(e.getMessage() + " Sadrzaj celije " + cellName + " ne odgovara zeljenom formatu.");
			return;
		}
		Main.table.setCell(rowIndex, colIndex, newCell);
	}

	/**
	 * Vraca ime celije (npr. A5, B10) na osnovu zadatih indeksa.
	 * Ukoliko je indeks reda negativan, vraca slovo koje oznacava kolonu.
	 * Ukoliko je indeks kolone negativan, vraca redni broj reda.
	 * Pretpostavka je da su indeksi u odgovarajucem opsegu, kao i da nisu oba negativna.
	 * @param row - Indeks reda celije u tabeli.
	 * @param col - Indeks kolone celije u tabeli.
	 * @return String koji predstavlja ime celije
	 */
	public static String tableIndexToCellName(int row, int col) {
		char colChar = (char) ('A' + col);
		String rowString = "" + (row + 1);
		if (row < 0) {
			return colChar + "";
		}
		else if (col < 0) {
			return rowString;
		}
		return colChar + rowString;
	}

	/**
	 * Vraca String koji predstavlja opseg celija (npr. A1:B5)
	 * @param minRow - Najmanji indeks reda opsega.
	 * @param minCol - Najmanji indeks kolone opsega.
	 * @param maxRow - Najveci indeks reda opsega.
	 * @param maxCol - Najveci indeks kolone opsega.
	 * @return String koji predstavlja opseg od gornje leve do donje desne celije.
	 */
	public static String tableIndicesToCellRange(int minRow, int minCol, int maxRow, int maxCol) {
		return tableIndexToCellName(minRow, minCol) + ":" + tableIndexToCellName(maxRow, maxCol);
	}

}
