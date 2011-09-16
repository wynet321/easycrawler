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
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.analyzepic.AnalyzePic;

public class ProduceDB {

	private static DefaultHttpClient httpClient = new DefaultHttpClient();
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
			htmlContent += htmlLine + "\r\n";
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
		produceResultFile(verifyCode, totalPageNum, resultPath);
	}

	private static void produceResultFile(String verifyCode, int totalPageNum,
			String path) throws IOException, ParserException {
		String Url = "";
		String htmlContent = "";
		int pageNum = 1;
		FileWriter fw = new FileWriter(path + "/1.txt");
		while (pageNum < totalPageNum) {
			Url = baseUrl + "&verifyCode=" + verifyCode + "&pageNo="
					+ String.valueOf(pageNum);
			htmlContent = getResponseAsString(Url);
			Parser parser = new Parser(htmlContent);
			HasAttributeFilter nf = new HasAttributeFilter();
			nf.setAttributeName("class");
			nf.setAttributeValue("a");
			NodeList list = parser.extractAllNodesThatMatch(nf);
			if (0 == pageNum % 1000) {
				fw.close();
				fw = new FileWriter(path + "/" + String.valueOf(pageNum / 1000)
						+ ".txt");
			}
			fw.append((CharSequence) list.elementAt(0).toHtml());
			fw.append("\r\n");
			pageNum++;
		}
		fw.close();
	}
}
