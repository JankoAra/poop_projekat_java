package application;

public interface Format {
	String getDescription();
	
	boolean stringFitsFormat(String string);
	
	String formattedValue(String value);
	
	static Format makeFormat(String desc) {
		if(desc.equals("T")) {
			return new TextFormat();
		}
		else if(desc.equals("N")) {
			return new NumberFormat();
		}
		else if(desc.equals("D")) {
			return new DateFormat();
		}
		else return null;
	}
}
