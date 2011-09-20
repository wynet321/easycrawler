package com.easycrawler;

import java.io.FileWriter;

public class Logger {
	private static int logLevel;
	private static FileWriter fw;
	private static String resultPath;
	public final static int ERROR = 0;
	public final static int INFO = 1;
	public final static int DEBUG = 2;

	private static FileWriter getLogger() {
		if (fw == null) {
			resultPath = ConfigHelper.getString("ResultPath");
			logLevel = Integer.valueOf(ConfigHelper.getString("LogLevel"));
		}
		try {
			fw = new FileWriter(resultPath + "log.txt", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fw;
	}

	public static void write(String Content, int logCategory) {
		if (logCategory > logLevel)
			try {
				getLogger().append(Content);
				getLogger().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}