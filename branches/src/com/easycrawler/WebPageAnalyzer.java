package com.easycrawler;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.analyzepic.PicAnalyzer;

public class WebPageAnalyzer {
	private static String domain;
	private static String host;
	private static String baseUrl;
	private static int pageSize;
	private int totalPageNum;
	private String verifyCode;
	private NodeList list;
	private HttpHelper httpHelper;

	public WebPageAnalyzer() {
		domain = ConfigHelper.getString("Domain");
		host = ConfigHelper.getString("Host");
		httpHelper = new HttpHelper();
		pageSize = Integer.valueOf(ConfigHelper.getString("PageSize"));
		baseUrl = host
				+ "icp/publish/query/icpMemoInfo_searchExecute.action?page.pageSize="
				+ String.valueOf(pageSize) + "&siteUrl=" + domain;
		verifyCode = getVerifyCode();
		totalPageNum = getTotalPageNum();
		Logger.write("WebPageAnalyzer.WebPageAnalyzer() - baseUrl: " + baseUrl
				+ "\n totalPageNum: " + totalPageNum, Logger.ERROR);
	}

	private boolean hasChildNode() {
		return (list.size() > 0) ? true : false;
	}

	private int getChildNodeNum() {
		return (list.elementAt(0).getChildren().elementAt(1).getChildren()
				.size() - 5) / 2;
	}

	private void setNodeList(String webPageContent, String attributeName,
			String attributeValue) {
		HasAttributeFilter haf = new HasAttributeFilter(attributeName,
				attributeValue);
		Parser parser;
		try {
			parser = new Parser(webPageContent);
			list = parser.extractAllNodesThatMatch(haf);
		} catch (ParserException e) {
			Logger.write("WebPageAnalyzer.setNodeList() - " + e.getMessage(),
					Logger.ERROR);
			e.printStackTrace();
		}
	}

	private NodeList getNodeList() {
		return list;
	}

	private String getVerifyCode() {
		String Url = "http://www.miibeian.gov.cn/validateCode";
		PicAnalyzer ap = new PicAnalyzer();
		String result = String.valueOf(ap.getResult(httpHelper
				.getResponseAsStream(Url)));
		Logger.write(
				"WebPageAnalyzer.getVerifyCode() - Verify Code: " + result,
				Logger.INFO);
		return result;
	}

	private int getTotalPageNum() {
		String htmlContent = getValidWebpage(1, "id", "button1");
		setNodeList(htmlContent, "class", "red");
		int totalPageNum = Integer.valueOf(list.elementAt(0)
				.toPlainTextString())
				/ pageSize + 1;
		// int totalPageNumStart = htmlContent.indexOf("第 1/") + 3;
		//Logger.write("WebPageAnalyzer.getTotalPageNum() - totalPageNumStart: "
		// + totalPageNumStart, Logger.DEBUG);
		// int totalPageNumLength = htmlContent.indexOf("页", totalPageNumStart);
		// int totalPageNum = Integer.valueOf(htmlContent.substring(
		// totalPageNumStart, totalPageNumLength));
		Logger.write("WebPageAnalyzer.getTotalPageNum() - Total Page Num: "
				+ totalPageNum, Logger.INFO);
		return totalPageNum;

	}

	private String getValidWebpage(int pageNum, String attributeName,
			String attributeValue) {
		String Url = baseUrl + "&verifyCode=" + verifyCode + "&pageNo="
				+ String.valueOf(pageNum);
		String htmlContent = "";
		htmlContent = httpHelper.getResponseAsString(Url);
		// Logger.write("WebPageAnalyzer.getValidWebpage() - Content: " +
		// htmlContent,
		// Logger.DEBUG);
		setNodeList(htmlContent, attributeName, attributeValue);
		while (hasChildNode()) {
			// reget verifyCode
			verifyCode = getVerifyCode();
			Url = baseUrl + "&verifyCode=" + verifyCode + "&pageNo="
					+ String.valueOf(pageNum);
			htmlContent = httpHelper.getResponseAsString(Url);
			setNodeList(htmlContent, attributeName, attributeValue);
		}
		Logger.write("WebPageAnalyzer.getValidWebpage() - Fetch URL: " + Url,
				Logger.INFO);
		return htmlContent;
	}

	public void produceResultFile() {
		String htmlContent = "";
		int pageNum = 1;
		int errorTimes = 0;
		String[] cellUrl = new String[pageSize];
		while (pageNum <= totalPageNum) {
			htmlContent = getValidWebpage(pageNum, "id", "button1");
			cellUrl = getCellUrl(htmlContent);
			if (cellUrl[0] != "Fail") {
				errorTimes = 0;
				produceFile(cellUrl, pageNum);
				pageNum++;
			} else {
				if (++errorTimes > 5) {
					Logger.write(
							"WebPageAnalyzer.produceResultFile() - Failed page number: "
									+ pageNum, Logger.ERROR);
					return;
				}
			}
		}

	}

	private String[] getCellUrl(String htmlContent) {
		setNodeList(htmlContent, "class", "a");
		String[] cellUrl = new String[getChildNodeNum()];
		if (hasChildNode()) {
			for (int i = 0; i < getChildNodeNum(); i++)
				cellUrl[i] = host
						+ ((Tag) (getNodeList().elementAt(0).getChildren()
								.elementAt(1).getChildren()
								.elementAt(2 * i + 3).getChildren()
								.elementAt(7).getChildren().elementAt(1)))
								.getAttribute("href");
		} else {
			cellUrl[0] = "Fail";
		}
		return cellUrl;
	}

	private void produceFile(String[] Url, int pageNum) {
		String htmlContent = "";
		int errorTimes = 0;
		ContainerHelper container = new ContainerHelper(pageNum);
		for (int i = 0; i < Url.length; i++) {
			htmlContent = httpHelper.getResponseAsString(Url[i]);
			setNodeList(htmlContent, "class", "a");
			if (getNodeList().size() == 2) {
				errorTimes = 0;
				// Append id into db
				container.append(Url[i].substring(Url[i].indexOf("id=") + 3),
						getNodeList());
			} else {
				i--;
				if (++errorTimes > 5) {
					container.append("Failed at page: " + pageNum + "\r\n"
							+ htmlContent);
					Logger.write("WebPageAnalyzer - File Failed at page: "
							+ pageNum + "\r\n" + htmlContent, Logger.INFO);
					container.close();
					return;
				}
			}
		}
		container.close();
	}
}