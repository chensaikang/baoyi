package com.by.detail;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import com.by.utils.Config;
import com.by.utils.NetworkUtil;
import com.example.baoyisteel.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class ManagerDetail extends Fragment implements OnClickListener {

	private WebView web;
	private View inflate;
	private ImageView back;
	private Activity mActivity;

	@Override
	public void onAttach(Activity activity) {
		// TODO
		super.onAttach(activity);
		mActivity = activity;
		Config.ITEM_POSITION = 010;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		inflate = inflater.inflate(R.layout.manger_detail, null);

		Bundle bundle = getArguments();
		int id = bundle.getInt("Web");

		back = (ImageView) inflate.findViewById(R.id.iv_onback);
		web = (WebView) inflate.findViewById(R.id.detail_web);
		back.setOnClickListener(this);
		if (NetworkUtil.isNetworkAvailable(mActivity)) {

			setData(id);
		}

		return inflate;
	}

	// ÏêÇéÒ³Ìø×ª
	private void setData(int id) {
		String url = Config.URL_PATH + "Home.asmx/Detail?Id=" + id;
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				String json = arg0.result;
				web.loadUrl(json);
			}
		});
	}

	// »ØÍË
	@Override
	public void onClick(View v) {

		getFragmentManager().popBackStackImmediate();

	}

}
