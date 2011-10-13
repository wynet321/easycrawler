package com.easycrawler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class HttpHelper {
	private DefaultHttpClient httpClient;
	private String Charset;
	private int TimeOut;

	public HttpHelper() {
		Charset = ConfigHelper.getString("Charset");
		TimeOut = Integer.valueOf(ConfigHelper.getString("HttpTimeOut"));
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, TimeOut);
		HttpConnectionParams.setSoTimeout(params, TimeOut);
		httpClient = new DefaultHttpClient(params);
	}

	public InputStream getResponseAsStream(String Url) {
		HttpPost httpPost = new HttpPost(Url);
		HttpResponse response = null;
		HttpEntity entity = null;
		InputStream resultStream = null;
		while (response == null) {
			try {
				response = httpClient.execute(httpPost);
				entity = response.getEntity();
				if (response.getStatusLine().getStatusCode() != 200) {
					throw new Exception("Wrong status code="
							+ response.getStatusLine().getStatusCode());
				}
				resultStream = entity.getContent();
			} catch (Exception e) {
				Logger.write(
						"HttpHelper.getResponseAsStream() - Failed to get response from URL: "
								+ Url + "\r\n" + e.getMessage(), Logger.ERROR);
				e.printStackTrace();
				try {
					EntityUtils.consume(entity);
				} catch (Exception e1) {
					Logger.write(
							"HttpHelper.getResponseAsStream() - Failed to consume corrupt response entity."
									+ "\r\n" + e1.getMessage(), Logger.ERROR);
					e1.printStackTrace();
					Thread.currentThread().interrupt();
				}
				response = null;
				try {
					long randomTime = new Random().nextInt(20) * 1000;
					Logger.write(
							"HttpHelper.getResponseAsString() - Sleeping: "
									+ randomTime, Logger.INFO);
					Thread.sleep(randomTime);
				} catch (Exception e2) {
					Logger.write(
							"HttpHelper.getResponseAsStream() - Failed to sleep current thread."
									+ "\r\n" + e2.getMessage(), Logger.ERROR);
					e2.printStackTrace();
				}
				continue;
			}

		}
		return resultStream;
	}

	private BufferedReader getBufferedReaderFromStream(InputStream is) {
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(is, Charset);
		} catch (Exception e) {
			Logger.write("HttpHelper.getBufferedReaderFromStream"
					+ e.getMessage(), Logger.ERROR);
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(isr, 8192);
		return br;
	}

	public String getResponseAsString(String Url) {
		Logger.write(
				"HttpHelper.getResponseAsString() - Start getting content of URL: "
						+ Url, Logger.DEBUG);
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
				Logger
						.write(
								"HttpHelper.getResponseAsString() - Failed to get the content string from URL: "
										+ Url + "\r\n" + e.getMessage(),
								Logger.ERROR);
				e.printStackTrace();
				try {
					br.close();
				} catch (Exception e1) {
					Logger.write(
							"HttpHelper.getResponseAsString() - Failed to close buffer reader.\r\n"
									+ e1.getMessage(), Logger.ERROR);
					e.printStackTrace();
					break;
				}
				htmlContent = "";
				try {
					long randomTime = new Random().nextInt(20) * 1000;
					Logger.write(
							"HttpHelper.getResponseAsString() - Sleeping: "
									+ randomTime, Logger.INFO);
					Thread.sleep(randomTime);
				} catch (Exception e2) {
					Logger.write(
							"HttpHelper.getResponseAsString() - Failed to sleep current thread."
									+ "\r\n" + e2.getMessage(), Logger.ERROR);
					e2.printStackTrace();
				}
				continue;
			}
		}
		htmlContent = htmlContent.replaceAll("&nbsp;", "");
		Logger.write(
				"HttpHelper.getResponseAsString() - Completed getting content of URL: "
						+ Url, Logger.DEBUG);
		return htmlContent;
	}
}
