package com.by.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.by.activity.LoginActivity;
import com.by.application.BaoYiApplication;
import com.by.updata.NotificationUpdateActivity;
import com.by.utils.Config;
import com.by.utils.NetworkUtil;
import com.example.baoyisteel.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Setting extends Fragment implements OnCheckedChangeListener,
		OnClickListener {

	private View inflate;
	private SharedPreferences sharedPreferences;
	private CheckBox jpush_toggle;
	private CheckBox login_toggle;
	private CheckBox version_tog;
	private Button other;
	private Button cancel;
	private Activity mActivity;
	private PackageManager manager;
	private int curVersion;
	private String curName;
	@SuppressWarnings("unused")
	private int latestVersion = -1;
	private String latestVersionName = "";
	private BaoYiApplication application;
	private String upApkUrl = "http://app.byssteel.com/home.asmx/AppVer";

	@Override
	public void onAttach(Activity activity) {
		// TODO 自动生成的方法存根
		super.onAttach(activity);
		mActivity = activity;
		Config.ITEM_POSITION = 5;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		inflate = inflater.inflate(R.layout.set, null);

		initView();

		return inflate;

	}

	private void initView() {

		application = (BaoYiApplication) mActivity.getApplication();
		sharedPreferences = getActivity().getSharedPreferences("LoginInfo", 0);
		jpush_toggle = (CheckBox) inflate.findViewById(R.id.jp_tog);
		login_toggle = (CheckBox) inflate.findViewById(R.id.login_tog);
		version_tog = (CheckBox) inflate.findViewById(R.id.version_tog);
		other = (Button) inflate.findViewById(R.id.other);
		cancel = (Button) inflate.findViewById(R.id.cancel);
		jpush_toggle.setOnCheckedChangeListener(this);
		login_toggle.setOnCheckedChangeListener(this);
		version_tog.setOnCheckedChangeListener(this);
		other.setOnClickListener(this);
		cancel.setOnClickListener(this);
		jpush_toggle.setChecked(true);
		login_toggle.setChecked(true);
	}

	@Override
	public void onClick(View v) {

		if (v.isClickable()) {
			v.setBackgroundResource(R.drawable.cancel_set_foucse);
		} else {
			v.setBackgroundResource(R.drawable.cancel_set_normal);
		}
		int id = v.getId();
		switch (id) {
		case R.id.other:
			Toast.makeText(getActivity(), "other", 0).show();
			Editor edit = sharedPreferences.edit();
			edit.clear();
			edit.commit();
			Intent intent = new Intent();
			intent.setClass(getActivity(), LoginActivity.class);
			startActivity(intent);
			Config.ITEM_POSITION = 0;
			break;
		case R.id.cancel:
			getActivity().finish();
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int Id = buttonView.getId();
		switch (Id) {
		case R.id.jp_tog://
			if (isChecked) {
				JPushInterface.resumePush(getActivity());
			} else {
				JPushInterface.stopPush(getActivity());
			}
			break;

		case R.id.login_tog://

			if (!isChecked) {

				Editor edit = sharedPreferences.edit();
				edit.clear();
				edit.commit();
			}
			break;
		case R.id.version_tog://
			Log.e("TAG", "login_tog");
			// Toast.makeText(getActivity(), "当前为最新版本", 0).show();

			versionUpdata();
			break;
		}

	}

	public boolean isLatestVersion() {
		// 获取当地版本信息
		manager = mActivity.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(
					mActivity.getPackageName(), 0);

			curVersion = info.versionCode;
			curName = info.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return getVersionFromNetOfLatest();
	}

	// 获取最新应用
	public boolean getVersionFromNetOfLatest() {

		HttpUtils utils = new HttpUtils();
		if (NetworkUtil.isNetworkConnect(mActivity)) {

			utils.send(HttpMethod.GET, upApkUrl, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {

					Toast.makeText(mActivity, "更新失败，请检查网络连接。。。", 0).show();
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {

					String restult = arg0.result;
					parseData(restult);// 版本信息实例化

				}
			});
		}
		// if (latestVersion != -1 && latestVersion > curVersion) {
		// return false;
		// }
		// 判断name是否相同
		if (curName != latestVersionName) {
			Log.e("TAG", "FLAG===" + curName + "====" + latestVersionName);
			return false;
		}
		return true;
	}

	/**
	 * {"versionCode":2,"versionName":"1.0.1","Url":
	 * "http://www.baoyi188.com/Download/BaoYiSteel.apk"}
	 */
	// 此方法对最新版本信息进行实例化
	public void parseData(String json) {
		try {

			JSONObject object = new JSONObject(json);
			latestVersion = object.getInt("versionCode");
			latestVersionName = object.getString("versionName");
			// Log.e("TAG", "CODE==" + latestVersion + "NAME=="
			// + latestVersionName);
			application.setUpdateUrl(object.getString("Url"));

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void showUpdateDialog2() {
		final Dialog dialog = new Dialog(mActivity,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.update_dialog);
		Button cancel = (Button) dialog.findViewById(R.id.next);
		Button ok = (Button) dialog.findViewById(R.id.justnow);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				version_tog.setChecked(false);
				dialog.dismiss();
			}
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e("TAG", "LoginUtils-ok");
				Intent it = new Intent(mActivity,
						NotificationUpdateActivity.class);
				startActivity(it);
				version_tog.setChecked(false);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	// 更新设置
	@SuppressWarnings("unused")
	private void showUpdateDialog() {
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle("检测到新版本");
		builder.setMessage("是否下载更新?");
		builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
				Intent it = new Intent(mActivity,
						NotificationUpdateActivity.class);
				startActivity(it);
				version_tog.setChecked(false);
			}
		}).setNegativeButton("下次再说", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				version_tog.setChecked(false);
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();
	}

	public void versionUpdata() {

		if (version_tog.isChecked()) {

			if (!isLatestVersion()) {
				// Log.e("TAG", "更新");
				application.setDownload(true);
				showUpdateDialog2();// 弹出更新设置
			} else {
				Toast.makeText(mActivity, "当前为最新版本  " + curVersion, 0).show();
			}
		}
	}

	@Override
	public void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();

	}

}
