package application;

import java.text.DecimalFormat;
import java.text.ParseException;
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
		if (string.equals(""))
			return true;
		String numberPattern = "[-+]?(\\d+\\.?\\d*|\\.\\d+)";
		Pattern pattern = Pattern.compile(numberPattern);
		return pattern.matcher(string).matches();
	}

	@Override
	public String formattedValue(String value) {
		if(value.equals("")) return "";
		double doubleValue = Double.parseDouble(value);
		String pattern = "%."+decimalsToShow+"f";
		return String.format(Locale.US,pattern,doubleValue);
		
		
		
		
		
		
		
//		// Create a pattern based on the desired number of decimal places
//		StringBuilder pattern = new StringBuilder("0");
//		if (decimalsToShow > 0) {
//			pattern.append(".");
//			for (int i = 0; i < decimalsToShow; i++) {
//				pattern.append("0");
//			}
//		}
//
//		// Create the DecimalFormat object with the specified pattern
//		DecimalFormat decimalFormat = new DecimalFormat(pattern.toString());
//
//		// Parse the string to get the double value with the desired decimal places
//		try {
//			return "" + decimalFormat.parse(value).doubleValue();
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return "ERROR";
//		}
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