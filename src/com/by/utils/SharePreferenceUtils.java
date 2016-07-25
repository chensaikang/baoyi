package com.by.utils;

import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 
 * @author 本地简易信息存储
 * 
 */
public class SharePreferenceUtils {

	private static SharedPreferences sharedPreferences;

	public static String getOpenId(Context c) {

		sharedPreferences = c.getSharedPreferences("LoginInfo", 0);
		String openId = sharedPreferences.getString("openId", null);
		// Log.e("TAG", "SharePreferenceUtils+++++++++++++Cur=" + openId);
		return openId;
	}

	public static String getLoginInfo(Context c, String name) {

		sharedPreferences = c.getSharedPreferences("LoginInfo", 0);
		String value = sharedPreferences.getString(name, null);
		return value;
	}

	// 判断登陆信息
	public static boolean isEnableLogin(Context c, final String openId) {

		HttpUtils httpUtils = new HttpUtils();
		RequestParams param = new RequestParams();
		String name = getLoginInfo(c, "name");
		String pwd = getLoginInfo(c, "pass");
		param.addBodyParameter("name", name);
		param.addBodyParameter("Pwd", pwd);

		httpUtils.send(HttpMethod.POST,
				"http://app.byssteel.com/Accredit.asmx/Login",
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Log.e("TAG", "检测登录信息异常");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						String json = arg0.result;
						try {
							org.json.JSONObject object = new org.json.JSONObject(
									json);
							int isOk = object.getInt("isOk");
							if (isOk == 0) {
								org.json.JSONObject res = object
										.getJSONObject("result");
								String OpenId = res.getString("openId");

								if (OpenId == openId) {
									Config.ISLOGIN = true;
								}
							} else {
								Config.ISLOGIN = false;
							}
						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
					}
				});

		return Config.ISLOGIN;
	}
}
