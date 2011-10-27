package com.easycrawler;

import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.easycrawler.helper.LogHelper;

public class WebPageBase {

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
			LogHelper.write(
					"ListPage.transferToNodeList() - " + e.getMessage(),
					LogHelper.ERROR);
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
			LogHelper.write("ListPage.hasAttribute() - " + e.getMessage(),
					LogHelper.ERROR);
			e.printStackTrace();
		}
		return (list.size() > 0) ? true : false;
	}
}
