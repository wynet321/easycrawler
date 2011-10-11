package com.easycrawler;

public class SonThread extends Thread {

public void run()
	{
		Logger.write(getName()+" Started.", Logger.INFO);
		//work
		new WebPageAnalyzer().produceResultFile();
		Logger.write(getName()+" Completed.", Logger.INFO);
	}
}
