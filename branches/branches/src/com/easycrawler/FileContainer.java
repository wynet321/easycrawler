package com.easycrawler;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.htmlparser.util.NodeList;

import com.easycrawler.helper.ConfigXMLHelper;
import com.easycrawler.helper.LogHelper;

public class FileContainer extends ContainerBase {
	private String resultPath;
	private int pagePerFile;
	private int pageNum;
	private FileWriter fw;

	public FileContainer(int pageNum) {
		resultPath = ConfigXMLHelper.getString("ResultPath");
		pagePerFile = Integer.valueOf(ConfigXMLHelper.getString("PagePerFile"));
		this.pageNum = pageNum;
		// String.valueOf(Integer.valueOf(Thread.currentThread().getName())+1)
		String fileName = Thread.currentThread().getName() + "-"
				+ String.valueOf(pageNum / pagePerFile + 1) + ".txt";
		try {
			fw = new FileWriter(resultPath + fileName, true);
		} catch (Exception e) {
			LogHelper.write("ContainerHelper.getFileWriter: " + e.getMessage(),
					LogHelper.ERROR);
			e.printStackTrace();
		}
	}

	public void append(String content) {
		LogHelper.write(
				"ContainerHelper.append - Start appending content, pageNum:"
						+ pageNum, LogHelper.INFO);
		try {
			fw.append(content);
			fw.flush();
		} catch (Exception e) {
			LogHelper.write("ContainerHelper.append - content: " + content
					+ "\r\npageNum: " + pageNum + "\r\n" + e.getMessage(),
					LogHelper.ERROR);
			e.printStackTrace();
		}
		LogHelper.write(
				"ContainerHelper.append - Completed appending content: "
						+ content, LogHelper.INFO);
	}

	public void append(String id, NodeList list) {
		LogHelper.write("ContainerHelper.append - Start appending unit id: "
				+ id, LogHelper.INFO);
		try {
			fw.append(getRegisterInfo(id, list));
			fw.flush();
		} catch (Exception e) {
			LogHelper.write(
					"ContainerHelper.append - list.Length: " + list.size()
							+ "\r\npageNum: " + pageNum + "\r\n"
							+ e.getMessage(), LogHelper.ERROR);
			e.printStackTrace();
		}
		LogHelper.write("ContainerHelper.append - Start appending unit id: "
				+ id, LogHelper.INFO);
	}

	@SuppressWarnings("deprecation")
	private static String getRegisterInfo(String id, NodeList list) {
		String registerInfo = id + "    ";
		registerInfo += new SimpleDateFormat("yyyy-MM-dd").format(new Date(list
				.elementAt(0).getChildren().elementAt(1).getChildren()
				.elementAt(1).getChildren().elementAt(7).toPlainTextString()))
				+ "    ";
		registerInfo += list.elementAt(0).getChildren().elementAt(1)
				.getChildren().elementAt(3).getChildren().elementAt(3)
				.toPlainTextString()
				+ "    ";
		registerInfo += list.elementAt(0).getChildren().elementAt(1)
				.getChildren().elementAt(3).getChildren().elementAt(7)
				.toPlainTextString()
				+ "    ";
		registerInfo += list.elementAt(1).getChildren().elementAt(1)
				.getChildren().elementAt(1).getChildren().elementAt(3)
				.toPlainTextString()
				+ "    ";
		NodeList webAddressList = list.elementAt(1).getChildren().elementAt(1)
				.getChildren().elementAt(1).getChildren().elementAt(7)
				.getChildren();
		String webAddress = "";
		int listLength = webAddressList.size();
		for (int i = 0; i < listLength; i++) {
			webAddress += webAddressList.elementAt(i).toHtml()
					.replaceAll("<[^>]+>", "")
					+ ";";
		}
		registerInfo += webAddress + "    ";
		registerInfo += list.elementAt(1).getChildren().elementAt(1)
				.getChildren().elementAt(3).getChildren().elementAt(3)
				.toPlainTextString()
				+ "    ";
		registerInfo += list.elementAt(1).getChildren().elementAt(1)
				.getChildren().elementAt(5).getChildren().elementAt(3)
				.toPlainTextString()
				+ "    \r\n";
		return registerInfo;
	}

	public void close() {
		try {
			fw.close();
		} catch (Exception e) {
			LogHelper.write("ContainerHelper.close\r\n" + e.getMessage(),
					LogHelper.ERROR);
			e.printStackTrace();
		}
	}
}
