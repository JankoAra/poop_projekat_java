package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import application.JsonMappedData.MetaCell;

public class Parser {
	static File currentFile = new File("Untitled");

	// extracts file extension (ex. .csv or .json)
	public static String getExtensionFromFilePath(File file) {
		String path = file.getName();
		int lastIndex = path.lastIndexOf('.');
		if (lastIndex > 0 && lastIndex < path.length() - 1) {
			return path.substring(lastIndex);
		}
		System.out.println("Greska u odredjivanju ekstenzije");
		return "";
	}

	public static String convertTableToString(Table table, String extension) throws Exception {
		if (extension.equals(".csv")) {
			return convertTableToCSVString(table);
		} else if (extension.equals(".json")) {
			return convertTableToJSONString(table);
		}
		System.out.println("Format nije podrzan");
		throw new Exception();
	}

	private static String convertTableToJSONString(Table table) {
		String jsonString = "";
		ObjectMapper objectMapper = new ObjectMapper();
		JsonMappedData data = new JsonMappedData(table);
		
		try {
			jsonString = objectMapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonString;
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
		String csv = sb.toString();
		// System.out.println("CSV tabele iz jave:\n" + csv);
		return csv;
	}

	public static Table convertCSVToTable(File file) {
		if (file == null) {
			System.out.println("Greska pri ucitavanju fajla");
			return null;
		}
		Table table = new Table();
		Parser.currentFile = file;
		GUI.stage.setTitle("Excel by JANKO - " + Parser.currentFile.getAbsolutePath());
		BufferedReader buffer = null;
		try {
			buffer = new BufferedReader(new FileReader(file));
			String line;
			int rowNum = 0;
			while ((line = buffer.readLine()) != null) {
				if (rowNum != 0)
					table.addRow();
				String row[] = line.split(",", -1);
				int colNum = 0;
				for (String value : row) {
					if (colNum >= Table.numOfCols)
						throw new Exception();
					Cell cell = new Cell(value, Cell.TEXT_FORMAT, rowNum, colNum);
					table.setCell(rowNum, colNum, cell);
					colNum++;
				}
				rowNum++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			table = new Table();
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
		if (file == null) {
			System.out.println("Greska pri ucitavanju fajla");
			return null;
		}
		Table table = new Table();
		Parser.currentFile = file;
		GUI.stage.setTitle("Excel by JANKO - " + Parser.currentFile.getAbsolutePath());

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonMappedData data = objectMapper.readValue(file, JsonMappedData.class);
			// Process the data...
			// Now you can work with the Java object representation of the JSON data
			System.out.println(data.getGlobalColumnFormats());
			System.out.println(data.getGlobalColumnDecimals());
			System.out.println(data.getCells());

			// find numOfRows
			int rowsNeeded = 1;
			rowsNeeded = data.getCells().get(data.getCells().size() - 1).getRow() + 1;
			table = new Table(rowsNeeded);
			// Access individual cells
			for (MetaCell metaCell : data.getCells()) {
				Cell newCell = new Cell(metaCell.getValue(), Format.makeFormat(metaCell.getFormat()), metaCell.getRow(),
						metaCell.getColumn());
				table.setCell(metaCell.getRow(), metaCell.getColumn(), newCell);

				System.out.println("Row: " + metaCell.getRow() + ", Column: " + metaCell.getColumn());
				System.out.println("Value: " + metaCell.getValue());
				System.out.println("Format: " + metaCell.getFormat());
				System.out.println("Decimals: " + metaCell.getDecimals());
//                int row = metaCell.getRow();
//                if(row+1>rowsNeeded) {
//                	rowsNeeded = row+1;
//                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return table;
	}

	public static void createNewSaveFile(File path, String content) {
		try {
			FileWriter writer = new FileWriter(path);
			System.out.println(path.getName());
			System.out.println(path.getAbsolutePath());
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			System.out.println("Greska pri cuvanju fajla");
			e.printStackTrace();
		}
	}

}
