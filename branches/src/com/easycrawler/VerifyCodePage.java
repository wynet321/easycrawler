package com.easycrawler;

public class VerifyCodePage extends WebPage {
	public String getVerifyCode(HttpHelper httpHelper) {
		Pic pic = new Pic();
		String result = pic.getResult(httpHelper);
		Logger.write(
				"MiibeianWebPage.getVerifyCode() - Verify Code: " + result,
				Logger.INFO);
		return result;
	}
}
