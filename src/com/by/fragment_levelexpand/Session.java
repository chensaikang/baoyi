package com.by.fragment_levelexpand;

import java.util.ArrayList;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.by.adapter.SessionAdapter;
import com.by.javabean.SessionBean;
import com.by.utils.Config;
import com.by.utils.LoginUtils;
import com.by.utils.NetworkUtil;
import com.example.baoyisteel.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Session extends Fragment implements OnScrollListener,
		OnClickListener, OnItemClickListener {

	private Activity mActivity;
	private View inflate;
	private ListView lv;
	private ImageView back;
	private SwipeRefreshLayout refresh;
	private int CurPage = 1;
	private SharedPreferences sp;
	private String openId;
	private boolean isBottom = false;// 初始化条目是否在抵达底部
	private SessionAdapter adapter = null;
	private TextView empty;
	private List<SessionBean> beans;
	private boolean isLoaded = false;
	private static int i = 0;

	@Override
	public void onAttach(Activity activity) {
		// TODO 自动生成的方法存根
		super.onAttach(activity);
		++i;
		Log.e("TAG", "onAttach_I==" + i);
		mActivity = activity;
		Config.ITEM_POSITION = 32;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("TAG", "onCreateView-SESSION");
		inflate = inflater.inflate(R.layout.session, null);
		initView();// 初始视图
		return inflate;
	}

	private void initView() {

		lv = (ListView) inflate.findViewById(R.id.session_lv);
		back = (ImageView) inflate.findViewById(R.id.iv_log);
		empty = (TextView) inflate.findViewById(R.id.empty);
		refresh = (SwipeRefreshLayout) inflate
				.findViewById(R.id.swipeRefreshLayout);// 刷新控件
		lv.setEmptyView(empty);
		lv.setOnScrollListener(this);
		lv.setOnItemClickListener(this);
		back.setOnClickListener(this);

		sp = mActivity.getSharedPreferences("LoginInfo", 0);// 获取openId

		// beans = new ArrayList<SessionBean>();

		setRefresh();

		// // 联网状态下获取数据
		// if (NetworkUtil.isNetworkConnect(mActivity)) {
		//
		// getDataFromNet();
		// }

	}

	private void setRefresh() {
		refresh.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);// 圆环颜色设置
		refresh.setOnRefreshListener(new OnRefreshListener() {// 数据刷新操作
			@Override
			public void onRefresh() {

				CurPage = 1;
				adapter = null;
				beans.clear();
				getDataFromNet();
				if (isLoaded) {
					refresh.setRefreshing(false);
					isLoaded = false;
				}
			}
		});
	}

	// 网络交互---------------我的场次
	private void getDataFromNet() {

		if (NetworkUtil.isNetworkConnect(mActivity)) {

			HttpUtils utils = new HttpUtils();
			openId = sp.getString("openId", null);
			String url = Config.URL_PATH + "Seller.asmx/MySession?openId="
					+ openId + "&pageNo=" + CurPage;
			Log.e("TAG", "SESSION--url" + url);
			utils.send(HttpMethod.GET, url, new RequestCallBack<String>() {
				@Override
				public void onFailure(HttpException arg0, String arg1) {

					Toast.makeText(mActivity, "获取数据失败，请检查网络",
							Toast.LENGTH_SHORT).show();
				}

				/**
				 * List<SessionBean> list = parseJson(json);
				 * beans.addAll(list);// 开启点击事件获取item条目
				 * 
				 * if (list.size() != 0) { if (adapter == null) { adapter = new
				 * SessionAdapter(); adapter.setData(mActivity, list);
				 * lv.setAdapter(adapter); } else { adapter.setData(mActivity,
				 * list); } isLoaded = true; }
				 */
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					String json = arg0.result;
					Log.e("TAG", "JSON_SESSION=" + json);
					if ("-1".equals(json)) {
						LoginUtils.onLogin(mActivity, true, 0);// 重新登录
					} else if ("-2".equals(json)) {
						empty.setText("对不起，您未开通此服务");// 为开通此项服务
						// lv.setEmptyView(empty);
					} else {
						List<SessionBean> list = parseJson(json);
						beans.addAll(list);// 开启点击事件获取item条目
						if (list.size() != 0) {
							if (adapter == null) {
								adapter = new SessionAdapter();
								adapter.setData(mActivity, list);
								lv.setAdapter(adapter);
							} else {
								adapter.setData(mActivity, list);
							}
							isLoaded = true;
						}
					}
				}
			});
		}
	}

	// fastjson解析
	private List<SessionBean> parseJson(String json) {

		return JSON.parseArray(json, SessionBean.class);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case SCROLL_STATE_FLING:// 快速滑动状态
			break;
		case SCROLL_STATE_IDLE:// 空闲状态
			if (isBottom == true) {
				CurPage++;
				try {
					Thread.sleep(500);
					getDataFromNet();
					isBottom = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
		case SCROLL_STATE_TOUCH_SCROLL:// 接触滑动状态
			break;
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
	public void onClick(View v) {
		getFragmentManager().popBackStackImmediate();

	}

	@Override
	public void onResume() {

		Log.e("TAG", "onResume");
		super.onResume();
		adapter = null;
		beans = new ArrayList<SessionBean>();
		getDataFromNet();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SessionBean bean = beans.get(position);// 获取点击条目
		Toast.makeText(mActivity, "position==" + position + "id==" + id, 0)
				.show();

	}

}
