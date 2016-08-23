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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLWriter {

//	public void addToSheet(HSSFWorkbook workbook, HSSFSheet sheet, Map<String, Object[]> data) {
//		Set<String> keyset = data.keySet();
//		List<Integer> list=new ArrayList<>();
//		for (String stringValue : keyset) {
//			Integer intValue=Integer.parseInt(stringValue);
//			list.add(intValue);
//		}
//		
//		Collections.sort(list);
//		
//		int rownum = 0;
//		for (Integer key : list) {
//			Row row = sheet.createRow(rownum++);
//			Object[] objArr = data.get(key.toString());
//			int cellnum = 0;
//			if (objArr!=null) {
//				for (Object obj : objArr) {
//					Cell cell = row.createCell(cellnum++);
//					if (obj instanceof Date)
//						cell.setCellValue((Date) obj);
//					else if (obj instanceof Boolean)
//						cell.setCellValue((Boolean) obj);
//					else if (obj instanceof String)
//						cell.setCellValue((String) obj);
//					else if (obj instanceof Double)
//						cell.setCellValue((Double) obj);
//				} 
//			}
//		}
//
//		
//	}
//	
//	public void writeXL(HSSFWorkbook workbook,String path){
//		try {
//			String newPath=path.concat("/Analysis.xls");
//			FileOutputStream out = new FileOutputStream(new File(newPath));
//			workbook.write(out);
//			out.close();
//			System.out.println("Excel written successfully..");
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	public void addToSheet(XSSFWorkbook workbook, XSSFSheet sheet, Map<String, Object[]> data) {
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
	
	public void writeXL(XSSFWorkbook workbook,String path){
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
