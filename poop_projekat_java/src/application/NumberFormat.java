package application;

import java.util.Locale;
import java.util.regex.Pattern;

public class NumberFormat implements Format {
	private int decimalsToShow;

	public NumberFormat(int decimals) {
		this.decimalsToShow = decimals;
	}

	public NumberFormat() {
		decimalsToShow = 2;
	}

	public int getDecimalsToShow() {
		return decimalsToShow;
	}

	public void setDecimalsToShow(int decimalsToShow) {
		this.decimalsToShow = decimalsToShow;
	}

	@Override
	public String getDescription() {
		return "N";
	}

	@Override
	public boolean stringFitsFormat(String string) {
		if (string.equals("") || string.charAt(0) == '=')
			return true;
		String numberPattern = "[-+]?(\\d+\\.?\\d*|\\.\\d+)";
		Pattern pattern = Pattern.compile(numberPattern);
		return pattern.matcher(string).matches();
	}

	@Override
	public String formattedValue(String value) {
		if (value.equals("")) {
			return "";
		}
		if(value.equals("ERROR")) {
			return value;
		}
		double doubleValue = 0;
		try {
			doubleValue = Double.parseDouble(value);
		}
		catch(NumberFormatException ex) {
			System.out.println("greska u konverziji");
			System.out.println("|"+value+"|");
		}
		String pattern = "%." + decimalsToShow + "f";
		return String.format(Locale.US, pattern, doubleValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NumberFormat other = (NumberFormat) obj;
		return decimalsToShow == other.decimalsToShow;
	}

}
