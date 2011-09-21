package com.easycrawler;

import java.io.FileWriter;

public class ContainerHelper {
	private static String resultPath;
	private static int pagePerFile;
	private static FileWriter fw;

	private static FileWriter getFileWriter(int pageNum) {
		if (fw == null) {
			resultPath = ConfigHelper.getString("ResultPath");
			pagePerFile = Integer
					.valueOf(ConfigHelper.getString("PagePerFile"));
		}
		if (0 == (pageNum-1) % 100) {
			String fileName = String.valueOf(pageNum / pagePerFile) + ".txt";
			try {
				if(fw!=null)
					fw.close();
				fw = new FileWriter(resultPath + fileName, true);
			} catch (Exception e) {
				Logger.write("ContainerHelper.getFileWriter", Logger.DEBUG);
				e.printStackTrace();
			}
		}
		return fw;
	}

	public static void append(String content, int pageNum) {
		try {
			getFileWriter(pageNum).append(content);
			getFileWriter(pageNum).flush();
		} catch (Exception e) {
			Logger.write("ContainerHelper.append- content: " + content
					+ "\r\npageNum: " + pageNum, Logger.DEBUG);
			e.printStackTrace();
		}
	}

	public static void close() {
		try {
			getFileWriter(0).close();
		} catch (Exception e) {
			Logger.write("ContainerHelper.close", Logger.DEBUG);
			e.printStackTrace();
		}
	}
}
