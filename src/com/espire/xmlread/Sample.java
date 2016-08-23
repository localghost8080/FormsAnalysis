package com.espire.xmlread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Sample {

	private Set<String> versionSet = new HashSet<String>();
	public static List<String> titleDetails = new ArrayList<>();
	private Map<String, Map<String,File >> files;
	ArrayList<List<File>> allVersionFiles;
	Map<String,Map<String,File>> fileMapWithVersion=new HashMap<>();
	
	public static void main(String[] args) {
		Sample sample = new Sample();
		sample.checkCommonFile(sample.listFiles());
		sample.scanAllVersionFile();
		// sample.search(sample.listFiles());
	}

	private void checkCommonFile(ArrayList<List<File>> list) {
		Map<String, List<String>> fileMap = new HashMap<>();

		for (List<File> outerFileList : list) {
			for (File file : outerFileList) {
				for (List<File> innerFilelist : list) {
					for (File innerfile : innerFilelist) {
						if (list.indexOf(innerFilelist) < list.indexOf(outerFileList)) {
							break;
						}
						if (innerfile.getName().equals(file.getName())) {
							if (fileMap.get(file.getName()) == null) {
								ArrayList<String> localList = new ArrayList<>();
								localList.add(innerfile.getParentFile().getName());
								fileMap.put(file.getName(), localList);
							} else {
								fileMap.get(file.getName()).add(innerfile.getParentFile().getName());
							}
							break;
						}
					}
				}

			}
		}
		for (String key : fileMap.keySet()) {
			List<String> lists = fileMap.get(key);
			boolean firstdone = false;
			Set<String> fileHashSet = new HashSet<String>(lists);
			for (String versionName : versionSet) {
				if (!firstdone) {
					firstdone = true;
					System.out.print(key);
				}
				if (fileHashSet.contains(versionName)) {
					System.out.print(",Yes");
				} else {
					System.out.print(",No");
				}
			}
			firstdone = true;
			System.out.println();
		}

	}

	public ArrayList<List<File>> listFiles() {
		File directory = new File("D:/SysconProduct/LocalWS/java8/xml");
		allVersionFiles = new ArrayList<List<File>>();
		File[] fList = directory.listFiles();
		for (File file : fList) {
 
			if (file.isDirectory()) {
				versionSet.add(file.getName());
				ArrayList<File> list = new ArrayList<>();
				for (File innerfile : file.listFiles()) {
					list.add(innerfile);
					if (fileMapWithVersion.get(innerfile.getName()) == null) {
						Map<String,File> vesionFileMap = new HashMap<>();
						vesionFileMap.put(innerfile.getParentFile().getName(),innerfile);
						fileMapWithVersion.put(innerfile.getName(), vesionFileMap);
					} else {
						fileMapWithVersion.get(innerfile.getName()).put(innerfile.getParentFile().getName(), innerfile);
					}
				}
				allVersionFiles.add(list);
			}
		}
		
//		Map<filename,map<version,FileObject>>
//		for (String fileName : fileMapWithVersion.keySet()) {
//			for (String version : fileMapWithVersion.get(fileName).keySet()) {
//				System.out.println(fileName+":"+version+":"+fileMapWithVersion.get(fileName).get(version).getAbsolutePath() );
//			}
//			
//		}
		return allVersionFiles;
	}

//	public void search(List<File> files) {
//		for (File file : files) {
//			Scanner fileScanner;
//			try {
//				fileScanner = new Scanner(file);
//				Pattern p1 = Pattern.compile("<Alert");
//				Pattern p2 = Pattern.compile("<AttachedLibrary");
//				Pattern p3 = Pattern.compile("<Block");
//				Pattern p4 = Pattern.compile("<Trigger");
//				Pattern p5 = Pattern.compile("<Canvas");
//				Pattern p6 = Pattern.compile("<LOV");
//				Pattern p7 = Pattern.compile("<ProgramUnit");
//				Pattern p8 = Pattern.compile("<RecordGroup");
//				Pattern p9 = Pattern.compile("<VisualAttribute");
//
//				int count1 = 0;
//				int count2 = 0;
//				int count3 = 0;
//				int count4 = 0;
//				int count5 = 0;
//				int count6 = 0;
//				int count7 = 0;
//				int count8 = 0;
//				int count9 = 0;
//
//				while (fileScanner.hasNext()) {
//					String s = fileScanner.next();
//					Matcher m1 = p1.matcher(s);
//					Matcher m2 = p2.matcher(s);
//					Matcher m3 = p3.matcher(s);
//					Matcher m4 = p4.matcher(s);
//					Matcher m5 = p5.matcher(s);
//					Matcher m6 = p6.matcher(s);
//					Matcher m7 = p7.matcher(s);
//					Matcher m8 = p8.matcher(s);
//					Matcher m9 = p9.matcher(s);
//
//					count1 = count(count1, m1);
//					count2 = count(count2, m2);
//					count3 = count(count3, m3);
//					count4 = count(count4, m4);
//					count5 = count(count5, m5);
//					count6 = count(count6, m6);
//					count7 = count(count7, m7);
//					count8 = count(count8, m8);
//					count9 = count(count9, m9);
//
//				}
//				System.out.println(file.getName() + "," + count1 + ","
//
//						+ count2 + "," + +count3 + "," + +count4 + "," + +count5 + "," + +count6 + "," + +count7 + ","
//						+ +count8 + "," + +count9
//
//				);
//			} catch (FileNotFoundException e) {
//
//				e.printStackTrace();
//			}
//
//		}
//
//	}

	public void scanAllVersionFile(){
		Map<String, Map<String, String>> tagData=new HashMap<>();
		for (String fileName : fileMapWithVersion.keySet()) {
			System.out.println(fileName);
			for (String version : fileMapWithVersion.get(fileName).keySet()) {

				//				System.out.println(fileName+":"+version+":"+fileMapWithVersion.get(fileName).get(version).getAbsolutePath() );
				
				getXMLData(fileMapWithVersion.get(fileName).get(version), tagData);
			}
			for (String tagName : tagData.keySet()) {
				for (String versionName : versionSet) {
				if(tagData.get(tagName).keySet().contains(versionName)){
					System.out.print(",Yes");
				}else{
					System.out.print(",No");
				}
				}
//				for(String versionName:tagData.get(tagName).keySet()){
//					System.out.println(tagName+" "+versionName+ " "+tagData.get(tagName).get(versionName));
//				}
			}
			tagData.clear();
			System.out.println("");
		}
		

	}
//	public void search(Collection<File> collection) {
//		for (File file : collection) {
//			Scanner fileScanner;
//			try {
//				fileScanner = new Scanner(file);
//				Pattern p1 = Pattern.compile("<Alert");
//				
//
//				int count1 = 0;
//				
//
//				while (fileScanner.hasNext()) {
//					String s = fileScanner.next();
//					Matcher m1 = p1.matcher(s);
//				
//					count1 = count(count1, m1);
//
//				}
//
//			} catch (FileNotFoundException e) {
//
//				e.printStackTrace();
//			}
//
//		}
//
//	}
	
//	<tagName,map<version,File absoultePath>>
	public void getXMLData(File inputFile,Map<String,Map<String,String>> tagData) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();
			String expression = "/Module/FormModule";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nNode = nodeList.item(i);
				// System.out.println("\nCurrent Element :"
				// + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if(tagData.get("Name")==null){
						Map<String, String> local = new HashMap<>();
						System.out.print(",Name,"+eElement.getAttribute("Name"));
						local.put(inputFile.getParentFile().getName(),inputFile.getAbsolutePath());
						tagData.put("Name", local);
					}else{
						tagData.get("Name").put(inputFile.getParentFile().getName(), inputFile.getAbsolutePath());	
					}
					
//					titleDetails.add(inputFile.getName()+","+ eElement.getAttribute("Name"));
				}
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	private int count(int count, Matcher m) {
		while (m.find()) {
			count++;

		}
		return count;
	}
}
