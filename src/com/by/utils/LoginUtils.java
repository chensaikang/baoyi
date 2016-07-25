package com.by.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.by.activity.LoginActivity;
import com.example.baoyisteel.R;

/**
 * 
 * @author 登陆的油条 flag:是否回收上一级activity type：activity、fragment
 * 
 */
public class LoginUtils {

	private static int i = 0;

	// 此接口用于监听fragment的回退
	public interface CallBack {
		public void onCallback(Activity a, boolean flag, int type);
	}

	public int class_type;
	public static CallBack callBack = new CallBack() {

		@Override
		public void onCallback(Activity a, boolean flag, int type) {

			if (type == Config.CLASS_TYPE) {
				if (flag) {
					a.finish();
				}
			} else {
				if (flag) {
					a.getFragmentManager().popBackStackImmediate();
				}
			}
		}
	};

	public static void onLogin(final Activity c, final boolean flag,
			final int type) {
		++i;
		final Dialog dialog = new Dialog(c,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.login_dialog);
		Button cancel = (Button) dialog.findViewById(R.id.login_cancel);
		Button ok = (Button) dialog.findViewById(R.id.login_ok);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				callBack.onCallback(c, flag, type);

				dialog.dismiss();

			}
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(c, LoginActivity.class);
				c.startActivity(intent);
				dialog.dismiss();
			}
		});

		dialog.show();
	}
}
