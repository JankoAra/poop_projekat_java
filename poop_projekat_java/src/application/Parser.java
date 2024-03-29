package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import application.JsonMappedData.MetaCell;

import application.MyExceptions.UnsupportedFileFormat;

public class Parser {
	static File currentFile = new File("Untitled");

	/**
	 * Utvrdjuje tip zadatog fajla.
	 * @param file - Zadati fajl.
	 * @return Vraca String koji predstavlja ekstenziju tog fajla (npr. .csv ili .json ili .txt)
	 */
	public static String getExtensionFromFilePath(File file) {
		String path = file.getName();
		int lastIndex = path.lastIndexOf('.');
		if (lastIndex > 0 && lastIndex < path.length() - 1) {
			return path.substring(lastIndex);
		}
		System.out.println("Greska u odredjivanju ekstenzije");
		return "";
	}

	/**
	 * Pretvara zadatu tabelu u String prema zadatom tipu fajla(na osnovu ekstenzije)
	 * @param table - Tabela koja se konvertuje.
	 * @param extension - Ekstenzija koja odredjuje tip fajla
	 * @return Vraca String u zadatom formatu.
	 * @throws UnsupportedFileFormat Ako tip fajla nije podrzan.
	 */
	public static String convertTableToString(Table table, String extension) throws UnsupportedFileFormat {
		if (extension.equals(".csv")) {
			return convertTableToCSVString(table);
		}
		else if (extension.equals(".json")) {
			return convertTableToJSONString(table);
		}
		System.out.println("Format nije podrzan");
		throw new UnsupportedFileFormat();
	}

	/**
	 * Konvertuje sadrzaj tabele u String u JSON formatu
	 * @param table - Tabela koja se konvertuje
	 * @return String u JSON formatu.
	 */
	private static String convertTableToJSONString(Table table) {
		String jsonString = "";
		ObjectMapper objectMapper = new ObjectMapper();
		JsonMappedData data = new JsonMappedData(table);

		try {
			jsonString = objectMapper.writeValueAsString(data);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return jsonString;
	}

	/**
	 * Konvertuje sadrzaj tabele u String u CSV formatu
	 * @param table - Tabela koja se konvertuje
	 * @return String u CSV formatu.
	 */
	public static String convertTableToCSVString(Table table) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < table.getNumOfRows(); i++) {
			for (int j = 0; j < Table.NUMBER_OF_COLUMNS; j++) {
				Cell cell = table.getCell(i, j);
				sb.append(cell.getValue());
				sb.append(j == Table.NUMBER_OF_COLUMNS - 1 ? "\n" : ",");
			}
		}
		String csv = sb.toString();
		return csv;
	}

	/**
	 * Pravi tabelu na osnovu fajla u CSV formatu
	 * @param file - Fajl
	 * @return Vraca napravljenu tabelu.
	 */
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
				if (rowNum != 0) table.addRow();
				String row[] = line.split(",", -1);
				int colNum = 0;
				for (String value : row) {
					if (colNum >= Table.NUMBER_OF_COLUMNS) throw new Exception();
					Cell cell = new Cell(value, Cell.TEXT_FORMAT, rowNum, colNum);
					table.setCell(rowNum, colNum, cell);
					colNum++;
				}
				rowNum++;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			table = new Table();
		}
		finally {
			try {
				if (buffer != null) {
					buffer.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return table;
	}

	/**
	 * Pravi tabelu na osnovu fajla u JSON formatu
	 * @param file - Fajl
	 * @return Vraca napravljenu tabelu.
	 */
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

			// find numOfRows
			int rowsNeeded = 1;
			rowsNeeded = data.getCells().get(data.getCells().size() - 1).getRow() + 1;
			table = new Table(rowsNeeded);

			//primeni globalne formate
			for (int i = 0; i < data.getGlobalColumnDecimals().size(); i++) {
				String decimalsString = data.getGlobalColumnDecimals().get(i);
				String formatString = data.getGlobalColumnFormats().get(i);
				int dec = Integer.parseInt(decimalsString);
				for (int r = 0; r < table.getNumOfRows(); r++) {
					Format f = Format.makeFormat(formatString);
					if (formatString.equals("N")) {
						((NumberFormat) f).setDecimalsToShow(dec);
					}
					Cell formattedCell = new Cell("", f, r, i);
					table.setCell(r, i, formattedCell);
				}
			}

			// ubaci pojedinacne celije
			for (MetaCell metaCell : data.getCells()) {
				String formatString = metaCell.getFormat();
				Format f = Format.makeFormat(formatString);
				int dec = metaCell.getDecimals();
				if (formatString.equals("N")) {
					((NumberFormat) f).setDecimalsToShow(dec);
				}
				Cell newCell = new Cell(metaCell.getValue(), f, metaCell.getRow(), metaCell.getColumn());

				table.setCell(metaCell.getRow(), metaCell.getColumn(), newCell);

			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return table;
	}

	/**
	 * Snima zadati sadrzaj u fajl sa zadatom putanjom.
	 * @param path - Putanja fajla.
	 * @param content - Novi sadrzaj fajla.
	 */
	public static void createNewSaveFile(File path, String content) {
		try {
			FileWriter writer = new FileWriter(path);
			//			System.out.println(path.getName());
			//			System.out.println(path.getAbsolutePath());
			writer.write(content);
			writer.close();
		}
		catch (IOException e) {
			System.out.println("Greska pri cuvanju fajla");
			e.printStackTrace();
		}
	}

}
