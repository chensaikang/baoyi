package com.by.activity;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.by.utils.Config;
import com.by.utils.NetworkUtil;
import com.example.baoyisteel.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 
 * @author CSK
 * 
 */
public class LoginActivity extends Activity {

	private ImageView back;
	private EditText et_num;
	private EditText et_pass;
	@SuppressWarnings("unused")
	private Button login;
	private String url = Config.URL_PATH + "Accredit.asmx/Login";
	private Dialog dialog;
	private SharedPreferences sharedPreferences;
	private String num;
	private String pass;
	@SuppressWarnings("unused")
	private int extra;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		setItemPosition();
		JPushSetting();
		initView();

	}

	public void setItemPosition() {

		Intent intent = getIntent();
		extra = intent.getIntExtra("Position", 0);

	}

	private void initView() {

		// 获取sp
		sharedPreferences = getSharedPreferences("LoginInfo", 0);
		back = (ImageView) findViewById(R.id.iv_log);
		et_num = (EditText) findViewById(R.id.num_login);
		et_pass = (EditText) findViewById(R.id.pass_login);
		login = (Button) findViewById(R.id.login_btn);
		num = sharedPreferences.getString("name", null);
		pass = sharedPreferences.getString("Pwd", null);
		et_num.setText(num);
		et_pass.setText(pass);
		et_num.setTextColor(R.color.gray);
		et_pass.setTextColor(R.color.gray);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				LoginActivity.this.finish();
				// Config.ITEM_POSITION = 404;
				Config.LOGIN_FLAG = true;

			}
		});
	}

	public void onLogin(View v) {

		num = et_num.getText().toString();
		pass = et_pass.getText().toString();
		if (num != null && pass != null) {

			RequestParams params = new RequestParams();
			params.addBodyParameter("name", num);
			params.addBodyParameter("Pwd", pass);
			HttpUtils utils = new HttpUtils();
			if (NetworkUtil.isNetworkAvailable(this)) {

				utils.send(HttpMethod.POST, url, params,
						new RequestCallBack<String>() {

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {

								if (dialog != null)
									dialog.dismiss();
								Toast.makeText(LoginActivity.this, arg1, 0)
										.show();
							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								if (dialog != null) {
									try {
										Thread.sleep(500);
										dialog.dismiss();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}

								}

								String info = arg0.result;
								try {
									JSONObject object = new JSONObject(info);
									int isOk = object.getInt("isOk");

									if (isOk == 0) {

										Config.LOGIN_FLAG = false;
										JSONObject res = object
												.getJSONObject("result");
										String openId = res.getString("openId");
										String loginId = res
												.getString("loginId");

										Editor edit = sharedPreferences.edit();
										edit.putString("name", num);
										edit.putString("Pwd", pass);//
										edit.putString("openId", openId);//
										edit.putString("loginId", loginId);//
										edit.commit();

										// 设置昵称
										setAlias();
										LoginActivity.this.finish();
									}

									else {
										String result = object
												.getString("result");
										Toast.makeText(LoginActivity.this,
												result, 0).show();
										et_num.setText(null);
										et_pass.setText(null);
									}
								} catch (JSONException e) {
									// TODO
									e.printStackTrace();
								}
							}

							@Override
							public void onStart() {
								// TODO
								super.onStart();
								// 创建loading提示
								dialog = new Dialog(
										LoginActivity.this,
										android.R.style.Theme_Translucent_NoTitleBar);
								dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
								dialog.setContentView(R.layout.login_loading);
								dialog.show();
							}
						});
			}
		}
	}

	private void JPushSetting() {

		// 设置推送可接受通知条数
		JPushInterface.setLatestNotificationNumber(getApplicationContext(), 5);

		// 设置默认通知 flag为0

		// BasicPushNotificationBuilder Notifybuilder = new
		// BasicPushNotificationBuilder(
		// this);
		// Notifybuilder.statusBarDrawable = R.drawable.androidlogo;
		// JPushInterface.setDefaultPushNotificationBuilder(Notifybuilder);

		// 设置通知样式
		CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(
				this, R.layout.push_layout, R.id.iv, R.id.title, R.id.msg);

		builder.notificationDefaults = Notification.DEFAULT_SOUND
				| Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
		builder.statusBarDrawable = R.drawable.androidlogo;
		builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; // 可自动消失
		// JPushInterface.setDefaultPushNotificationBuilder(1, builder);
		JPushInterface.setPushNotificationBuilder(1, builder);// 样式一

	}

	// 推送昵称设置
	void setAlias() {
		JPushInterface.setAlias(getApplicationContext(), num,
				new TagAliasCallback() {

					// code=0: 设置成功
					@Override
					public void gotResult(int code, String alias,
							Set<String> tags) {
						if (code == 0) {
							Toast.makeText(LoginActivity.this, "昵称为" + num,
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		// JPushInterface.setAliasAndTags(arg0, arg1, arg2, arg3);
	}

	//
	@Override
	protected void onPause() {
		super.onPause();
		finish();
		JPushInterface.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	public void onCancel(View v) {

		if (et_num != null || et_pass != null) {
			et_num.setText(null);
			et_pass.setText(null);
		} else {
			finish();
		}
	}

}
