package application;

import application.MyExceptions.FormatChangeUnsuccessful;

public class Cell {
	private String value = "";

	private Format format;

	public static final TextFormat TEXT_FORMAT = new TextFormat();
	public static final DateFormat DATE_FORMAT = new DateFormat();

	private int row, col;

	public Cell(int r, int c) {
		format = TEXT_FORMAT;
		row = r;
		col = c;
	}

	public Cell(String value, Format format, int r, int c) throws FormatChangeUnsuccessful {
		if (format.stringFitsFormat(value) == false)
			throw new FormatChangeUnsuccessful();
		this.value = value;
		this.format = format;
		row = r;
		col = c;
	}

	public Cell(String value, int r, int c) {
		this.value = value;
		this.format = TEXT_FORMAT;
		row = r;
		col = c;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public void setValue(String val) {
		value = val;
	}

	public String getValue() {
		return value;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public String getFormattedValue() {
		String inputVal = value;
		if (format.getDescription().equals("N")) {
			inputVal = Table.calculatedLabels.get(row).get(col);
		}
		return format.formattedValue(inputVal);
	}

	public static void convertCellToFormat(int rowIndex, int colIndex, Format newFormat) {
		Cell oldCell = Main.table.getCell(rowIndex, colIndex);
		if (oldCell.getFormat().equals(newFormat)) {
			// nema promene formata
			return;
		}
		Cell newCell = null;
		try {
			newCell = new Cell(oldCell.value, newFormat, oldCell.getRow(), oldCell.getCol());
		} catch (FormatChangeUnsuccessful e) {
			String cellName = tableIndexToCellName(rowIndex, colIndex);
			GUI.printlnLog(e.getMessage() + " Sadrzaj celije " + cellName + " ne odgovara zeljenom formatu.");
			return;
		}
		Main.table.setCell(rowIndex, colIndex, newCell);
	}

	public static String tableIndexToCellName(int row, int col) {
		char colChar = (char) ('A' + col);
		String rowString = "" + (row + 1);
		if (row < 0) {
			return colChar + "";
		} else if (col < 0) {
			return rowString;
		}
		return colChar + rowString;
	}

	public static String tableIndicesToCellRange(int minRow, int minCol, int maxRow, int maxCol) {
		return tableIndexToCellName(minRow, minCol) + ":" + tableIndexToCellName(maxRow, maxCol);
	}

}
