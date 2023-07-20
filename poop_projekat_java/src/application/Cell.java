package application;

public class Cell {
	private String value = "";

	private Format format;

	public static final TextFormat TEXT_FORMAT = new TextFormat();
	public static final DateFormat DATE_FORMAT = new DateFormat();
	public static final NumberFormat NUMBER_FORMAT_DEFAULT = new NumberFormat(2);
	
	static int selectedCellRow = -1;
	static int selectedCellColumn = -1;

	public Cell() {
		format = TEXT_FORMAT;
	}

	public Cell(String value, Format format) throws FormatChangeUnsuccessful {
		if (format.stringFitsFormat(value) == false)
			throw new FormatChangeUnsuccessful();
		this.value = value;
		this.format = format;
	}

	public Cell(String value) {
		this.value = value;
		this.format = TEXT_FORMAT;
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
		return format.formattedValue(value);
	}

	public static void convertCellToFormat(int rowIndex, int colIndex, Format newFormat) {
		Cell oldCell = Main.table.getCell(rowIndex, colIndex);
		if (oldCell.getFormat().equals(newFormat)) {
			// nema promene formata
			return;
		}
		Cell newCell = null;
		try {
			newCell = new Cell(oldCell.value, newFormat);
		} catch (FormatChangeUnsuccessful e) {
			// TODO: handle exception
			System.out.println("Promena formata nije uspela");
			return;
		}
		Main.table.setCell(rowIndex, colIndex, newCell);
		GUI.repaintGrid();
	}

}
