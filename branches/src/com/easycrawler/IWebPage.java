package com.easycrawler;

import org.htmlparser.util.NodeList;

public interface IWebPage {
	public String getValidWebpageWithAttribute(int pageNum,
			String attributeName, String attributeValue);

	public String getValidWebpageWithoutAttribute(int pageNum,
			String attributeName, String attributeValue);

	public String[] getURLFromPage(String htmlContent);

	public NodeList transferToNodeList(String htmlContent,
			String attributeName, String attributeValue);

}
