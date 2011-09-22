package com.easycrawler;

import org.htmlparser.Tag;

import com.analyzepic.AnalyzePic;

public class ProduceDB {
	private static String domain;
	private static String host;
	private static String baseUrl;
	private static int pageSize;

	public static void main(String[] args) {
		domain = ConfigHelper.getString("Domain");
		host = ConfigHelper.getString("Host");
		pageSize = Integer.valueOf(ConfigHelper.getString("PageSize"));
		baseUrl = host
				+ "icp/publish/query/icpMemoInfo_searchExecute.action?page.pageSize="
				+ String.valueOf(pageSize) + "&siteUrl=" + domain;
		String verifyCode = getVerifyCode();
		int totalPageNum = getTotalPageNum(verifyCode);
		produceResultFile(verifyCode, totalPageNum);
	}

	private static String getVerifyCode() {
		String Url = "http://www.miibeian.gov.cn/validateCode";
		AnalyzePic ap = new AnalyzePic();
		String result = String.valueOf(ap.getResult(HttpHelper
				.getResponseAsStream(Url)));
		Logger.write("Verify Code: " + result, Logger.INFO);
		return result;
	}

	private static int getTotalPageNum(String verifyCode) {
		String htmlContent = getValidWebpage(verifyCode, 1, "id", "button1");
		int totalPageNumStart = htmlContent.indexOf("&nbsp;1/") + 8;
		int totalPageNumLength = htmlContent.indexOf("&nbsp;",
				totalPageNumStart);
		int totalPageNum = Integer.valueOf(htmlContent.substring(
				totalPageNumStart, totalPageNumLength));
		Logger.write("Total Page Num: " + totalPageNum, Logger.INFO);
		System.out.println("Total Page Num: " + totalPageNum);
		return totalPageNum;

	}

	private static String getValidWebpage(String verifyCode, int pageNum,
			String attributeName, String attributeValue) {
		String Url = baseUrl + "&verifyCode=" + verifyCode + "&pageNo="
				+ String.valueOf(pageNum);
		String htmlContent = "";
		htmlContent = HttpHelper.getResponseAsString(Url);
		WebPageAnalyzer.setNodeList(htmlContent, attributeName, attributeValue);
		while (WebPageAnalyzer.hasChildNode()) {
			// reget verifyCode
			verifyCode = getVerifyCode();
			Url = baseUrl + "&verifyCode=" + verifyCode + "&pageNo="
					+ String.valueOf(pageNum);
			htmlContent = HttpHelper.getResponseAsString(Url);
			WebPageAnalyzer.setNodeList(htmlContent, "id", "button1");
		}
		Logger.write("Fetch URL: " + Url, Logger.INFO);
		return htmlContent;
	}

	private static void produceResultFile(String verifyCode, int totalPageNum) {
		String htmlContent = "";
		int pageNum = 1;
		int errorTimes = 0;
		String[] cellUrl = new String[pageSize];
		while (pageNum < totalPageNum) {
			htmlContent = getValidWebpage(verifyCode, pageNum, "id", "button1");
			cellUrl = getCellUrl(htmlContent);
			if (cellUrl[0] != "Fail") {
				errorTimes = 0;
				produceFile(cellUrl, pageNum);
				pageNum++;
			} else {
				errorTimes++;
				if (errorTimes++ > 5) {
					Logger.write("Failed page number: " + pageNum, Logger.ERROR);
					System.out.println("Failed to produce at page " + pageNum);
					return;
				}
			}
		}

	}

	private static String[] getCellUrl(String htmlContent) {
		String[] cellUrl = new String[pageSize];
		WebPageAnalyzer.setNodeList(htmlContent, "class", "a");
		if (WebPageAnalyzer.hasChildNode()) {
			for (int i = 0; i < pageSize; i++)
				cellUrl[i] = host
						+ ((Tag) (WebPageAnalyzer.getNodeList().elementAt(0)
								.getChildren().elementAt(1).getChildren()
								.elementAt(2 * i + 3).getChildren()
								.elementAt(7).getChildren().elementAt(1)))
								.getAttribute("href");
		} else {
			cellUrl[0] = "Fail";
		}
		return cellUrl;
	}

	private static void produceFile(String[] Url, int pageNum) {
		String htmlContent = "";
		int errorTimes = 0;
		System.out.println("Current PageNum is " + pageNum);
		for (int i = 0; i < Url.length; i++) {
			htmlContent = HttpHelper.getResponseAsString(Url[i]);
			WebPageAnalyzer.setNodeList(htmlContent, "class", "a");
			if (WebPageAnalyzer.getNodeList().size() == 2) {
				errorTimes = 0;
				ContainerHelper.append((CharSequence) WebPageAnalyzer
						.getNodeList().elementAt(0).toHtml()
						+ "\r\n", pageNum);
				ContainerHelper.append((CharSequence) WebPageAnalyzer
						.getNodeList().elementAt(1).toHtml()
						+ "\r\n", pageNum);
			} else {
				System.out.println(errorTimes++);
				ContainerHelper.append("Failed at page: " + pageNum + "\r\n"
						+ htmlContent, pageNum);
				i--;
				if (errorTimes > 5) {
					Logger.write("ProduceDB-produceFile Failed at page: "
							+ pageNum + "\r\n" + htmlContent, Logger.INFO);
					ContainerHelper.close();
					return;
				}
			}
		}
	}
}
