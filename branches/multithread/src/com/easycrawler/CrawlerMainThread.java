package com.easycrawler;

import java.util.Random;

import com.easycrawler.helper.ConfigXMLHelper;
import com.easycrawler.helper.LogHelper;

public class CrawlerMainThread {

	public static void main(String[] args) {
		startThread();
	}

	private static void startThread() {
		int threadNum = ConfigXMLHelper.getInt("ThreadNumber");
		Thread[] sonThreads = new Thread[threadNum];
		for (int i = 1; i <= threadNum; ++i) {
			sonThreads[i - 1] = new CrawlerSonThread(String.valueOf(i));
			sonThreads[i - 1].start();
			try {
				long randomTime = (new Random().nextInt(10) + 20) * 1000;
				LogHelper.write("SonThread.startThread() - Sleeping: "
						+ randomTime, LogHelper.INFO);
				Thread.sleep(randomTime);
			} catch (Exception e) {
				LogHelper.write(
						"SonThread.startThread() - Failed to sleep current thread."
								+ "\r\n" + e.getMessage(), LogHelper.ERROR);
				e.printStackTrace();
			}
		}

	}

}
