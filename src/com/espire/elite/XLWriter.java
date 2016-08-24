package com.espire.elite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class XLWriter {

	
	public static void addToSheet(Workbook workbook, Sheet sheet, Map<String, Object[]> data) {
		Set<String> keyset = data.keySet();
		List<Integer> list=new ArrayList<>();
		for (String stringValue : keyset) {
			Integer intValue=Integer.parseInt(stringValue);
			list.add(intValue);
		}
		
		Collections.sort(list);
		
		int rownum = 0;
		for (Integer key : list) {
			Row row = sheet.createRow(rownum++);
			Object[] objArr = data.get(key.toString());
			int cellnum = 0;
			if (objArr!=null) {
				for (Object obj : objArr) {
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof Date)
						cell.setCellValue((Date) obj);
					else if (obj instanceof Boolean)
						cell.setCellValue((Boolean) obj);
					else if (obj instanceof String)
						cell.setCellValue((String) obj);
					else if (obj instanceof Double)
						cell.setCellValue((Double) obj);
				} 
			}
		}

		
	}
	
	public static void writeXL(Workbook workbook,String path){
		try {
			String newPath=path.concat("/Analysis.xlsx");
			FileOutputStream out = new FileOutputStream(new File(newPath));
			workbook.write(out);
			out.close();
			System.out.println("Excel written successfully..");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
