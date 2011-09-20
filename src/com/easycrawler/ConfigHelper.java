package com.easycrawler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConfigHelper extends DOMHelper {
	private static String FILENAME = "Config.xml";

	public static Document getDoc() {
		return getDoc("bin/"+FILENAME);
	}

	public static String getString(String tagName) {

		try {
			Element elm = (Element) getDoc().getElementsByTagName(tagName)
					.item(0);
			return elm.getFirstChild().getNodeValue();
		} catch (Exception e) {
			System.out.println("Failed to getString!");
			e.printStackTrace();
			return null;
		}

	}
}
