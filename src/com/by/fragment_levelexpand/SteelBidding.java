package com.by.fragment_levelexpand;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.by.adapter.BuyerJoinBidingAdapter;
import com.by.interf.OnBidingDataRefreshListener;
import com.by.javabean.BidingJoinBean;
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

public class SteelBidding extends Fragment implements OnClickListener,
		OnScrollListener, OnBidingDataRefreshListener {

	private View view;
	private ImageView back;
	private FragmentManager manager;
	private Activity mActivity;
	private TextView pinpainame;
	private TextView weight;
	private TextView tidu;
	private TextView qipaijia;
	private TextView dangqianjia;
	private TextView endtime;
	private TextView ruweistate;
	private TextView bidingstate;
	private SharedPreferences sp;
	private String Path = "buyer.asmx/MyAuctionChu";
	private String cCID;
	private BidingJoinBean joinBean;
	private List<BidingJoinBean> beans;
	private BuyerJoinBidingAdapter adapter = null;
	private ListView lv;
	private boolean flag = true;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what != 0) {
				if (flag) {
					adapter = null;
					getDataFromNet();
				}
			}
		}
	};
	private TextView empty;
	private SwipeRefreshLayout layout;
	private int CurPage = 1;
	private boolean isLoaded = false;
	private boolean isBottom = false;
	private ScheduledExecutorService scheduled;

	@Override
	public void onAttach(Activity activity) {
		// TODO 自动生成的方法存根
		super.onAttach(activity);
		mActivity = activity;
		Config.ITEM_POSITION = 332;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		view = inflater.inflate(R.layout.steelbid_layout, null);
		cCID = getArguments().getString("ccid");
		initView();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		adapter = null;
		sp = mActivity.getSharedPreferences("LoginInfo", 0);
		dataRefreshShow();
		getDataFromNet();// 填充数据
	}

	private void getDataFromNet() {

		HttpUtils utils = new HttpUtils();
		String openId = sp.getString("openId", null);
		RequestParams params = new RequestParams();
		params.addBodyParameter("pageNo", String.valueOf(CurPage));
		params.addBodyParameter("OpenId", openId);
		params.addBodyParameter("ccid", cCID);
		utils.send(HttpMethod.POST, Config.URL_PATH + Path, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO 自动生成的方法存根
						Toast.makeText(mActivity, "error=" + arg0.toString(), 0)
								.show();
						NetworkUtil.isNetworkConnect(mActivity);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						String json = arg0.result;
						if ("-1".equals(json)) {

							LoginUtils.onLogin(mActivity, true,
									Config.FRAGMENT_TYPE);
						} else {
							beans = JSON.parseArray(json, BidingJoinBean.class);

							if (beans.size() != 0) {

								if (adapter == null) {
									adapter = new BuyerJoinBidingAdapter(beans,
											SteelBidding.this);
									lv.setAdapter(adapter);

								} else {
									adapter.setData(beans);
								}
							} else {
								lv.setEmptyView(empty);
							}
							isLoaded = true;
						}
					}
				});
	}

	private void initView() {

		manager = getFragmentManager();
		back = (ImageView) view.findViewById(R.id.iv_back);
		lv = (ListView) view.findViewById(R.id.bidlist);
		empty = (TextView) view.findViewById(R.id.empty);
		layout = (SwipeRefreshLayout) view
				.findViewById(R.id.swipeRefreshLayout);
		lv.setEmptyView(empty);
		setRefreshLayout();
		lv.setOnScrollListener(this);
		back.setOnClickListener(this);

	}

	private void dataRefreshShow() {

		scheduled = Executors.newSingleThreadScheduledExecutor();
		DataRefreshTask task = new DataRefreshTask();
		/**
		 * @param command
		 *            the task to execute 确定执行的任务
		 * @param initialDelay
		 *            the time to delay first execution 推迟第一次执行的时间
		 * @param delay
		 *            the delay between the termination of one 时间间隔
		 * @param unit
		 *            the time unit of the initialDelay and delay parameters
		 *            单位时间单位
		 */
		scheduled.scheduleAtFixedRate(task, 5, 5, TimeUnit.SECONDS);
	}

	private void setRefreshLayout() {
		layout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		layout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				CurPage = 1;
				adapter = null;
				beans.clear();
				getDataFromNet();
				if (isLoaded) {
					layout.setRefreshing(false);
					isLoaded = false;
				}
			}

		});

	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		manager.popBackStackImmediate();

	}

	private class DataRefreshTask implements Runnable {

		@Override
		public void run() {

			handler.sendEmptyMessage(1);

		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {// 闲置状态
			CurPage++;
			try {
				Thread.sleep(500);
				getDataFromNet();
				isBottom = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
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

	@Override
	public void onPause() {
		// TODO 自动生成的方法存根
		super.onPause();
		scheduled.shutdownNow();// 关闭刷新服务

	}

	@Override
	public void onBidingDataRefreshListener(boolean b) {
		// TODO 自动生成的方法存根
		Log.e("TAG", "onBidingDataRefreshListener" + b);
		flag = b;
	}
}
