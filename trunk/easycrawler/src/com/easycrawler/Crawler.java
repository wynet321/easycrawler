package com.easycrawler;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class Crawler {

	public static void getResponse(String url) {
		HttpClient httpclient = new DefaultHttpClient();
		// httpclient.getParams().setParameter("http.protocol.content-charset",
		// "GB2312");
		try {
			HttpGet httpget = new HttpGet(url);
			System.out.println("executing request " + httpget.getURI());
			// Create a response handler
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String result = new String();
			String responseBody = new String(httpclient.execute(httpget,
					responseHandler).getBytes(), "utf-8");

			result = responseBody.replaceAll("\n", "");
			result = result.replaceAll("\r", "");
			result = result.replaceAll("\t", "");
			result = result.replaceAll("<script[^>]*?>.*?<\\/script>", "");
			result = result.replaceAll("<style[^>]*?>.*?<\\/style>", "");
			result = result.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll(
					"<[^>]*>", "");

			// result=responseBody;

			System.out.println("----------------------------------------");
			System.out.println(result);
			System.out.println("----------------------------------------");

		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}

	}

	public static void get() {
		Parser parser;
		try {
			parser = new Parser("http://www.audi.cn");
			LinkRegexFilter nf = new LinkRegexFilter("audi\\.cn.*");
			NodeList list = parser.extractAllNodesThatMatch(nf);

			int i = 0;
			String href = new String("");
			while (i < list.size() - 1) {
				href = ((Tag) list.elementAt(i)).getAttribute("href");
				if (!href.startsWith("#"))
					System.out.println(href);
				i++;

			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "http://www.audi.cn/";
		get(); // 第一种方法
		// getResponse(url);
	}

}
