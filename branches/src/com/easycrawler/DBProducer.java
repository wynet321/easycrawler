package com.easycrawler;

import java.util.Random;

public class DBProducer {

	public static void main(String[] args) {
		startThread();
	}

	private static void startThread() {
		int threadNum = ConfigHelper.getInt("ThreadNumber");
		Thread[] sonThreads = new Thread[threadNum];
		for (int i = 1; i <= threadNum; ++i) {
			sonThreads[i-1] = new SonThread(String.valueOf(i));
			sonThreads[i-1].start();
			try {
				long randomTime = (new Random().nextInt(10) + 20) * 1000;
				Logger.write("SonThread.startThread() - Sleeping: "
						+ randomTime, Logger.INFO);
				Thread.sleep(randomTime);
			} catch (Exception e) {
				Logger.write(
						"SonThread.startThread() - Failed to sleep current thread."
								+ "\r\n" + e.getMessage(), Logger.ERROR);
				e.printStackTrace();
			}
		}

	}

}
