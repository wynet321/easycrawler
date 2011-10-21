package com.easycrawler;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.htmlparser.util.NodeList;

public class FileContainer implements IContainer{
	private String resultPath;
	private int pagePerFile;
	private int pageNum;
	private FileWriter fw;

	public FileContainer(int pageNum) {
		resultPath = ConfigHelper.getString("ResultPath");
		pagePerFile = Integer.valueOf(ConfigHelper.getString("PagePerFile"));
		this.pageNum = pageNum;
		// String.valueOf(Integer.valueOf(Thread.currentThread().getName())+1)
		String fileName = Thread.currentThread().getName() + "-"
				+ String.valueOf(pageNum / pagePerFile + 1) + ".txt";
		try {
			fw = new FileWriter(resultPath + fileName, true);
		} catch (Exception e) {
			Logger.write("ContainerHelper.getFileWriter: " + e.getMessage(),
					Logger.ERROR);
			e.printStackTrace();
		}
	}

	public void append(String content) {
		Logger.write(
				"ContainerHelper.append - Start appending content, pageNum:"
						+ pageNum, Logger.INFO);
		try {
			fw.append(content);
			fw.flush();
		} catch (Exception e) {
			Logger.write("ContainerHelper.append - content: " + content
					+ "\r\npageNum: " + pageNum + "\r\n" + e.getMessage(),
					Logger.ERROR);
			e.printStackTrace();
		}
		Logger.write("ContainerHelper.append - Completed appending content: "
				+ content, Logger.INFO);
	}

	public void append(String id, NodeList list) {
		Logger.write("ContainerHelper.append - Start appending unit id: " + id,
				Logger.INFO);
		try {
			fw.append(getRegisterInfo(id, list));
			fw.flush();
		} catch (Exception e) {
			Logger.write("ContainerHelper.append - list.Length: " + list.size()
					+ "\r\npageNum: " + pageNum + "\r\n" + e.getMessage(),
					Logger.ERROR);
			e.printStackTrace();
		}
		Logger.write("ContainerHelper.append - Start appending unit id: " + id,
				Logger.INFO);
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
			Logger.write("ContainerHelper.close\r\n" + e.getMessage(),
					Logger.ERROR);
			e.printStackTrace();
		}
	}
}
