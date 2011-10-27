package com.easycrawler;

import com.easycrawler.helper.LogHelper;

public class VerifyCodeWebPage extends WebPageBase {
	public String getVerifyCode(HttpHandler httpHelper) {
		PicHandler pic = new PicHandler();
		String result = pic.getResult(httpHelper);
		LogHelper.write(
				"MiibeianWebPage.getVerifyCode() - Verify Code: " + result,
				LogHelper.INFO);
		return result;
	}
}
