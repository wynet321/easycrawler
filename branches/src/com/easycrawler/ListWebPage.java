package com.easycrawler;

import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import com.easycrawler.helper.ConfigXMLHelper;
import com.easycrawler.helper.LogHelper;

public class ListWebPage extends WebPageBase {

	private String baseUrl;
	private HttpHandler httpHelper;
	private String host;
	private static int pageSize;
	private String verifyCode;
	private VerifyCodeWebPage verifyCodePage;

	public int getPageSize() {
		return pageSize;
	}

	public ListWebPage(String domain) {
		host = ConfigXMLHelper.getString("Host");
		pageSize = Integer.valueOf(ConfigXMLHelper.getString("PageSize"));
		baseUrl = host
				+ "icp/publish/query/icpMemoInfo_searchExecute.action?page.pageSize="
				+ String.valueOf(pageSize) + "&siteUrl=" + domain;
		httpHelper = new HttpHandler();
		verifyCodePage = new VerifyCodeWebPage();
		verifyCode = verifyCodePage.getVerifyCode(httpHelper);
	}

	public NodeList getNodeListByPageNumWithAttribute(int pageNum,
			String attributeName, String attributeValue) {
		String htmlContent = getValidWebpageWithAttribute(1, attributeName,
				attributeValue);
		NodeList list = transferToNodeList(htmlContent, attributeName,
				attributeValue);
		return list;
	}

	public String getValidWebpageWithAttribute(int pageNum,
			String attributeName, String attributeValue) {
		String Url = baseUrl + "&pageNo=" + String.valueOf(pageNum)
				+ "&verifyCode=" + verifyCode;
		LogHelper.write(
				"ListPage.getValidWebpageWithAttribute() - Start fetching page: "
						+ pageNum, LogHelper.DEBUG);
		String htmlContent = httpHelper.getResponseAsString(Url);
		while (!hasAttribute(htmlContent, attributeName, attributeValue)) {
			LogHelper.write(
					"ListPage.getValidWebpageWithAttribute() - VerifyCode is wrong, get it again.",
					LogHelper.ERROR);
			LogHelper.write(htmlContent, LogHelper.ERROR);
			verifyCode = verifyCodePage.getVerifyCode(httpHelper);
			Url = baseUrl + "&pageNo=" + String.valueOf(pageNum)
					+ "&verifyCode=" + verifyCode;
			htmlContent = httpHelper.getResponseAsString(Url);
		}
		LogHelper.write("ListPage.getValidWebpage() - Completed fetching page: "
				+ pageNum, LogHelper.INFO);
		return htmlContent;
	}

	public String getValidWebpageWithoutAttribute(int pageNum,
			String attributeName, String attributeValue) {
		String Url = baseUrl + "&pageNo=" + String.valueOf(pageNum)
				+ "&verifyCode=" + verifyCode;
		LogHelper.write(
				"ListPage.getValidWebpageWithoutAttribute() - Start fetching page: "
						+ pageNum, LogHelper.DEBUG);
		String htmlContent = httpHelper.getResponseAsString(Url);
		while (hasAttribute(htmlContent, attributeName, attributeValue)) {
			LogHelper.write(
					"ListPage.getValidWebpageWithoutAttribute() - VerifyCode is wrong, get it again.",
					LogHelper.ERROR);
			verifyCode = verifyCodePage.getVerifyCode(httpHelper);
			Url = baseUrl + "&pageNo=" + String.valueOf(pageNum)
					+ "&verifyCode=" + verifyCode;
			htmlContent = httpHelper.getResponseAsString(Url);
		}
		LogHelper.write(
				"ListPage.getValidWebpageWithoutAttribute() - Completed fetching page: "
						+ pageNum, LogHelper.INFO);
		return htmlContent;
	}

	public String[] getURLFromPage(String htmlContent) {
		String[] cellUrl = { "Fail" };
		NodeList list = transferToNodeList(htmlContent, "class", "a");
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
