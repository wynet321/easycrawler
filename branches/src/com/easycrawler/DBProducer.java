package com.easycrawler;

public class DBProducer {

	public static void main(String[] args) {

		// String verifyCode = getVerifyCode();
		// int totalPageNum = getTotalPageNum(verifyCode);
		// multi-threads
		// startThread(totalPageNum);
		startThread();

		// produceResultFile(verifyCode, totalPageNum);
	}

	private static void startThread() {
		for(int i=0;i<2;++i)
			new SonThread().start();
		
	}

}
