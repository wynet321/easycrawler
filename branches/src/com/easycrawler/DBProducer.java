package com.easycrawler;

import java.util.Random;

public class DBProducer {

	public static void main(String[] args) {
		startThread();
	}

	private static void startThread() {
		int threadNum = 10;
		Thread[] sonThreads = new Thread[threadNum];
		for (int i = 1; i < threadNum; ++i) {
			sonThreads[i] = new SonThread(String.valueOf(i));
			sonThreads[i].start();
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
