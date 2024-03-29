package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Predstavlja podatke potrebne da se serijalizuje tabela u JSON formatu.
 * @author janko
 *
 */
public class JsonMappedData {
	public static class MetaCell {
		private int row;
		private int column;
		private String value;
		private String format;
		private int decimals;

		public MetaCell() {
		}

		public MetaCell(int row, int column, String value, String format, int decimals) {
			super();
			this.row = row;
			this.column = column;
			this.value = value;
			this.format = format;
			this.decimals = decimals;
		}

		public int getRow() { return row; }

		public void setRow(int row) { this.row = row; }

		public int getColumn() { return column; }

		public void setColumn(int column) { this.column = column; }

		public String getValue() { return value; }

		public void setValue(String value) { this.value = value; }

		public String getFormat() { return format; }

		public void setFormat(String format) { this.format = format; }

		public int getDecimals() { return decimals; }

		public void setDecimals(int decimals) { this.decimals = decimals; }
	}

	private List<String> globalColumnFormats;
	private List<String> globalColumnDecimals;
	private List<MetaCell> cells;

	public JsonMappedData() {
	}

	/**
	 * Stvara podatke za serijalizaciju na osnovu zadate tabele.
	 * @param table - Tabela koja se serijalizuje.
	 */
	public JsonMappedData(Table table) {
		globalColumnFormats = new ArrayList<String>(Collections.nCopies(26, "T"));
		globalColumnDecimals = new ArrayList<String>(Collections.nCopies(26, "2"));
		cells = new LinkedList<MetaCell>();
		for (int i = 0; i < table.getNumOfRows(); i++) {
			for (int j = 0; j < Table.NUMBER_OF_COLUMNS; j++) {
				Cell tc = table.getCell(i, j);
				Format f = tc.getFormat();
				boolean isNumber = f.getDescription().equals("N");
				int decimals = (isNumber ? ((NumberFormat) f).getDecimalsToShow() : -1);
				MetaCell mc = new MetaCell(i, j, tc.getValue(), tc.getFormat().getDescription(), decimals);
				cells.add(mc);
			}
		}
	}

	public List<String> getGlobalColumnFormats() { return globalColumnFormats; }

	public void setGlobalColumnFormats(List<String> globalColumnFormats) {
		this.globalColumnFormats = globalColumnFormats;
	}

	public List<String> getGlobalColumnDecimals() { return globalColumnDecimals; }

	public void setGlobalColumnDecimals(List<String> globalColumnDecimals) {
		this.globalColumnDecimals = globalColumnDecimals;
	}

	public List<MetaCell> getCells() { return cells; }

	public void setCells(List<MetaCell> cells) { this.cells = cells; }
}
