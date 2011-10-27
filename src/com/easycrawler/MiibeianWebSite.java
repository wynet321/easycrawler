package com.easycrawler;

import org.htmlparser.util.NodeList;

import com.easycrawler.helper.ConfigXMLHelper;
import com.easycrawler.helper.LogHelper;

public class MiibeianWebSite extends WebSiteBase {
	private static String domain;
	private int totalPageNum;
	ListWebPage listPage;
	private int threadNum;

	public MiibeianWebSite() {
		domain = ConfigXMLHelper.getString("Domain");
		threadNum = ConfigXMLHelper.getInt("ThreadNumber");
		listPage = new ListWebPage(domain);
		totalPageNum = getTotalPageNum();
	}

	private int getTotalPageNum() {
		LogHelper
				.write("MiibeianWebCrawler.getTotalPageNum() - Start getting total page number",
						LogHelper.DEBUG);
		NodeList list = listPage.getNodeListByPageNumWithAttribute(1, "class",
				"red");
		totalPageNum = Integer.valueOf(list.elementAt(0).toPlainTextString())
				/ listPage.getPageSize() + 1;
		LogHelper.write(
				"MiibeianWebCrawler.getTotalPageNum() - Completed getting total page number: "
						+ totalPageNum, LogHelper.DEBUG);
		return totalPageNum;
	}

	public void crawl() {
		LogHelper.write(
				"MiibeianWebCrawler.crawl() - Start getting all pages.",
				LogHelper.DEBUG);
		String htmlContent = "";
		int currentThreadName = Integer.valueOf(Thread.currentThread()
				.getName());
		int pageNumPerThread = totalPageNum / threadNum;
		int threadTotalPageNum = currentThreadName * pageNumPerThread;
		if (currentThreadName == threadNum)
			threadTotalPageNum = totalPageNum;
		int pageNum = (currentThreadName - 1) * pageNumPerThread + 1;
		int errorTimes = 0;
		String[] cellUrl = new String[listPage.getPageSize()];
		while (pageNum <= threadTotalPageNum) {
			htmlContent = listPage.getValidWebpageWithoutAttribute(pageNum,
					"id", "button1");
			cellUrl = listPage.getURLFromPage(htmlContent);
			LogHelper.write("cellUrl:" + cellUrl[0], LogHelper.ERROR);
			if (cellUrl[0] != "Fail") {
				errorTimes = 0;
				writeToContainer(cellUrl, pageNum);
				pageNum++;
			} else {
				if (++errorTimes > 5) {
					LogHelper.write(
							"MiibeianWebCrawler.crawl() - Failed at page "
									+ pageNum + "for at least 5 times.",
							LogHelper.ERROR);
					break;
				}
			}
		}
		LogHelper.write(
				"MiibeianWebCrawler.crawl() - Completed getting all pages.",
				LogHelper.DEBUG);
	}

	private void writeToContainer(String[] Url, int pageNum) {
		LogHelper.write(
				"MiibeianWebCrawler.writeToContainer() - Start writing page:"
						+ pageNum, LogHelper.DEBUG);
		int errorTimes = 0;
		DetailWebPage detailWebPage = new DetailWebPage();
		FileContainer container = new FileContainer(pageNum);
		for (int i = 0; i < Url.length; ++i) {
			NodeList list = detailWebPage.getNodeListWithAttribute(Url[i],
					"class", "a");
			if (list.size() == 2) {
				errorTimes = 0;
				container.append(Url[i].substring(Url[i].indexOf("id=") + 3),
						list);
			} else {
				i--;
				if (++errorTimes > 5) {
					container
							.append("MiibeianWebCrawler.writeToContainer() - Failed at page: "
									+ pageNum + "\r\n" + Url[i]);
					LogHelper
							.write("MiibeianWebCrawler.writeToContainer() - Failed at page: "
									+ pageNum + "\r\n" + Url[i], LogHelper.INFO);
					break;
				}
			}
		}
		container.close();
		LogHelper.write(
				"MiibeianWebCrawler.writeToContainer() - Completed writing page: "
						+ pageNum, LogHelper.DEBUG);
	}
}
