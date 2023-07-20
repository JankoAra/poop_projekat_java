package application;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFormat implements Format {

	public DateFormat() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getDescription() {
		return "D";
	}

	@Override
	public boolean stringFitsFormat(String string) {
		// Define the regex pattern for the date format "dd.MM.yyyy" with capturing
		// groups
		String dateFormatPattern = "^(\\d{1,2})\\.(\\d{1,2})\\.(\\d{1,4})\\.$";

		// Create the regex pattern object
		Pattern pattern = Pattern.compile(dateFormatPattern);

		// Match the string against the regex pattern
		Matcher matcher = pattern.matcher(string);
		if (matcher.matches()) {
			// Extract the day, month, and year from the capturing groups
			int day = Integer.parseInt(matcher.group(1));
			int month = Integer.parseInt(matcher.group(2));
			int year = Integer.parseInt(matcher.group(3));

			// Print the extracted components
			System.out.println("Day: " + day);
			System.out.println("Month: " + month);
			System.out.println("Year: " + year);
			return isValidDate(day, month, year);
		} else {
			System.out.println("Invalid date format.");
			return false;
		}
	}

	@Override
	public String formattedValue(String value) {
		String dateFormatPattern = "^(\\d{1,2})\\.(\\d{1,2})\\.(\\d{1,4})\\.$";

		// Create the regex pattern object
		Pattern pattern = Pattern.compile(dateFormatPattern);

		// Match the string against the regex pattern
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches()) {
			// Extract the day, month, and year from the capturing groups
			int day = Integer.parseInt(matcher.group(1));
			int month = Integer.parseInt(matcher.group(2));
			int year = Integer.parseInt(matcher.group(3));
			return String.format("%02d.%02d.%04d.", day, month, year);
		} else {
			return "ERROR";
		}
	}

	private static boolean isValidDate(int day, int month, int year) {
		// Check if the year, month, and day are within valid ranges
		if (year < 1 || month < 1 || month > 12 || day < 1) {
			return false;
		}

		// Determine the maximum number of days for the given month and year
		int maxDays = getMaxDaysInMonth(year, month);
		if (day > maxDays) {
			return false;
		}

		// The date is valid if all conditions are met
		return true;
	}

	private static int getMaxDaysInMonth(int year, int month) {
		// Determine the maximum number of days in the given month and year
		if (month == 2) {
			if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
				return 29;
			} else {
				return 28;
			}
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			return 30;
		} else {
			return 31;
		}
	}

}
