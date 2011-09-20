package com.easycrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

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
		HttpResponse response = getResponse(Url);
		while (response == null)
			response = getResponse(Url);
		HttpEntity entity = response.getEntity();
		InputStream resultStream = null;
		try {
			resultStream = entity.getContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultStream;
	}

	private static HttpResponse getResponse(String Url) {
		HttpPost httpPost = new HttpPost(Url);
		HttpResponse response = null;
		try {
			response = getHttpClient().execute(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public static String getResponseAsString(String Url) {
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(getResponseAsStream(Url), Charset);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(isr);
		String htmlLine = "";
		String htmlContent = "";
		try {
			while ((htmlLine = br.readLine()) == null) {
				br.close();
				try {
					isr = new InputStreamReader(getResponseAsStream(Url),
							Charset);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				br = new BufferedReader(isr);
			}
			htmlContent = htmlLine;
			while ((htmlLine = br.readLine()) != null) {
				htmlContent += htmlLine;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return htmlContent;
	}
}
