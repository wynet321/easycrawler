package com.easycrawler;

import java.io.FileWriter;

import org.htmlparser.util.NodeList;

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
		// if (0 == (pageNum - 1) % 100) {
		String fileName = String.valueOf(pageNum / pagePerFile) + ".txt";
		try {
			if (fw != null)
				fw.close();
			fw = new FileWriter(resultPath + fileName, true);
		} catch (Exception e) {
			Logger.write("ContainerHelper.getFileWriter: " + e.getMessage(),
					Logger.ERROR);
			e.printStackTrace();
		}
		// }
		return fw;
	}

	public static void append(String content, int pageNum) {
		try {
			getFileWriter(pageNum).append(content);
			getFileWriter(pageNum).flush();
		} catch (Exception e) {
			Logger.write("ContainerHelper.append- content: " + content
					+ "\r\npageNum: " + pageNum + "\r\n" + e.getMessage(),
					Logger.ERROR);
			e.printStackTrace();
		}
	}

	public static void append(NodeList list, int pageNum) {
		try {
			getFileWriter(pageNum).append(getRegisterInfo(list));
			getFileWriter(pageNum).flush();
		} catch (Exception e) {
			Logger.write("ContainerHelper.append- list.Length: " + list.size()
					+ "\r\npageNum: " + pageNum + "\r\n" + e.getMessage(),
					Logger.ERROR);
			e.printStackTrace();
		}
	}

	// private static Hashtable<String, String> getRegisterInfo(String content)
	// {
	// Document doc = DOMHelper.getDoc(content);
	// Hashtable<String, String> registerInfo = new Hashtable();
	// registerInfo.put("licenseNo", doc.getChildNodes().item(0)
	// .getFirstChild().getFirstChild().getChildNodes().item(1)
	// .getNodeValue());
	// registerInfo.put("passDate", doc.getChildNodes().item(0)
	// .getFirstChild().getFirstChild().getChildNodes().item(3)
	// .getNodeValue());
	// registerInfo.put("unitName", doc.getChildNodes().item(0)
	// .getFirstChild().getChildNodes().item(1).getChildNodes()
	// .item(1).getNodeValue());
	// registerInfo.put("Category", doc.getChildNodes().item(0)
	// .getFirstChild().getChildNodes().item(1).getChildNodes()
	// .item(3).getNodeValue());
	// registerInfo.put("name", doc.getChildNodes().item(1).getFirstChild()
	// .getFirstChild().getChildNodes().item(1).getNodeValue());
	// NodeList webAddressList = doc.getChildNodes().item(1).getFirstChild()
	// .getFirstChild().getChildNodes().item(3).getChildNodes();
	// String webAddress = "";
	// for (int i = 0; i < webAddressList.getLength(); i++) {
	// webAddress += webAddressList.item(i).getTextContent().replaceAll(
	// "<[^>]+>", "")
	// + ";";
	// }
	// registerInfo.put("webAddress", webAddress);
	// registerInfo.put("contactor", doc.getChildNodes().item(1)
	// .getFirstChild().getChildNodes().item(1).getChildNodes()
	// .item(1).getNodeValue());
	//
	// return registerInfo;
	//
	// }

	private static String getRegisterInfo(NodeList list) {
		String registerInfo = "";
		registerInfo += list.elementAt(0).getChildren().elementAt(1).getChildren().elementAt(1)
				.getChildren().elementAt(3).toPlainTextString()
				+ "    ";
		registerInfo += list.elementAt(0).getChildren().elementAt(1).getChildren().elementAt(1)
				.getChildren().elementAt(7).toPlainTextString()
				+ "    ";
		registerInfo = list.elementAt(0).getChildren().elementAt(1).getChildren()
				.elementAt(3).getChildren().elementAt(3).toPlainTextString()
				+ "    ";
		registerInfo += list.elementAt(0).getChildren().elementAt(1).getChildren()
		.elementAt(3).getChildren().elementAt(7).toPlainTextString()
				+ "    ";
		registerInfo += list.elementAt(1).getChildren().elementAt(1).getChildren().elementAt(1)
				.getChildren().elementAt(3).toPlainTextString()
				+ "    ";
		NodeList webAddressList = list.elementAt(1).getChildren().elementAt(1).getChildren().elementAt(1).getChildren().elementAt(7).getChildren();
		String webAddress = "";
		for (int i = 0; i < webAddressList.size(); i++) {
			webAddress += webAddressList.elementAt(i).toHtml().replaceAll(
					"<[^>]+>", "")
					+ ";";
		}
		registerInfo += webAddress + "    ";
		registerInfo += list.elementAt(1).getChildren().elementAt(1).getChildren()
				.elementAt(1).getChildren().elementAt(7).toPlainTextString()
				+ "    \r\n";
		return registerInfo;
	}

	public static void close() {
		try {
			getFileWriter(0).close();
		} catch (Exception e) {
			Logger.write("ContainerHelper.close\r\n" + e.getMessage(),
					Logger.ERROR);
			e.printStackTrace();
		}
	}
}
