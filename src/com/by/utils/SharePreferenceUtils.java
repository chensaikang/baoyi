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
 * @author ���ؼ�����Ϣ�洢
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

	// �жϵ�½��Ϣ
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
						Log.e("TAG", "����¼��Ϣ�쳣");
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
							// TODO �Զ����ɵ� catch ��
							e.printStackTrace();
						}
					}
				});

		return Config.ISLOGIN;
	}
}
