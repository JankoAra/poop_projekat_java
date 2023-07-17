package application;

public class Cell {
	private String value = "";

	public Cell() {

	}

	public Cell(String s) {
		value = s;
	}

	public void setValue(String val) {
		value = val;
	}

	public String getValue() {
		return value;
	}

}
