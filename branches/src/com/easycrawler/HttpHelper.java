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
	private DefaultHttpClient httpClient;
	private String Charset;
	private int TimeOut;

	public HttpHelper() {
		Charset = ConfigHelper.getString("Charset");
		TimeOut = Integer.valueOf(ConfigHelper.getString("HttpTimeOut"));
		httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, TimeOut);
		HttpConnectionParams.setSoTimeout(params, TimeOut);
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
				Logger.write("HttpHelper.getResponseAsStream: " + Url + "\r\n"
						+ e.getMessage(), Logger.ERROR);
				e.printStackTrace();
				try {
					EntityUtils.consume(entity);
				} catch (Exception e1) {
					Logger.write("HttpHelper.getResponseAsStream: " + Url
							+ "\r\n" + e1.getMessage(), Logger.ERROR);
					e1.printStackTrace();
					System.exit(1);
				}
				response = null;
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
		BufferedReader br = new BufferedReader(isr);
		return br;
	}

	public String getResponseAsString(String Url) {
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
				try {
					br.close();
				} catch (Exception e1) {
					Logger.write(
							"HttpHelper.getResponseAsString: Close buffer reader failed.\r\n"
									+ e.getMessage(), Logger.ERROR);
					e.printStackTrace();
					break;
				}
				htmlContent = "";
				continue;
			}
		}
		htmlContent = htmlContent.replaceAll("&nbsp;", "");
		return htmlContent;
	}
}
