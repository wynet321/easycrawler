package com.easycrawler;

import org.htmlparser.util.NodeList;

public class MiibeianWebCrawler {
	private static String domain;
	private int totalPageNum;
	MiibeianWebPage miibeianWebPage;
	private HttpHelper httpHelper;
	private int threadNum;

	public MiibeianWebCrawler() {
		domain = ConfigHelper.getString("Domain");
		threadNum = ConfigHelper.getInt("ThreadNumber");
		totalPageNum = getTotalPageNum();
		miibeianWebPage = new MiibeianWebPage(domain);
	}

	private int getTotalPageNum() {
		Logger.write(
				"WebPageAnalyzer.getTotalPageNum() - Start getting total page number",
				Logger.DEBUG);
		NodeList list = miibeianWebPage.getNodeListByPageNumWithAttribute(1,
				"class", "red");
		totalPageNum = Integer.valueOf(list.elementAt(0).toPlainTextString())
				/ miibeianWebPage.getPageSize() + 1;
		Logger.write(
				"WebPageAnalyzer.getTotalPageNum() - Completed getting total page number: "
						+ totalPageNum, Logger.DEBUG);
		return totalPageNum;
	}

	public void crawl() {
		Logger.write(
				"WebPageAnalyzer.produceResultFile() - Start getting all pages.",
				Logger.DEBUG);
		String htmlContent = "";
		int currentThreadName = Integer.valueOf(Thread.currentThread()
				.getName());
		int pageNumPerThread = totalPageNum / threadNum;
		int threadTotalPageNum = currentThreadName * pageNumPerThread;
		if (currentThreadName == threadNum)
			threadTotalPageNum = totalPageNum;
		int pageNum = (currentThreadName - 1) * pageNumPerThread + 1;
		int errorTimes = 0;
		String[] cellUrl = new String[miibeianWebPage.getPageSize()];
		while (pageNum <= threadTotalPageNum) {
			htmlContent = miibeianWebPage.getValidWebpageWithoutAttribute(
					pageNum, "id", "button1");
			cellUrl = miibeianWebPage.getURLFromPage(htmlContent);
			if (cellUrl[0] != "Fail") {
				errorTimes = 0;
				writeToContainer(cellUrl, pageNum);
				pageNum++;
			} else {
				if (++errorTimes > 5) {
					Logger.write(
							"WebPageAnalyzer.produceResultFile() - Failed at page "
									+ pageNum + "for at least 5 times.",
							Logger.ERROR);
					break;
				}
			}
		}
		Logger.write(
				"WebPageAnalyzer.produceResultFile() - Completed getting all pages.",
				Logger.DEBUG);
	}

	private void writeToContainer(String[] Url, int pageNum) {
		Logger.write("WebPageAnalyzer.produceFile() - Start writing page:"
				+ pageNum, Logger.DEBUG);
		String htmlContent = "";
		int errorTimes = 0;
		FileContainer container = new FileContainer(pageNum);
		for (int i = 0; i < Url.length; i++) {
			htmlContent = httpHelper.getResponseAsString(Url[i]);
			NodeList list = miibeianWebPage.transferToNodeList(htmlContent,
					"class", "a");
			if (list.size() == 2) {
				errorTimes = 0;
				// Append id into db
				container.append(Url[i].substring(Url[i].indexOf("id=") + 3),
						list);
			} else {
				i--;
				if (++errorTimes > 5) {
					container
							.append("WebPageAnalyzer.produceFile() - Failed at page: "
									+ pageNum + "\r\n" + htmlContent);
					Logger.write(
							"WebPageAnalyzer.produceFile() - Failed at page: "
									+ pageNum + "\r\n" + htmlContent,
							Logger.INFO);
					break;
				}
			}
		}
		container.close();
		Logger.write("WebPageAnalyzer.produceFile() - Completed writing page: "
				+ pageNum, Logger.DEBUG);
	}
}
