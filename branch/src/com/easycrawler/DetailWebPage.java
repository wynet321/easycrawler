package com.easycrawler;

import org.htmlparser.util.NodeList;

import com.easycrawler.helper.LogHelper;

public class DetailWebPage extends WebPageBase {

	private HttpHandler httpHelper;

	public DetailWebPage() {
		httpHelper = new HttpHandler();
	}

	public NodeList getNodeListWithAttribute(String Url, String attributeName,
			String attributeValue) {
		LogHelper.write(
				"MiibeianWebPage.getValidWebpageWithAttribute() - Start fetching page: "
						+ Url, LogHelper.DEBUG);
		String htmlContent = "";
		do {
			htmlContent = httpHelper.getResponseAsString(Url);
		} while (!hasAttribute(htmlContent, attributeName, attributeValue));
		LogHelper.write(
				"MiibeianWebPage.getValidWebpage() - Completed fetching page: "
						+ Url, LogHelper.INFO);
		NodeList list = transferToNodeList(htmlContent, attributeName,
				attributeValue);
		return list;
	}
}
