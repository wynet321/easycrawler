package com.easycrawler;

import com.easycrawler.helper.LogHelper;

public class CrawlerSonThread extends Thread {

	public CrawlerSonThread(String threadName) {
		super(threadName);
	}

	public void run() {
		LogHelper.write(" Started.", LogHelper.INFO);
		// work
		new MiibeianWebSite().crawl();
		LogHelper.write(" Completed.", LogHelper.INFO);
	}
}
