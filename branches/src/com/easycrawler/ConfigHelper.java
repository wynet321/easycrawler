package com.easycrawler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConfigHelper extends DOMHelper {
	private static String FILENAME = "Config.xml";

	public static Document getDoc() {
		return getDoc("bin/" + FILENAME);
	}

	public static String getString(String tagName) {
		try {
			Element elm = (Element) getDoc().getElementsByTagName(tagName)
					.item(0);
			return elm.getFirstChild().getNodeValue();
		} catch (Exception e) {
			Logger.write("ConfigHelper.getString() - " + e.getMessage(),
					Logger.ERROR);
			e.printStackTrace();
			return null;
		}
	}

	public static int getInt(String tagName) {
		return Integer.valueOf(getString(tagName));
	}
}
