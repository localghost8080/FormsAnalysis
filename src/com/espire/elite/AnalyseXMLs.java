package com.espire.elite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AnalyseXMLs {
	private Set<String> comparedFiles = new HashSet<>();
	// Map<versionName,map<fileName,path>>
	private Map<String, Map<String, String>> fileswithVersion = new HashMap<>();
	// fileName,set of same file
	private Map<String, Set<WrapperFile>> diffVersionFiles = new HashMap<>();
	private Set<String> versionSet = new HashSet<String>();
	private Set<String> tagNames = new HashSet<>();
	private Map<String, String> tagForScan = new HashMap<>();
	private String baseVersion = null;

	private static XSSFWorkbook workbook = new XSSFWorkbook();
	//private static HSSFWorkbook workbook = new HSSFWorkbook();
	private static XLWriter xlWriter = new XLWriter();
	private List<String> tagHeaderList=new ArrayList<>();
	private static String xmlPath=null;
	// HSSFSheet sheet = workbook.createSheet("File Ex");

	public static void main(String[] args) {
		AnalyseXMLs analyseXMLs = new AnalyseXMLs();
		Integer roNum = new Integer(1);
		Map<String, Object[]> data = analyseXMLs.scanFiles(roNum);

		analyseXMLs.identifyDuplicateFiles(data, roNum);

		XSSFSheet sheet = workbook.createSheet("File Existence");
		xlWriter.addToSheet(workbook, sheet, data);

		analyseXMLs.scanTags();

		analyseXMLs.xlWriter.writeXL(workbook,xmlPath);
	}

	private Map<String, Object[]> scanFiles(Integer roNum) {
		
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		File directory;
		if(prop.get("xmlLocation")!=null && !prop.get("xmlLocation").toString().isEmpty()){
			directory = new File(prop.get("xmlLocation").toString());
			xmlPath=prop.get("xmlLocation").toString();
		}else{
			directory = new File("D:/xml");	
			xmlPath="D:/xml";
		}
		baseVersion=prop.get("baseversion").toString();
		tagNames.add("Name");
		tagNames.add("Alert");
		tagNames.add("ProgramUnit");
		tagNames.add("Block");
		tagNames.add("Trigger");
		tagNames.add("LOV");
		tagNames.add("ModuleParameter");
		tagNames.add("RecordGroup");
		
		
		tagForScan.put("/Module/FormModule", "Name");
		tagForScan.put("/Module/FormModule/Alert", "Alert");
		tagForScan.put("/Module/FormModule/ProgramUnit", "ProgramUnit");
		tagForScan.put("/Module/FormModule/Block", "Block");
		tagForScan.put("/Module/FormModule/Trigger", "Trigger");
		tagForScan.put("/Module/FormModule/LOV", "LOV");
		tagForScan.put("/Module/FormModule/ModuleParameter", "ModuleParameter");
		tagForScan.put("/Module/FormModule/RecordGroup", "RecordGroup");
		
		Map<String, Object[]> data = new HashMap<String, Object[]>();
		// data.put("1", new Object[] {"Emp No.", "Name", "Salary"});

		File[] fList = directory.listFiles();
		// System.out.print(",,");
		// for(int index=0;index < fList.length;index++){
		// File file=fList[index];
		// if (file.isDirectory()) {
		// System.out.print(","+file.getName());
		// versionSet.add(file.getName());
		// Map<String, String> xmlfiles = new HashMap<>();
		// for (File innerfile : file.listFiles()) {
		// xmlfiles.put(innerfile.getName(), innerfile.getAbsolutePath());
		// }
		// fileswithVersion.put(file.getName(), xmlfiles);
		// }
		// }
		List<String> versionSetTemp = new ArrayList<String>();
		versionSetTemp.add("File Name");

		// versionSet.add("File Name");
		// versionSet.add("Tag Value");

		for (File file : fList) {
			if (file.isDirectory()) {
				// System.out.print(","+file.getName());
				versionSet.add(file.getName());
				Map<String, String> xmlfiles = new HashMap<>();
				for (File innerfile : file.listFiles()) {
					xmlfiles.put(innerfile.getName(), innerfile.getAbsolutePath());
				}
				fileswithVersion.put(file.getName(), xmlfiles);
			}
		}
		versionSetTemp.addAll(versionSet);
		String[] inputArray = new String[versionSetTemp.size()];
		versionSetTemp.toArray(inputArray);
		data.put(roNum.toString(), inputArray);
		
		tagHeaderList.add("File Name");
		tagHeaderList.add("Tag Name");
		tagHeaderList.add("Tag Value");
		tagHeaderList.addAll(versionSet);

		System.out.println();
		return data;
	}

	private void identifyDuplicateFiles(Map<String, Object[]> data, Integer rownum) {

		Set<String> versionSet = fileswithVersion.keySet();
		for (String version : versionSet) {
			Set<String> innerVersionSet = fileswithVersion.keySet();
			Map<String, String> files = fileswithVersion.get(version);
			Set<String> fileSet = files.keySet();

			for (String fileName : fileSet) {

				List<String> fileStaus = new ArrayList<>();
				if (!comparedFiles.contains(fileName)) {

					System.out.print(fileName + ",,");
					fileStaus.add(fileName);
				}

				for (String innerVersion : innerVersionSet) {
					WrapperFile wrapperFile = new WrapperFile();
					if (fileswithVersion.get(innerVersion).get(fileName) != null && !comparedFiles.contains(fileName)) {
						System.out.print(",Yes");
						fileStaus.add("Yes");
						wrapperFile.setVersion(innerVersion);
						wrapperFile.setFileName(fileName);
						wrapperFile.setFilePath(fileswithVersion.get(innerVersion).get(fileName));

						if (diffVersionFiles.get(fileName) == null) {
							Set<WrapperFile> Wrapperfiles = new HashSet();
							diffVersionFiles.put(fileName, Wrapperfiles);
						}
						diffVersionFiles.get(fileName).add(wrapperFile);

					} else if (!version.equals(innerVersion) && !comparedFiles.contains(fileName)) {
						System.out.print(",No");
						fileStaus.add("No");
					}

				}
				if (!comparedFiles.contains(fileName)) {
					System.out.println();
				}
				if (!fileStaus.isEmpty()) {
					rownum++;
					String[] inputArray = new String[fileStaus.size()];
					fileStaus.toArray(inputArray);
					data.put(rownum.toString(), inputArray);
				}
				comparedFiles.add(fileName);
			}
		}
	}

	public void getXMLData(WrapperFile wrapperFile) {
		File inputFile = new File(wrapperFile.getFilePath());
		Map<String, Set<Element>> tagData = new HashMap<>();
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();
			for (String expression : tagForScan.keySet()) {
				String tagName = tagForScan.get(expression);
				NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node nNode = nodeList.item(i);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						eElement.getChildNodes();
						if (tagData.get(tagName) == null) {
							Set<Element> tagValues = new HashSet<>();
							tagData.put(tagName, tagValues);
						}
						// tagData.get(tagName).add(eElement.getAttribute("Name"));
						tagData.get(tagName).add(eElement);
					}
				}
			}

			//// XPath xPath1 = XPathFactory.newInstance().newXPath();
			// expression = "/Module/FormModule/Alert";
			// nodeList = (NodeList) xPath.compile(expression).evaluate(doc,
			//// XPathConstants.NODESET);
			// for (int i = 0; i < nodeList.getLength(); i++) {
			// Node nNode = nodeList.item(i);
			// if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			// Element eElement = (Element) nNode;
			// eElement.getChildNodes();
			// if (tagData.get("Alert") == null) {
			// Set<String> tagValues = new HashSet<>();
			// tagData.put("Alert", tagValues);
			// }
			// tagData.get("Alert").add(eElement.getAttribute("Name"));
			// }
			// }

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		wrapperFile.setTagData(tagData);
	}

	public void scanTags() {
		Map<String, Object[]> data = new HashMap<String, Object[]>();
		
		String[] headerArray = new String[tagHeaderList.size()];
		tagHeaderList.toArray(headerArray);
		Integer rowNum = 1;
		data.put(rowNum.toString(), headerArray);
		for (String fileName : diffVersionFiles.keySet()) {
			// Set<String> tagValues = new HashSet<>();
			Map<String, Set<Element>> tagValuesMap = new HashMap<>();

			
			// List<String> status=new ArrayList<String>();

			System.out.println(fileName+" "+rowNum);
			
			// status.add(fileName);

			for (WrapperFile wrapperFile : diffVersionFiles.get(fileName)) {
				getXMLData(wrapperFile);
				for (String tagName : wrapperFile.getTagData().keySet()) {
					if (tagValuesMap.get(tagName) == null) {
						tagValuesMap.put(tagName, new HashSet<Element>());
					}
					tagValuesMap.get(tagName).addAll(wrapperFile.getTagData().get(tagName));
				}
			}
//                .xml  pu,fv--v1,v2,v3
			// <fileName,Map<tagName, Map<tagValue, Set<diffrentVersions>>>>
			Map<String, Map<String, Map<TagValueWrapper, Set<ElementWrapper>>>> existingTagsMap = new HashMap<>();
			Map<String, Map<TagValueWrapper, Set<ElementWrapper>>> existingTags = new HashMap<>();
			for (String tagNameCurrent : tagValuesMap.keySet()) {
				Set<Element> tagValues = tagValuesMap.get(tagNameCurrent);
				for (Element tagValue : tagValues) {
					existingTagsMap.put(fileName, existingTags);
					for (WrapperFile wrapperFile : diffVersionFiles.get(fileName)) {
						for (String tagName : tagNames) {
							if (tagName.equals(tagNameCurrent)) {
								if (existingTags.get(tagName) == null) {
									Map<TagValueWrapper, Set<ElementWrapper>> map = new HashMap<>();
									existingTags.put(tagName, map);
								}
								if (existingTags.get(tagName).get(tagValue.getAttribute("Name")) == null) {
									TagValueWrapper tagValueWrapper=new TagValueWrapper();
									tagValueWrapper.setTagValue(tagValue.getAttribute("Name"));
									if(tagName.equals("ProgramUnit")){										
										tagValueWrapper.setOtherKey(tagValue.getAttribute("ProgramUnitType"));
									}
									existingTags.get(tagName).put(tagValueWrapper, new HashSet<>());
								}
								if (wrapperFile.getTagData().get(tagName) != null
										&& wrapperFile.getTagData().get(tagName).contains(tagValue)) {
									ElementWrapper elementWrapper = new ElementWrapper(tagValue,
											wrapperFile.getVersion());
									// existingTags.get(tagName).get(tagValue.getAttribute("Name")).add(wrapperFile.getVersion());
									TagValueWrapper tagValueWrapper=new TagValueWrapper();
									tagValueWrapper.setTagValue(tagValue.getAttribute("Name"));
									if(tagName.equals("ProgramUnit")){
										
										tagValueWrapper.setOtherKey(tagValue.getAttribute("ProgramUnitType"));
									}
									
									existingTags.get(tagName).get(tagValueWrapper).add(elementWrapper);
								}
							}
						}
					}
				}
			}
			for (String fileName1 : existingTagsMap.keySet()) {

				Map<String, Map<TagValueWrapper, Set<ElementWrapper>>> existingTagsForFile = existingTagsMap.get(fileName1);
//				Integer rowNum = 1;
				for (String tagName : existingTagsForFile.keySet()) {

					for (TagValueWrapper tagValueWrapper : existingTagsForFile.get(tagName).keySet()) {
						List<String> status = new ArrayList<String>();

						System.out.print("," + tagName + "," + tagValueWrapper.getTagValue());
						status.add(fileName1);
						status.add(tagName);
						status.add(tagValueWrapper.getTagValue());

						for (String verion : versionSet) {
							ElementWrapper elementWrapperVersion = new ElementWrapper(null, verion);
							ElementWrapper elementWrapperBaseVersion = null;
							ElementWrapper elementWrapperCurrentVersion = null;

							for (Iterator<ElementWrapper> iterator = existingTagsForFile.get(tagName).get(tagValueWrapper)
									.iterator(); iterator.hasNext();) {
								ElementWrapper currentElement = iterator.next();
								if (currentElement.getVersionNumber().equals(verion)) {
									elementWrapperCurrentVersion = currentElement;
									break;
								}

							}

							for (Iterator<ElementWrapper> iterator = existingTagsForFile.get(tagName).get(tagValueWrapper)
									.iterator(); iterator.hasNext();) {
								ElementWrapper currentElement = iterator.next();
								if (currentElement.getVersionNumber().equals(baseVersion)) {
									elementWrapperBaseVersion = currentElement;
									break;
								}

							}
							// existingTagsForFile.get(tagName).get(tagValue).

							if (existingTagsForFile.get(tagName).get(tagValueWrapper) != null
									&& existingTagsForFile.get(tagName).get(tagValueWrapper).contains(elementWrapperVersion)) {

								if (elementWrapperCurrentVersion != null && elementWrapperBaseVersion != null
										&& !elementWrapperBaseVersion.getVersionNumber()
												.equals(elementWrapperCurrentVersion.getVersionNumber())
										&& !elementWrapperBaseVersion.getElement()
												.isEqualNode(elementWrapperCurrentVersion.getElement())) {
									
									
									String puTxtCurrentVersion = elementWrapperCurrentVersion.getElement().getAttribute("ProgramUnitText").replaceAll("[\\n\\t]", "").replaceAll("\\s\\s+", "");
									String puTxtBaseVersion = elementWrapperBaseVersion.getElement().getAttribute("ProgramUnitText").replaceAll("[\\n\\t]", "").replaceAll("\\s\\s+", "");	
									if (puTxtBaseVersion.equals(puTxtCurrentVersion)) {
										System.out.print(",Yes");
										status.add("Yes");
									} else {
										System.out.print(",Yes*");
										status.add("Yes*");
									}

								} else {
									System.out.print(",Yes");
									status.add("Yes");
								}

							} else {
								System.out.print(",No");
								status.add("No");
							}
						}
						System.out.println("  "+rowNum);
						rowNum++;
						String[] inputArray = new String[status.size()];
						status.toArray(inputArray);
						data.put(rowNum.toString(), inputArray);

					}

				}
			}
		}
		XSSFSheet summarySheet = workbook.createSheet("Summary");
		xlWriter.addToSheet(workbook, summarySheet, data);
	}
}
