package com.by.fragment;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.by.adapter.BidingAdpter;
import com.by.detail.BidingDetail;
import com.by.javabean.BiddingBean;
import com.by.utils.Config;
import com.by.utils.LoginUtils;
import com.by.utils.NetworkUtil;
import com.example.baoyisteel.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Biding extends Fragment {

	private View view;
	private Activity mActivity;
	private ListView lv;
	private String url = Config.URL_PATH + Config.JJinfo;
	private BidingAdpter adapter;
	private SharedPreferences sp;
	private String openId;
	private List<BiddingBean> list;
	private FragmentManager manager;
	private TextView tv_empty;
	private ProgressBar empty;
	private SwipeRefreshLayout layout;
	private boolean isLoaded = false;

	@Override
	public void onAttach(Activity activity) {
		// TODO 自动生成的方法存根
		super.onAttach(activity);
		mActivity = activity;
		Config.ITEM_POSITION = 2;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.biding, null);
		lv = (ListView) view.findViewById(R.id.biding_lv);
		layout = (SwipeRefreshLayout) view
				.findViewById(R.id.swipeRefreshLayout);
		empty = (ProgressBar) view.findViewById(R.id.empty);
		tv_empty = (TextView) view.findViewById(R.id.tvempty);
		lv.setEmptyView(empty);
		setLayoutRefresh();// 刷新设置
		manager = getFragmentManager();
		// 条目监听
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BiddingBean bean = list.get(position);
				FragmentTransaction transaction = manager.beginTransaction();
				BidingDetail fragment = new BidingDetail();
				Bundle bundle = new Bundle();
				bundle.putString("url", bean.getXiangqin());
				fragment.setArguments(bundle);
				transaction.replace(R.id.fragment, fragment).addToBackStack(
						null);
				transaction.commit();
			}
		});

		return view;
	}

	private void setLayoutRefresh() {

		layout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		layout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				adapter = null;
				list.clear();
				getDataFromNet();
				if (isLoaded) {
					layout.setRefreshing(false);
					isLoaded = false;
				}
			}
		});
	}

	@Override
	public void onResume() {

		super.onResume();
		sp = mActivity.getSharedPreferences("LoginInfo", 0);

		if (NetworkUtil.isNetworkConnect(mActivity)) {

			getDataFromNet();
		}
	}

	// 获取数据
	public void getDataFromNet() {

		HttpUtils utils = new HttpUtils();
		RequestParams params = new RequestParams();
		openId = sp.getString("openId", "");
		params.addBodyParameter("OpenId", openId);
		utils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

				Toast.makeText(mActivity, arg0.toString(), 0).show();

			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO 自动生成的方法存根
				String json = arg0.result;
				if ("-1".equals(json)) {
					LoginUtils.onLogin(mActivity, false, Config.FRAGMENT_TYPE);
				} else {
					list = JSON.parseArray(json, BiddingBean.class);
					if (list.size() == 0) {

						empty.setVisibility(View.GONE);
						lv.setEmptyView(tv_empty);
					} else {
						adapter = new BidingAdpter(list, mActivity);
						lv.setAdapter(adapter);
					}
					isLoaded = true;
				}
			}
		});
	}

}
