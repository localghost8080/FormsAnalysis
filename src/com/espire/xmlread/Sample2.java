package com.espire.xmlread;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sample2 {
	Set<String> functionNames=new HashSet<>();
	Set<String> procNames=new HashSet<>();
	
	public static void main(String[] args) {
		Sample2 sample=new Sample2();
		
		String filePath = new File("").getAbsolutePath();
		filePath.concat("path to the property file");

		File directory = new File("xml");
		
		File[] fList = directory.listFiles();
		System.out.println(directory.getAbsolutePath());
		System.out.println(directory.getAbsolutePath().lastIndexOf("/"));
//		sample.search(sample.listFiles());
		
		
		
	}
	
	 public ArrayList<File> listFiles(){
	        File directory = new File("D:/SysconProduct/LocalWS/java8/Elite-PLDs");
	        ArrayList<File> list = new ArrayList<>();
	        //get all the files from a directory
	        File[] fList = directory.listFiles();
	        for (File file : fList){
	            if (file.isFile()){
//	                System.out.println(file.getAbsolutePath());
	                list.add(file);
	            }
	        }
	        return list;
	    }
	 public void search(List<File> files) {
		 for (File file : files) {
			 Scanner fileScanner;
			try {
				  fileScanner = new Scanner(file);
//				  Pattern p1=Pattern.compile("<Alert");
//				  Pattern p2=Pattern.compile("<AttachedLibrary");
//				  Pattern p3=Pattern.compile("<Block");
//				  Pattern p4=Pattern.compile("<Trigger");
//				  Pattern p5=Pattern.compile("<Canvas");
//				  Pattern p6=Pattern.compile("<LOV");
//				  Pattern p7=Pattern.compile("<ProgramUnit");
//				  Pattern p8=Pattern.compile("<RecordGroup");
//				  Pattern p9=Pattern.compile("<VisualAttribute");
//				  String regx = "[a-zA-Z]+\\.?";
//				    Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
				  Pattern p1=Pattern.compile("<FormModule");
				  Pattern p2=Pattern.compile("Title=");
				  
				  int count1=0;
//				  int count2=0;
//				  int count3=0;
//				  int count4=0;
//				  int count5=0;
//				  int count6=0;
//				  int count7=0;
//				  int count8=0;
//				  int count9=0;
				  
				while(fileScanner.hasNext()) {
					String s=fileScanner.next();
					Matcher m1=p1.matcher(s);
//					Matcher m2=p2.matcher(s);
//					Matcher m3=p3.matcher(s);
//					Matcher m4=p4.matcher(s);
//					Matcher m5=p5.matcher(s);
//					Matcher m6=p6.matcher(s);
//					Matcher m7=p7.matcher(s);
//					Matcher m8=p8.matcher(s);
//					Matcher m9=p9.matcher(s);
					
					 count1= count(count1, m1,file.getName(),fileScanner,functionNames);
//					 count2= count(count2, m2,file.getName(),fileScanner,procNames);
//					 count3= count(count3, m3);
//					 count4= count(count4, m4);
//					 count5= count(count5, m5);
//					 count6= count(count6, m6);
//					 count7= count(count7, m7);
//					 count8= count(count8, m8);
//					 count9= count(count9, m9);
					 
				    }
				
//				System.out.println(file.getName()+","+count1+","+count2
						//+","+
//						+count3+","+    
//						+count4+","+    
//						+count5+","+    
//						+count6+","+    
//						+count7+","+    
//						+count8+","+    
//						+count9    
						
						
//						);
			} catch (FileNotFoundException e) {    
				  							       
				e.printStackTrace();               
			}                                      
			                                       
		}                                          
		 System.out.println("                                               ---------      FUNCTION NAME              ---------------                    ----------------- ");                                          
		 System.out.println("functioncount:"+functionNames.size() + "procCount " +procNames.size());    
		 for (String functionname  : functionNames) {
			System.out.println(functionname);
		}
		 System.out.println("                                               ---------      PROC NAME              ---------------                    ----------------- ");
		 for (String procName  : procNames) {
				System.out.println(procName);
			}
		 
		}

	private int count(int count, Matcher m,String fileName,Scanner filescaner,Set<String> names) {
		while (m.find()) {
//		    System.out.println(fileName+": "+filescaner.next()); 
			names.add(filescaner.next());
			count++;

		    }
		return count;
	}
}
