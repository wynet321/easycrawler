package com.easycrawler;

import java.io.FileWriter;
import java.io.IOException;

public class ContainerHelper {
	private static String resultPath;
	private static int pagePerFile;
	private static FileWriter fw;
	private static FileWriter getFileWriter(int pageNum)
	{
		if(fw==null||0==pageNum % pagePerFile)
		{
			resultPath=ConfigHelper.getString("ResultPath");
			pagePerFile=Integer.valueOf(ConfigHelper.getString("PagePerFile"));
			String fileName = String.valueOf(pageNum / pagePerFile) + ".txt";
			try {
				fw=new FileWriter(resultPath + fileName, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fw;
	}
	public static void append(String content, int pageNum)
{
		try {
			getFileWriter(pageNum).append(content);
		} catch (IOException e) {
			Logger.write("ContainerHelper.append- content: "+content+"\r\npageNum: "+pageNum, Logger.DEBUG);
			e.printStackTrace();
		}
}
	public static void close()
	{
		try {
			getFileWriter(0).close();
		} catch (IOException e) {
			Logger.write("ContainerHelper.close", Logger.DEBUG);
			e.printStackTrace();
		}
	}
}
