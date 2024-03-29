package com.easycrawler.helper;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class XMLHelperBase {
	private static DocumentBuilder getDocBuilder() {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = null;

		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (Exception e) {
			LogHelper.write(
					"DOMHelper-getDocBuilder: Failed to create doc factory!\r\n"
							+ e.getMessage(), LogHelper.ERROR);
			e.printStackTrace();
			System.exit(1);
		}
		return docBuilder;
	}

	protected static Document getDoc(String fileName) {

		FileInputStream fin = null;
		try {
			fin = new FileInputStream(fileName);
		} catch (Exception e) {
			LogHelper.write("DOMHelper-getDoc(String): XML can't be found!\r\n"
					+ e.getMessage(), LogHelper.ERROR);
			e.printStackTrace();
			System.exit(1);
		}
		Document doc = null;
		try {
			doc = getDocBuilder().parse(fin);
		} catch (Exception e) {
			LogHelper.write(
					"DOMHelper-getDoc(String): Failed to analyse xml!\r\n"
							+ e.getMessage(), LogHelper.ERROR);
			e.printStackTrace();
			System.exit(1);
		}
		return doc;
	}

	protected static Document getDoc(InputStream is) {
		Document doc = null;
		try {
			doc = getDocBuilder().parse(is);
		} catch (Exception e) {
			LogHelper.write(
					"DOMHelper-getDoc(InputStream): Failed to analyse xml!\r\n"
							+ e.getMessage(), LogHelper.ERROR);
			e.printStackTrace();
			System.exit(1);
		}
		return doc;
	}
}
