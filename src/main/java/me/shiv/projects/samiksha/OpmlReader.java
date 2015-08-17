package me.shiv.projects.samiksha;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OpmlReader {

	public static boolean isOpmlFile(File f) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document xmlDocument = null;

		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		try {
			xmlDocument = builder.parse(new FileInputStream(f));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = "/opml/body/outline";

		try {
			NodeList outlines = (NodeList) xPath.compile(expression).evaluate(
					xmlDocument, XPathConstants.NODESET);

			if (outlines.getLength() > 0) {
				return true;
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return false;
	}
}

