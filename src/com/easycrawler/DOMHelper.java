package com.easycrawler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class DOMHelper {
	private static DocumentBuilder getDocBuilder() {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = null;

		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (Exception e1) {
			System.out.println("Failed to create doc factory!");
			e1.printStackTrace();
			System.exit(1);
		}
		return docBuilder;
	}

	protected static Document getDoc(String fileName) {

		FileInputStream fin = null;
		try {
			fin = new FileInputStream(fileName);
		} catch (FileNotFoundException e2) {
			System.out.println("XML can't be found!");
			e2.printStackTrace();
			System.exit(1);
		}
		Document doc = null;
		try {
			doc = getDocBuilder().parse(fin);
		} catch (Exception e3) {
			System.out.println("Failed to analyse xml!");
			e3.printStackTrace();
			System.exit(1);
		}
		return doc;
	}

	protected static Document getDoc(InputStream is) {
		Document doc = null;
		try {
			doc = getDocBuilder().parse(is);
		} catch (Exception e3) {
			System.out.println("Failed to analyse xml!");
			e3.printStackTrace();
			System.exit(1);
		}
		return doc;
	}

	// public static String getString(String tagName) {
	//
	// try {
	// Element elm = (Element) getDoc().getElementsByTagName(tagName)
	// .item(0);
	// return elm.getFirstChild().getNodeValue();
	// } catch (Exception e) {
	// System.out.println("Failed to getString!");
	// e.printStackTrace();
	// return null;
	// }
	//
	// }
}
