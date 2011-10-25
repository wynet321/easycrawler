package com.easycrawler;

import org.htmlparser.util.NodeList;

public class DetailPage extends WebPage {

	private HttpHelper httpHelper;

	public DetailPage() {
		httpHelper = new HttpHelper();
	}

	public NodeList getNodeListWithAttribute(String Url, String attributeName,
			String attributeValue) {
		Logger.write(
				"MiibeianWebPage.getValidWebpageWithAttribute() - Start fetching page: "
						+ Url, Logger.DEBUG);
		String htmlContent = "";
		do {
			htmlContent = httpHelper.getResponseAsString(Url);
		} while (!hasAttribute(htmlContent, attributeName, attributeValue));
		Logger.write(
				"MiibeianWebPage.getValidWebpage() - Completed fetching page: "
						+ Url, Logger.INFO);
		NodeList list = transferToNodeList(htmlContent, attributeName,
				attributeValue);
		return list;
	}
}
