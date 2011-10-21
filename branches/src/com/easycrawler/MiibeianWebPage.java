package com.easycrawler;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class MiibeianWebPage implements IWebPage {

	private String baseUrl;
	private HttpHelper httpHelper;
	private String host;
	private static int pageSize;
	private String verifyCode;

	public int getPageSize() {
		return pageSize;
	}

	public MiibeianWebPage(String domain) {
		host = ConfigHelper.getString("Host");
		pageSize = Integer.valueOf(ConfigHelper.getString("PageSize"));
		baseUrl = host
				+ "icp/publish/query/icpMemoInfo_searchExecute.action?page.pageSize="
				+ String.valueOf(pageSize) + "&siteUrl=" + domain;
		httpHelper = new HttpHelper();
	}

	public NodeList getNodeListByPageNumWithAttribute(int pageNum,
			String attributeName, String attributeValue) {
		String htmlContent = getValidWebpageWithAttribute(1, "class", "red");
		NodeList list = transferToNodeList(htmlContent, "class", "red");
		return list;
	}

	public NodeList transferToNodeList(String htmlContent,
			String attributeName, String attributeValue) {
		NodeList list = new NodeList();
		HasAttributeFilter haf = new HasAttributeFilter(attributeName,
				attributeValue);
		Parser parser;
		try {
			parser = new Parser(htmlContent);
			list = parser.extractAllNodesThatMatch(haf);
		} catch (ParserException e) {
			Logger.write(
					"MiibeianWebPage.transferToNodeList() - " + e.getMessage(),
					Logger.ERROR);
			e.printStackTrace();
		}
		return list;
	}

	private String getVerifyCode() {
		Pic pic = new Pic();
		String result = pic.getResult();
		Logger.write(
				"MiibeianWebPage.getVerifyCode() - Verify Code: " + result,
				Logger.INFO);
		return result;
	}

	public String getValidWebpageWithAttribute(int pageNum,
			String attributeName, String attributeValue) {
		String Url = baseUrl + "&pageNo=" + String.valueOf(pageNum)
				+ "&verifyCode=" + verifyCode;
		Logger.write(
				"MiibeianWebPage.getValidWebpageWithAttribute() - Start fetching page: "
						+ pageNum, Logger.DEBUG);
		String htmlContent = "";
		htmlContent = httpHelper.getResponseAsString(Url);
		do {
			Logger.write(
					"MiibeianWebPage.getValidWebpageWithAttribute() - VerifyCode is wrong, get it again.",
					Logger.ERROR);
			verifyCode = getVerifyCode();
			Url = baseUrl + "&pageNo=" + String.valueOf(pageNum)
					+ "&verifyCode=" + verifyCode;
			htmlContent = httpHelper.getResponseAsString(Url);
		} while (!hasAttribute(htmlContent, attributeName, attributeValue));
		Logger.write(
				"MiibeianWebPage.getValidWebpage() - Completed fetching page: "
						+ pageNum, Logger.INFO);
		return htmlContent;
	}

	private boolean hasAttribute(String htmlContent, String attributeName,
			String attributeValue) {
		NodeList list = new NodeList();
		HasAttributeFilter haf = new HasAttributeFilter(attributeName,
				attributeValue);
		Parser parser;
		try {
			parser = new Parser(htmlContent);
			list = parser.extractAllNodesThatMatch(haf);
		} catch (ParserException e) {
			Logger.write("MiibeianWebPage.hasAttribute() - " + e.getMessage(),
					Logger.ERROR);
			e.printStackTrace();
		}
		return (list.size() > 0) ? true : false;
	}

	public String getValidWebpageWithoutAttribute(int pageNum,
			String attributeName, String attributeValue) {
		String Url = baseUrl + "&pageNo=" + String.valueOf(pageNum)
				+ "&verifyCode=" + verifyCode;
		Logger.write(
				"MiibeianWebPage.getValidWebpageWithoutAttribute() - Start fetching page: "
						+ pageNum, Logger.DEBUG);
		String htmlContent = "";
		htmlContent = httpHelper.getResponseAsString(Url);
		do {
			Logger.write(
					"MiibeianWebPage.getValidWebpageWithoutAttribute() - VerifyCode is wrong, get it again.",
					Logger.ERROR);
			verifyCode = getVerifyCode();
			Url = baseUrl + "&pageNo=" + String.valueOf(pageNum)
					+ "&verifyCode=" + verifyCode;
			htmlContent = httpHelper.getResponseAsString(Url);
		} while (hasAttribute(htmlContent, attributeName, attributeValue));
		Logger.write(
				"MiibeianWebPage.getValidWebpageWithoutAttribute() - Completed fetching page: "
						+ pageNum, Logger.INFO);
		return htmlContent;
	}

	public String[] getURLFromPage(String htmlContent) {
		NodeList list = transferToNodeList(htmlContent, "class", "a");
		String[] cellUrl = { "Fail" };
		if (list.size() != 0) {
			int childNodeNum = (list.elementAt(0).getChildren().elementAt(1)
					.getChildren().size() - 5) / 2;
			cellUrl = new String[childNodeNum];

			for (int i = 0; i < childNodeNum; i++)
				cellUrl[i] = host
						+ ((Tag) (list.elementAt(0).getChildren().elementAt(1)
								.getChildren().elementAt(2 * i + 3)
								.getChildren().elementAt(7).getChildren()
								.elementAt(1))).getAttribute("href");
		}
		return cellUrl;
	}
}
