package com.easycrawler;

import org.htmlparser.Tag;

import com.analyzepic.AnalyzePic;

public class ProduceDB {
	// customize if need
	private static String domain;

	// no need to change, hard code
	private static String host;
	// private static String resultFileName = "0.txt";
	private static String baseUrl;

	public static void main(String[] args) {
		domain = ConfigHelper.getString("Domain");
		host = ConfigHelper.getString("Host");
		baseUrl = host
				+ "icp/publish/query/icpMemoInfo_searchExecute.action?siteUrl="
				+ domain;
		String verifyCode = getVerifyCode();
		int totalPageNum = getTotalPageNum(verifyCode);
		produceResultFile(verifyCode, totalPageNum);
	}

	private static String getVerifyCode() {
		String Url = "http://www.miibeian.gov.cn/validateCode";
		AnalyzePic ap = new AnalyzePic();
		String result = String.valueOf(ap.getResult(HttpHelper
				.getResponseAsStream(Url)));
		// System.out.println(result);
		return result;
	}

	private static int getTotalPageNum(String verifyCode) {
		String htmlContent = getValidWebpage(verifyCode, "id", "button1");
		int totalPageNumStart = htmlContent.indexOf("&nbsp;1/") + 8;
		int totalPageNumLength = htmlContent.indexOf("&nbsp;",
				totalPageNumStart);
		int totalPageNum = Integer.valueOf(htmlContent.substring(
				totalPageNumStart, totalPageNumLength));

		return totalPageNum;

	}

	private static String getValidWebpage(String verifyCode,
			String attributeName, String attributeValue) {
		String Url = baseUrl + "&verifyCode=" + verifyCode + "&pageNo=1";
		String htmlContent = "";
		htmlContent = HttpHelper.getResponseAsString(Url);
		WebPageAnalyzer.setNodeList(htmlContent, attributeName, attributeValue);
		while (WebPageAnalyzer.hasChildNode()) {
			// reget verifyCode
			verifyCode = getVerifyCode();
			Url = baseUrl + "&verifyCode=" + verifyCode + "&pageNo=1";
			htmlContent = HttpHelper.getResponseAsString(Url);
			WebPageAnalyzer.setNodeList(htmlContent, "id", "button1");
		}
		return htmlContent;
	}

	private static void produceResultFile(String verifyCode, int totalPageNum) {
		String htmlContent = "";
		int pageNum = 1;
		int errorTimes = 0;
		String[] cellUrl = new String[20];
		while (pageNum < totalPageNum) {
			htmlContent = getValidWebpage(verifyCode, "id", "button1");
			cellUrl = getCellUrl(htmlContent);
			if (cellUrl[0] != "Fail") {
				errorTimes = 0;
				produceFile(cellUrl, pageNum);
				pageNum++;
			} else {
				if (errorTimes++ > 5) {
					System.out.println("Failed to produce at page " + pageNum);
					return;
				}
			}
		}

	}

	private static String[] getCellUrl(String htmlContent) {
		String[] cellUrl = new String[20];
		WebPageAnalyzer.setNodeList(htmlContent, "class", "a");
		if (WebPageAnalyzer.hasChildNode()) {
			for (int i = 0; i < 20; i++)
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
				System.out.println(errorTimes);
				ContainerHelper.append("Failed at page: " + pageNum + "\r\n"
						+ htmlContent, pageNum);
				i--;
				if (errorTimes++ > 5) {
					ContainerHelper.close();
					return;
				}
			}
		}
		ContainerHelper.close();
	}
}
