package application;

public class Cell {
	private String value = "";
	
	private Format format;
	

	public Cell() {
		format = new TextFormat();
	}

	public Cell(String value, Format format) {
		this.value = value;
		this.format = format;
	}
	public Cell(String value) {
		this.value = value;
		this.format = new TextFormat();
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

}
