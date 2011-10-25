package com.easycrawler;

import org.htmlparser.util.NodeList;

public class MiibeianWebCrawler {
	private static String domain;
	private int totalPageNum;
	ListPage listPage;
	private int threadNum;

	public MiibeianWebCrawler() {
		domain = ConfigHelper.getString("Domain");
		threadNum = ConfigHelper.getInt("ThreadNumber");
		listPage = new ListPage(domain);
		totalPageNum = getTotalPageNum();
	}

	private int getTotalPageNum() {
		Logger.write(
				"MiibeianWebCrawler.getTotalPageNum() - Start getting total page number",
				Logger.DEBUG);
		NodeList list = listPage.getNodeListByPageNumWithAttribute(1, "class",
				"red");
		totalPageNum = Integer.valueOf(list.elementAt(0).toPlainTextString())
				/ listPage.getPageSize() + 1;
		Logger.write(
				"MiibeianWebCrawler.getTotalPageNum() - Completed getting total page number: "
						+ totalPageNum, Logger.DEBUG);
		return totalPageNum;
	}

	public void crawl() {
		Logger.write("MiibeianWebCrawler.crawl() - Start getting all pages.",
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
		String[] cellUrl = new String[listPage.getPageSize()];
		while (pageNum <= threadTotalPageNum) {
			htmlContent = listPage.getValidWebpageWithoutAttribute(pageNum,
					"id", "button1");
			cellUrl = listPage.getURLFromPage(htmlContent);
			Logger.write("cellUrl:" + cellUrl[0], Logger.ERROR);
			if (cellUrl[0] != "Fail") {
				errorTimes = 0;
				writeToContainer(cellUrl, pageNum);
				pageNum++;
			} else {
				if (++errorTimes > 5) {
					Logger.write("MiibeianWebCrawler.crawl() - Failed at page "
							+ pageNum + "for at least 5 times.", Logger.ERROR);
					break;
				}
			}
		}
		Logger.write(
				"MiibeianWebCrawler.crawl() - Completed getting all pages.",
				Logger.DEBUG);
	}

	private void writeToContainer(String[] Url, int pageNum) {
		Logger.write(
				"MiibeianWebCrawler.writeToContainer() - Start writing page:"
						+ pageNum, Logger.DEBUG);
		int errorTimes = 0;
		DetailPage detailPage = new DetailPage();
		FileContainer container = new FileContainer(pageNum);
		for (int i = 0; i < Url.length; ++i) {
			NodeList list = detailPage.getNodeListWithAttribute(Url[i],
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
					Logger.write(
							"MiibeianWebCrawler.writeToContainer() - Failed at page: "
									+ pageNum + "\r\n" + Url[i], Logger.INFO);
					break;
				}
			}
		}
		container.close();
		Logger.write(
				"MiibeianWebCrawler.writeToContainer() - Completed writing page: "
						+ pageNum, Logger.DEBUG);
	}
}
