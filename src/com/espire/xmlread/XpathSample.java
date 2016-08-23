package com.espire.xmlread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class XpathSample {
	public static List<String> titleDetails = new ArrayList<>();

	public void getTitle(File inputFile) {

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
					// System.out.println("Title info : "
					// + eElement.getAttribute("Title"));
					titleDetails.add(inputFile.getName()+","+ eElement.getAttribute("Title"));
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

	public ArrayList<File> listFiles() {
		File directory = new File("D:/SysconProduct/LocalWS/java8/xml");
		ArrayList<File> list = new ArrayList<>();
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				list.add(file);
			}
		}
		return list;
	}

	public static void main(String[] args) {
		XpathSample sample = new XpathSample();
		for (File file : sample.listFiles()) {
        sample.getTitle(file);
		}
		
		for (String title : titleDetails) {
			System.out.println(title);
		}
	}
}
