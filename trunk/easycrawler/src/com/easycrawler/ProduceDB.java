package com.easycrawler;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.analyzepic.AnalyzePic;

public class ProduceDB {

	public static void main(String[] args) throws IOException, ParserException {
		HttpHost targetHost = new HttpHost("www.miibeian.gov.cn", 80, "http");
		DefaultHttpClient httpclient = new DefaultHttpClient();

		String url = "http://www.miibeian.gov.cn/validateCode";

		HttpPost httpPost = new HttpPost(url);
		HttpResponse response = httpclient.execute(targetHost, httpPost);

		HttpEntity entity = response.getEntity();
		AnalyzePic ap = new AnalyzePic();
		int result = ap.getResult(entity.getContent());
		System.out.println(result);

		url = "http://www.miibeian.gov.cn/icp/publish/query/icpMemoInfo_searchExecute.action?siteUrl=com&verifyCode="
				+ result + "&pageNo=1";
		httpPost = new HttpPost(url);
		response = httpclient.execute(targetHost, httpPost);
		entity = response.getEntity();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				entity.getContent(), "GBK"));

		String htmlLine = "";
		String htmlContent = "";

		while ((htmlLine = br.readLine()) != null) {
			htmlContent += htmlLine;
		}
		br.close();
		htmlContent = new String(htmlContent.getBytes(), "UTF-8");
		// System.out.println(page);
		int totalPageNumStart = htmlContent.indexOf("&nbsp;1/") + 8;
		int totalPageNumLength = htmlContent.indexOf("&nbsp;",
				totalPageNumStart);
		int totalPageNum = Integer.valueOf(htmlContent.substring(
				totalPageNumStart, totalPageNumLength));

		int pageNum = 1;
		FileWriter fw = new FileWriter("d:/easycrawlerresult/1.txt");
		while (pageNum < totalPageNum) {
			url = "http://www.miibeian.gov.cn/icp/publish/query/icpMemoInfo_searchExecute.action?siteUrl=com&verifyCode="
					+ result + "&pageNo=" + String.valueOf(pageNum);
			httpPost = new HttpPost(url);
			response = httpclient.execute(targetHost, httpPost);
			entity = response.getEntity();
			br = new BufferedReader(new InputStreamReader(entity.getContent(),
					"GBK"));

			htmlLine = "";
			htmlContent = "";

			while ((htmlLine = br.readLine()) != null) {
				htmlContent += htmlLine;
			}
			br.close();
			htmlContent = new String(htmlContent.getBytes(), "UTF-8");
			Parser parser = Parser.createParser(htmlContent, "UTF-8");
			HasAttributeFilter nf = new HasAttributeFilter();
			nf.setAttributeName("class");
			nf.setAttributeValue("a");
			NodeList list = parser.extractAllNodesThatMatch(nf);
			if (0 == pageNum % 1000) {
				fw.close();
				fw = new FileWriter("d:/easycrawlerresult/"
						+ String.valueOf(pageNum / 1000) + ".txt");
			}
			fw.append((CharSequence) list.elementAt(0).toHtml());
			fw.append("\r\n");
			pageNum++;
		}
		fw.close();
	}
}
