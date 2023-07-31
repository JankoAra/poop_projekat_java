package application;

import application.MyExceptions.FormatChangeUnsuccessful;

public class Cell {
	private String value = "";

	private Format format;

	public static final TextFormat TEXT_FORMAT = new TextFormat();
	public static final DateFormat DATE_FORMAT = new DateFormat();
	//public static final NumberFormat NUMBER_FORMAT_DEFAULT = new NumberFormat(2);

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
		if(format.getDescription().equals("N")) {
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
			System.out.println("Promena formata nije uspela");
			return;
		}
		Main.table.setCell(rowIndex, colIndex, newCell);
//		Main.table.getLabel(rowIndex, colIndex).getStyleClass().clear();
//		Main.table.getLabel(rowIndex, colIndex).getStyleClass().add("number-label");
		//GUI.rebuildGrid();
	}

}
