package com.easycrawler;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;

public class Logger {
	private static int logLevel;
	private static FileWriter fw;
	private static String resultPath;
	private static int logFileNum;
	public final static int ERROR = 0;
	public final static int INFO = 1;
	public final static int DEBUG = 2;

	private static FileWriter getLogger() {
		if (fw == null) {
			resultPath = ConfigHelper.getString("ResultPath");
			logLevel = Integer.valueOf(ConfigHelper.getString("LogLevel"));
			logFileNum = 0;
			try {
				fw = new FileWriter(resultPath + "log"
						+ String.valueOf(logFileNum) + ".txt", true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (new File(resultPath + "log" + String.valueOf(logFileNum)
					+ ".txt").length() > 3000000) {
				if (logFileNum++ > 9)
					logFileNum = 0;
				if (new File(resultPath + "log" + String.valueOf(logFileNum)
						+ ".txt").exists()) {
					try {
						fw = new FileWriter(resultPath + "log"
								+ String.valueOf(logFileNum) + ".txt", false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						fw = new FileWriter(resultPath + "log"
								+ String.valueOf(logFileNum) + ".txt", true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return fw;
	}

	public static void write(String Content, int logCategory) {
		FileWriter temp=getLogger();
		if (logCategory <= logLevel)
			try {
				temp.append(getCurrentTime() + " - " + Content + "\r\n");
				temp.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private static String getCurrentTime() {
		Calendar c = Calendar.getInstance();
		return String.valueOf(c.get(Calendar.YEAR)) + "/"
				+ String.valueOf(c.get(Calendar.MONTH)) + "/"
				+ String.valueOf(c.get(Calendar.DATE)) + " "
				+ String.valueOf(c.get(Calendar.HOUR)) + ":"
				+ String.valueOf(c.get(Calendar.MINUTE)) + ":"
				+ String.valueOf(c.get(Calendar.SECOND)) + " "
				+ String.valueOf(c.get(Calendar.MILLISECOND));
	}
}