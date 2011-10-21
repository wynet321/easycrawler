package com.easycrawler;

public class SonThread extends Thread {

	public SonThread(String threadName) {
		super(threadName);
	}

	public void run() {
		Logger.write(" Started.", Logger.INFO);
		// work
		new MiibeianWebCrawler().crawl();
		Logger.write(" Completed.", Logger.INFO);
	}
}
