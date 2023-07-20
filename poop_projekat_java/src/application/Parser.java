package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class Parser {
	static File currentFile = new File("Untitled");

	

	public static String getExtensionFromFilePath(File file) {
		String path = file.getName();
		int lastIndex = path.lastIndexOf('.');
		if (lastIndex > 0 && lastIndex < path.length() - 1) {
			return path.substring(lastIndex);
		}
		System.out.println("Greska u odredjivanju ekstenzije");
		return "";
	}

	public static String convertTableToString(Table table, String extension) {
		if (extension.equals(".csv")) {
			return convertTableToCSVString(table);
		} else if (extension.equals(".json")) {
			return convertTableToJSONString(table);
		}
		System.out.println("Format nije podrzan");
		return "";
	}

	private static String convertTableToJSONString(Table table) {
		// TODO Auto-generated method stub
		return "Nije napravljen";
	}

	public static String convertTableToCSVString(Table table) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < table.getNumOfRows(); i++) {
			for (int j = 0; j < Table.numOfCols; j++) {
				Cell cell = table.getCell(i, j);
				sb.append(cell.getValue());
				sb.append(j == Table.numOfCols - 1 ? "\n" : ",");
			}
		}
		return sb.toString();
	}

	public static Table convertCSVToTable(File file) {
		if (file == null) {
			System.out.println("Greska pri ucitavanju fajla");
			return null;
		}
		Table table = new Table();
		Parser.currentFile = file;
		GUI.primaryStage.setTitle("Excel by JANKO - " + Parser.currentFile.getAbsolutePath());
		BufferedReader buffer = null;
		try {
			buffer = new BufferedReader(new FileReader(file));
			String line;
			int rowNum = 0;
			while ((line = buffer.readLine()) != null) {
				if (rowNum != 0)
					table.addRow();
				String row[] = line.split(",");
				int colNum = 0;
				for (String value : row) {
					Cell cell = new Cell(value);
					table.setCell(rowNum, colNum, cell);
					colNum++;
				}
				rowNum++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (buffer != null) {
					buffer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return table;
	}

	public static Table convertJSONToTable(File file) {
		return new Table(10);
	}

	public static void createNewSaveFile(File path, String content) {
		try {
			FileWriter writer = new FileWriter(path);
			System.out.println(path.getName());
			System.out.println(path.getAbsolutePath());
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Greska pri cuvanju fajla");
			e.printStackTrace();
		}
	}

}
