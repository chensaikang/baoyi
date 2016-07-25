package com.by.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cn.jpush.android.api.JPushInterface;

import com.example.baoyisteel.BaseActivity;
import com.example.baoyisteel.R;

public class IndentStateActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO
		super.onCreate(savedInstanceState);
		setContentView(R.layout.handorder);
	}

	public void onClick(View v) {
		finish();
	}

	public void onQuery(View v) {

		Intent intent = new Intent(this, BaseActivity.class);
		intent.putExtra("position", 22);
		startActivity(intent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();

		JPushInterface.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

}
