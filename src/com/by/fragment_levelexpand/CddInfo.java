package com.by.fragment_levelexpand;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.by.utils.Config;
import com.by.utils.NetworkUtil;
import com.example.baoyisteel.R;

/**
 * 
 * @author CSK
 * 
 */
public class CddInfo extends Fragment implements OnClickListener {

	private WebView web;
	private View inflate;
	private Activity mActivity;

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		mActivity = activity;
		Config.ITEM_POSITION = 2211;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		inflate = inflater.inflate(R.layout.cdd_layout, null);
		inflate.findViewById(R.id.iv_orderdetail).setOnClickListener(this);

		Bundle bundle = getArguments();
		String url = bundle.getString("url");

		web = (WebView) inflate.findViewById(R.id.web);

		if (NetworkUtil.isNetworkAvailable(mActivity)) {

			web.loadUrl(url);
		}
		return inflate;
	}

	// 回退
	@Override
	public void onClick(View v) {

		getFragmentManager().popBackStackImmediate();

	}

}
