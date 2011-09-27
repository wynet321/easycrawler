package com.easycrawler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class HttpHelper {
	private static DefaultHttpClient httpClient;
	private static String Charset;
	private static int TimeOut;

	private static DefaultHttpClient getHttpClient() {
		if (httpClient == null) {
			Charset = ConfigHelper.getString("Charset");
			TimeOut = Integer.valueOf(ConfigHelper.getString("HttpTimeOut"));
			httpClient = new DefaultHttpClient();
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, TimeOut);
			HttpConnectionParams.setSoTimeout(params, TimeOut);
		}
		return httpClient;

	}

	public static InputStream getResponseAsStream(String Url) {
		HttpPost httpPost = new HttpPost(Url);
		HttpResponse response = null;
		HttpEntity entity = null;
		InputStream resultStream = null;
		while (response == null) {
			try {
				response = getHttpClient().execute(httpPost);
				entity = response.getEntity();
				if (response.getStatusLine().getStatusCode() != 200) {
					throw new Exception("fda");
				}
				resultStream = entity.getContent();
			} catch (Exception e) {
				Logger.write("HttpHelper.getResponse: " + Url + "\r\n"
						+ e.getMessage(), Logger.ERROR);
				e.printStackTrace();
				try {
					EntityUtils.consume(entity);
				} catch (Exception e1) {
					Logger.write("HttpHelper.getResponse: " + Url + "\r\n"
							+ e1.getMessage(), Logger.ERROR);
					e1.printStackTrace();
					System.exit(1);
				}
				response = null;
				continue;
			}

		}
		return resultStream;
	}

	private static BufferedReader getBufferedReaderFromStream(InputStream is) {
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(is, Charset);
		} catch (Exception e) {
			Logger.write("HttpHelper.getBufferedReaderFromStream"
					+ e.getMessage(), Logger.ERROR);
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(isr);
		return br;
	}

	public static String getResponseAsString(String Url) {
		BufferedReader br;
		String htmlLine = "";
		String htmlContent = "";
		while (0 == htmlContent.length()) {
			br = getBufferedReaderFromStream(getResponseAsStream(Url));
			htmlContent = "";
			try {
				while ((htmlLine = br.readLine()) != null) {
					htmlContent += htmlLine;
				}
				br.close();
			} catch (Exception e) {
				Logger.write("HttpHelper.getResponseAsString: " + Url + "\r\n"
						+ e.getMessage(), Logger.ERROR);
				e.printStackTrace();
			}
		}
		htmlContent = htmlContent.replaceAll("&nbsp;", "");
		return htmlContent;
	}
}
