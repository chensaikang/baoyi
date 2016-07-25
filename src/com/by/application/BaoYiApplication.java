package com.by.application;

import android.app.Application;
import cn.jpush.android.api.JPushInterface;

public class BaoYiApplication extends Application {

	private boolean isDownload;
	private String updateUrl;

	@Override
	public void onCreate() {
		super.onCreate();

		isDownload = false;// ÊÇ·ñ¸üÐÂ

		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
	}

	public String getUpdateUrl() {
		return updateUrl;
	}

	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}
}
