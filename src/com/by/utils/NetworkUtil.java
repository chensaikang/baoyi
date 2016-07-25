package com.by.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * 
 * @author 监测手机端网络连接的状态
 * 
 */
public class NetworkUtil {

	public static boolean isNetworkAvailable(final Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
			return true;
		} else {
			AlertDialog.Builder ab = new AlertDialog.Builder(context);

			// 设定标题
			ab.setMessage("网络连接断开，请检查网络");
			// 设定退出按钮

			// 网络设置按钮
			ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
					context.startActivity(intent);
					dialog.dismiss();

				}
			}).show();
		}
		return false;
	}

	public static boolean isNetworkConnect(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			return true;
		}

		return false;
	}

	public static boolean isWiFiConnected(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}

		return false;
	}

	public static String getNetworkTypeName(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
		if (activeNetworkInfo != null) {
			// ConnectivityManager.TYPE_WIFI;
			// activeNetworkInfo.getType();
			return activeNetworkInfo.getTypeName();
		}
		return "";
	}
}
