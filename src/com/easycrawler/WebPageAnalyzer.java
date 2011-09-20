package com.easycrawler;

import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class WebPageAnalyzer{
	public static NodeList list;

	public static boolean hasChildNode() {
		return (list.size() > 0) ? true : false;
	}

	public static void setNodeList(String webPageContent, String attributeName,
			String attributeValue) {
		HasAttributeFilter haf = new HasAttributeFilter(attributeName,
				attributeValue);
		Parser parser;
		try {
			parser = new Parser(webPageContent);
			list = parser.extractAllNodesThatMatch(haf);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static NodeList getNodeList() {
		return list;
	}
}
