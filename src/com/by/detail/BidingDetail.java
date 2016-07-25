package com.by.detail;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import com.example.baoyisteel.R;

public class BidingDetail extends Fragment {

	private View view;
	private WebView web;
	private ImageView back;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO 自动生成的方法存根

		Bundle bundle = getArguments();
		String url = bundle.getString("url");

		view = inflater.inflate(R.layout.biding_detail, null);
		back = (ImageView) view.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getFragmentManager().popBackStackImmediate();

			}
		});

		web = (WebView) view.findViewById(R.id.web);
		web.loadUrl(url);
		return view;
	}
}
