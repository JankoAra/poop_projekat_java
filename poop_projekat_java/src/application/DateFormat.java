package application;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFormat implements Format {
	@Override
	public String getDescription() { return "D"; }

	@Override
	public boolean stringFitsFormat(String string) {
		if (string.equals("")) return true;
		String dateFormatPattern = "^(\\d{1,2})\\.(\\d{1,2})\\.(\\d{1,4})\\.$";

		Pattern pattern = Pattern.compile(dateFormatPattern);

		// Match the string against the regex pattern
		Matcher matcher = pattern.matcher(string);
		if (matcher.matches()) {
			int day = Integer.parseInt(matcher.group(1));
			int month = Integer.parseInt(matcher.group(2));
			int year = Integer.parseInt(matcher.group(3));

			//System.out.println("Day: " + day);
			//System.out.println("Month: " + month);
			//System.out.println("Year: " + year);
			return isValidDate(day, month, year);
		}
		else {
			//System.out.println("Invalid date format.");
			GUI.printlnLog("Datum nije upisan u pravilnom formatu, ili je nepostojeci.");
			return false;
		}
	}

	@Override
	public String formattedValue(String value) {
		if (value.equals("")) return "";
		String dateFormatPattern = "^(\\d{1,2})\\.(\\d{1,2})\\.(\\d{1,4})\\.$";

		Pattern pattern = Pattern.compile(dateFormatPattern);

		Matcher matcher = pattern.matcher(value);
		if (matcher.matches()) {
			int day = Integer.parseInt(matcher.group(1));
			int month = Integer.parseInt(matcher.group(2));
			int year = Integer.parseInt(matcher.group(3));
			return String.format("%02d.%02d.%04d.", day, month, year);
		}
		else {
			return "ERROR";
		}
	}

	private static boolean isValidDate(int day, int month, int year) {
		if (year < 1 || month < 1 || month > 12 || day < 1) {
			return false;
		}

		int maxDays = getMaxDaysInMonth(year, month);
		if (day > maxDays) {
			return false;
		}

		return true;
	}

	private static int getMaxDaysInMonth(int year, int month) {
		if (month == 2) {
			if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
				return 29;
			}
			else {
				return 28;
			}
		}
		else if (month == 4 || month == 6 || month == 9 || month == 11) {
			return 30;
		}
		else {
			return 31;
		}
	}

}
