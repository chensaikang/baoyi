package com.by.fragment_levelexpand;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.by.adapter.AuctionAdapter;
import com.by.javabean.AuctionBean;
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

public class BuyerBiding extends Fragment implements OnScrollListener {

	private Activity mActivity;
	private View view;
	private ListView ppLv;// 拼盘列表
	private SharedPreferences sp;
	private int pageNo = 1;
	private TextView empty;
	private SwipeRefreshLayout refresh;
	private boolean isBottom = false;
	private List<AuctionBean> list;// 数据集合；item条目显示
	private AuctionAdapter adapter;
	private boolean flag = false;

	@Override
	public void onAttach(Activity activity) {
		// TODO 自动生成的方法存根
		super.onAttach(activity);
		mActivity = activity;
		Config.ITEM_POSITION = 331;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.buyer_biding, null);
		ppLv = (ListView) view.findViewById(R.id.bidlist);
		empty = (TextView) view.findViewById(R.id.empty);
		refresh = (SwipeRefreshLayout) view
				.findViewById(R.id.swipeRefreshLayout);
		ppLv.setEmptyView(empty);
		setRefresh();
		ppLv.setOnScrollListener(this);
		view.findViewById(R.id.iv_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						getFragmentManager().popBackStackImmediate();

					}
				});

		return view;
	}

	// 下拉刷新设置
	private void setRefresh() {

		refresh.setColorSchemeResources(
				android.R.color.holo_blue_bright,// 设置圆环颜色值
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		// 刷新操作
		refresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				pageNo = 1;
				adapter = null;
				list.clear();
				getDataFromNet();
				if (flag) {
					refresh.setRefreshing(false);
					flag = false;
				}
			}
		});
	}

	// 下载数据
	public void getDataFromNet() {
		if (NetworkUtil.isNetworkConnect(mActivity)) {

			String openId = sp.getString("openId", null);

			HttpUtils utils = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("OpenId", openId);
			params.addBodyParameter("pageNo", String.valueOf(pageNo));
			utils.send(HttpMethod.POST, Config.URL_PATH
					+ "buyer.asmx/MyAuction", params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO 自动生成的方法存根
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {

							String json = arg0.result;
							if ("-1".equals(json)) {
								Log.e("TAG", "EEROR" + json);
								LoginUtils.onLogin(mActivity, false,
										Config.FRAGMENT_TYPE);
							} else if (json == null) {
								ppLv.setEmptyView(empty);
							} else {
								list = JSON.parseArray(json, AuctionBean.class);
								if (adapter == null) {
									adapter = new AuctionAdapter(list,
											mActivity);
									ppLv.setAdapter(adapter);
								} else {
									adapter.setData(list);
								}
								flag = true;// 数据刷新
							}
						}
					});
		}
	}

	@Override
	public void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();
		sp = mActivity.getSharedPreferences("LoginInfo", 0);
		adapter = null;
		getDataFromNet();

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO 自动生成的方法存根
		if (scrollState == SCROLL_STATE_IDLE) {
			if (isBottom) {
				pageNo++;
				try {
					Thread.sleep(500);
					getDataFromNet();
					isBottom = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount == totalItemCount) {
			isBottom = true;
		}

	}

	public void onBack(View v) {
		getFragmentManager().popBackStackImmediate();
	}
}
