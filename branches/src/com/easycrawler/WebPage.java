package com.easycrawler;

import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class WebPage {
	
	protected NodeList transferToNodeList(String htmlContent,
			String attributeName, String attributeValue) {
		NodeList list = new NodeList();
		HasAttributeFilter haf = new HasAttributeFilter(attributeName,
				attributeValue);
		Parser parser;
		try {
			parser = new Parser(htmlContent);
			list = parser.extractAllNodesThatMatch(haf);
		} catch (ParserException e) {
			Logger.write("ListPage.transferToNodeList() - " + e.getMessage(),
					Logger.ERROR);
			e.printStackTrace();
		}
		return list;
	}

	protected boolean hasAttribute(String htmlContent, String attributeName,
			String attributeValue) {
		NodeList list = new NodeList();
		HasAttributeFilter haf = new HasAttributeFilter(attributeName,
				attributeValue);
		Parser parser;
		try {
			parser = new Parser(htmlContent);
			list = parser.extractAllNodesThatMatch(haf);
		} catch (ParserException e) {
			Logger.write("ListPage.hasAttribute() - " + e.getMessage(),
					Logger.ERROR);
			e.printStackTrace();
		}
		return (list.size() > 0) ? true : false;
	}
}
