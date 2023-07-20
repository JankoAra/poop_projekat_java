package application;

public interface Format {
	String getDescription();
	
	boolean stringFitsFormat(String string);
	
	String formattedValue(String value);
}
