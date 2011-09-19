package com.easycrawler;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.analyzepic.AnalyzePic;

public class ProduceDB {

	private static DefaultHttpClient httpClient = new DefaultHttpClient();
	private static String hostUrl = "http://www.miibeian.gov.cn";
	private static String baseUrl = "http://www.miibeian.gov.cn/icp/publish/query/icpMemoInfo_searchExecute.action?siteUrl=baidu.com";
	private static String resultPath = "d:/easycrawlerresult";
	private static String charSet = "GBK";

	private static InputStream getResponseAsStream(String Url)
			throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(Url);
		HttpResponse response = httpClient.execute(httpPost);
		while (response.getStatusLine().getStatusCode() != 200)
			response = httpClient.execute(httpPost);
		HttpEntity entity = response.getEntity();
		return entity.getContent();
	}

	private static String getResponseAsString(String Url)
			throws UnsupportedEncodingException, ClientProtocolException,
			IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				getResponseAsStream(Url), charSet));
		String htmlLine = "";
		String htmlContent = "";
		while ((htmlLine = br.readLine()) != null) {
			htmlContent += htmlLine;
		}
		br.close();
		return htmlContent;
	}

	private static String getVerifyCode() throws ClientProtocolException,
			IOException {
		String Url = "http://www.miibeian.gov.cn/validateCode";
		AnalyzePic ap = new AnalyzePic();
		String result = String.valueOf(ap.getResult(getResponseAsStream(Url)));
		// System.out.println(result);
		return result;
	}

	private static int getTotalPageNum(String verifyCode)
			throws UnsupportedEncodingException, ClientProtocolException,
			IOException {
		String Url = baseUrl + "&verifyCode=" + verifyCode + "&pageNo=1";
		String htmlContent = getResponseAsString(Url);
		int totalPageNumStart = htmlContent.indexOf("&nbsp;1/") + 8;
		int totalPageNumLength = htmlContent.indexOf("&nbsp;",
				totalPageNumStart);
		int totalPageNum = Integer.valueOf(htmlContent.substring(
				totalPageNumStart, totalPageNumLength));
		return totalPageNum;
	}

	public static void main(String[] args) throws ClientProtocolException,
			IOException, ParserException {
		String verifyCode = getVerifyCode();
		int totalPageNum = getTotalPageNum(verifyCode);
		produceResultFile(verifyCode, totalPageNum);
	}

	private static void produceResultFile(String verifyCode, int totalPageNum)
			throws IOException, ParserException {
		String Url = "";
		String htmlContent = "";
		int pageNum = 1;
		int errorTimes = 0;
		String[] cellUrl = new String[20];
		HasAttributeFilter nf = new HasAttributeFilter();
		NodeList list = new NodeList();
		while (pageNum < totalPageNum) {
			Url = baseUrl + "&verifyCode=" + verifyCode + "&pageNo="
					+ String.valueOf(pageNum);
			htmlContent = getResponseAsString(Url);
			Parser parser = new Parser(htmlContent);
			nf.setAttributeName("id");
			nf.setAttributeValue("button1");
			list = parser.extractAllNodesThatMatch(nf);
			if (list.size() > 0)
				// reget verifyCode
				verifyCode = getVerifyCode();
			else {
				cellUrl = getCellUrl(htmlContent);
				if (cellUrl[0] != "Fail") {
					errorTimes=0;
					produceFile(cellUrl, pageNum);
					pageNum++;
				}
				else {
					if (errorTimes++ > 5) {
						System.out.println("Failed to produce at page "
								+ pageNum);
						return;
					}
				}

			}
		}
	}

	private static String[] getCellUrl(String htmlContent)
			throws UnsupportedEncodingException, ClientProtocolException,
			IOException, ParserException {
		String[] cellUrl = new String[20];

		HasAttributeFilter nf = new HasAttributeFilter();
		NodeList list = new NodeList();

		Parser parser = new Parser(htmlContent);
		nf.setAttributeName("class");
		nf.setAttributeValue("a");
		list = parser.extractAllNodesThatMatch(nf);
		if (list.size() > 0) {
			for (int i = 0; i < 20; i++)
				cellUrl[i] = hostUrl
						+ ((Tag) (list.elementAt(0).getChildren().elementAt(1)
								.getChildren().elementAt(2 * i + 3)
								.getChildren().elementAt(7).getChildren()
								.elementAt(1))).getAttribute("href");
		} else {
			cellUrl[0] = "Fail";
		}
		return cellUrl;
	}

	private static void produceFile(String[] Url, int pageNum) throws IOException,
			ParserException {
		FileWriter fw = new FileWriter(resultPath + "/0.txt");
		HasAttributeFilter nf = new HasAttributeFilter();
		NodeList list = new NodeList();
		String htmlContent = "";
		int errorTimes = 0;
		for (int i = 0; i < Url.length; i++) {
			htmlContent = getResponseAsString(Url[i]);
			Parser parser = new Parser(htmlContent);
			nf.setAttributeName("class");
			nf.setAttributeValue("a");
			list = parser.extractAllNodesThatMatch(nf);
			if (list.size() == 2) {
				errorTimes = 0;
				if (0 == pageNum % 500) {
					fw.close();
					fw = new FileWriter(resultPath + "/"
							+ String.valueOf(pageNum / 500) + ".txt");
				}
				fw.append((CharSequence) list.elementAt(0).toHtml());
				fw.append((CharSequence) list.elementAt(1).toHtml());
				fw.append("\r\n");
			} else {
				System.out.println(errorTimes);
				i--;
				if (errorTimes++ > 5) {
					fw.close();
					return;
				}
			}
		}
		fw.close();
	}
}