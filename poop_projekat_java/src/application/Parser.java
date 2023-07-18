package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

public class Parser {
	public static Table loadCSVTable(File file) {
		
		if (file==null) {
			System.out.println("Greska pri ucitavanju fajla");
			return null;
		}
		Table table = new Table();
		try {
//			if(!Paths.get(filename).isAbsolute()) {
//				filename = "savedTables\\"+filename;
//			}
//			BufferedReader buffer = new BufferedReader(new FileReader(new File(filename)));
			BufferedReader buffer = new BufferedReader(new FileReader(file));
			String line;
			int rowNum = 0;
			while ((line = buffer.readLine()) != null) {
				if(rowNum!=0)table.addRow();
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
		}
		return table;
	}

}
