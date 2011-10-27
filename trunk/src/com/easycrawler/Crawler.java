package com.easycrawler;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class Crawler {

	public static void main(String[] args) {
		// String url = "http://www.audi.cn/";
		get();
		// getResponse(url);
	}

	public static void get() {
		Parser parser;
		for (int j = 0; j < 2; j++)
			try {
				parser = new Parser(
						"http://cn.bing.com/search?q=%E5%AE%98%E6%96%B9%E7%BD%91%E7%AB%99&go=&first="
								+ (0 == j ? "" : String.valueOf(j)) + "1");
				HasAttributeFilter nf = new HasAttributeFilter();
				nf.setAttributeName("class");
				nf.setAttributeValue("sa_cc");
				NodeList list = parser.extractAllNodesThatMatch(nf);

				int i = 0;
				String href = new String("");
				int size = list.size();
				while (i < size) {
					href = ((Tag) list.elementAt(i).getFirstChild()
							.getFirstChild().getFirstChild())
							.getAttribute("href");
					if (!href.contains("cc.bingj.com")) {
						System.out.println(href);
						System.out.println(list.elementAt(i).getChildren()
								.elementAt(0).toPlainTextString());
						System.out.println(list.elementAt(i).getChildren()
								.elementAt(1).toPlainTextString());
					}
					i++;
				}
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	// public static void getResponse(String url) {
	// HttpClient httpclient = new DefaultHttpClient();
	// // httpclient.getParams().setParameter("http.protocol.content-charset",
	// // "GB2312");
	// try {
	// HttpGet httpget = new HttpGet(url);
	// System.out.println("executing request " + httpget.getURI());
	// // Create a response handler
	// ResponseHandler<String> responseHandler = new BasicResponseHandler();
	// String result = new String();
	// String responseBody = new String(httpclient.execute(httpget,
	// responseHandler).getBytes(), "utf-8");
	//
	// result = responseBody.replaceAll("\n", "");
	// result = result.replaceAll("\r", "");
	// result = result.replaceAll("\t", "");
	// result = result.replaceAll("<script[^>]*?>.*?<\\/script>", "");
	// result = result.replaceAll("<style[^>]*?>.*?<\\/style>", "");
	// result = result.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll(
	// "<[^>]*>", "");
	//
	// // result=responseBody;
	//
	// System.out.println("----------------------------------------");
	// System.out.println(result);
	// System.out.println("----------------------------------------");
	//
	// } catch (Exception e) {
	// System.out.println(e.toString());
	// } finally {
	// // When HttpClient instance is no longer needed,
	// // shut down the connection manager to ensure
	// // immediate deallocation of all system resources
	// httpclient.getConnectionManager().shutdown();
	// }
	//
	// }

}
