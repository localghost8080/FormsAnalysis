package com.espire.elite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	private Map<String, Map<String, String>> versionFileIndex = new HashMap<>();
	private Map<String, Set<WrapperFile>> diffVersionFiles = new HashMap<>();
	private Set<String> versionSet = new HashSet<String>();
	private String baseVersion = null;
	File directory;
	Properties configProp = new Properties();
	Properties tagNamePathProp = new Properties();
	Map<String , String> tagXpathMap = new HashMap<>();
	private static XSSFWorkbook workbook = new XSSFWorkbook();
	private List<String> tagHeaderList=new ArrayList<>();
	private static String xmlPath=null;

	public static void main(String[] args) {
		AnalyseXMLs analyseXMLs = new AnalyseXMLs();
		Integer roNum = 1;
		analyseXMLs.init();
		Map<String, Object[]> data = analyseXMLs.scanFiles();
		data = analyseXMLs.identifyDuplicateFiles(data, roNum);

		XSSFSheet sheet = workbook.createSheet("File Availability");
		XLWriter.addToSheet(workbook, sheet, data);

		analyseXMLs.scanTags();

		XLWriter.writeXL(workbook,xmlPath);
	}
	
	/**
	 * Method to initialize the external property files
	 */
	private void init(){
		try {
			configProp.load(new FileInputStream("config.properties"));
			tagNamePathProp.load(new FileInputStream("tagnames.properties"));
			Set<Object> keys = tagNamePathProp.keySet();
			for(Object tagName : keys){
				tagXpathMap.put(tagName.toString(),tagNamePathProp.getProperty(tagName.toString()));
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(configProp.get("xmlLocation")!=null && !configProp.get("xmlLocation").toString().isEmpty()){
			directory = new File(configProp.get("xmlLocation").toString());
			xmlPath=configProp.get("xmlLocation").toString();
		}else{
			directory = new File("D:/xml");	
			xmlPath="D:/xml";
		}
		baseVersion=configProp.get("baseversion").toString();
	}


	/**
	 * 
	 * @return Map <String, String[]> with <1, list of all unique files in a directory>
	 * initializes VersionSet with <"File Name" , List of all versions in the base dir>
	 * initializes VersionFileIndex with <versionDirName,<filename,filePath>>
	 */
	private Map<String, Object[]> scanFiles() {
		
		Map<String, Object[]> data = new HashMap<String, Object[]>();

		File[] fList = directory.listFiles();
		
		List<String> versionSetAll = new ArrayList<String>();
		versionSetAll.add("File Name");

		for (File dir : fList) {
			if (dir.isDirectory()) {
				versionSet.add(dir.getName());
				Map<String, String> xmlfiles = new HashMap<>();
				for (File innerfile : dir.listFiles()) {
					xmlfiles.put(innerfile.getName(), innerfile.getAbsolutePath());
				}
				versionFileIndex.put(dir.getName(), xmlfiles);
			}
		}
		versionSetAll.addAll(versionSet);
		String[] inputArray = new String[versionSetAll.size()];
		versionSetAll.toArray(inputArray);
		data.put("1", inputArray);
		
		return data;
	}

	/**
	 * 
	 * @param data The Map containing details of files scanned in ScanFiles.
	 * @param rownum the file roe Index pointer 
	 */
	
	private Map<String, Object[]> identifyDuplicateFiles(Map<String, Object[]> data, Integer rownum) {

		Set<String> versionSet = versionFileIndex.keySet();
		for (String version : versionSet) {
			Map<String, String> files = versionFileIndex.get(version);
			Set<String> fileSet = files.keySet();

			for (String fileName : fileSet) {

				List<String> fileStaus = new ArrayList<>();
				if (!comparedFiles.contains(fileName)) {

					System.out.print(fileName + ",,");
					fileStaus.add(fileName);
				}

				for (String innerVersion : versionSet) {
					WrapperFile wrapperFile = new WrapperFile();
					if (versionFileIndex.get(innerVersion).get(fileName) != null && !comparedFiles.contains(fileName)) {
						System.out.print(",Yes");
						fileStaus.add("Yes");
						wrapperFile.setVersion(innerVersion);
						wrapperFile.setFileName(fileName);
						wrapperFile.setFilePath(versionFileIndex.get(innerVersion).get(fileName));

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
		return data;
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
			for (String tagName : tagXpathMap.keySet()) {
			//for (String expression : tagForScan.keySet()) {
				String expression = tagXpathMap.get(tagName);
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
		tagHeaderList.add("File Name");
		tagHeaderList.add("Tag Name");
		tagHeaderList.add("Tag Value");
		tagHeaderList.addAll(versionSet);
		
		String[] headerArray = new String[tagHeaderList.size()];
		tagHeaderList.toArray(headerArray);
		Integer rowNum = 1;
		data.put("1", headerArray);
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
						for (String tagName :  tagXpathMap.keySet()) {
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
		XLWriter.addToSheet(workbook, summarySheet, data);
	}
}
