package application;

public class TextFormat implements Format {
	@Override
	public String getDescription() {
		return "T";
	}

	@Override
	public boolean stringFitsFormat(String string) {
		return true;
	}

	@Override
	public String formattedValue(String value) {
		return value;
	}

}
