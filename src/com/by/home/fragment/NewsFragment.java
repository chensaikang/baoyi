package com.by.home.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.by.detail.ManagerDetail;
import com.by.javabean.DataBean;
import com.by.utils.NetworkUtil;
import com.example.baoyisteel.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class NewsFragment extends Fragment implements OnScrollListener {

	private ListView lv;
	private List<DataBean> beans;
	private BarAdapter adapter;
	private int CurPage = 1;
	private boolean isBottom = false;
	private List<DataBean> list = new ArrayList<DataBean>();
	private Activity mActivity;
	private SwipeRefreshLayout layout;
	private boolean isLoaded = false;
	private FragmentManager manager;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		manager = getFragmentManager();

		View view = initView(inflater);

		if (NetworkUtil.isNetworkAvailable(mActivity)) {

			getJson();
		}
		return view;
	}

	private View initView(LayoutInflater inflater) {

		View view = inflater.inflate(R.layout.fragment, null);
		lv = (ListView) view.findViewById(R.id.fg_lv);
		ProgressBar empty = (ProgressBar) view.findViewById(R.id.empty);
		layout = (SwipeRefreshLayout) view
				.findViewById(R.id.swipeRefreshLayout);
		setLayoutRefresh();
		lv.setEmptyView(empty);
		lv.setOnScrollListener(this);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				int Id = list.get(position).getId();

				FragmentTransaction transaction = manager.beginTransaction();
				ManagerDetail fragment = new ManagerDetail();
				Bundle bundle = new Bundle();
				bundle.putInt("Web", Id);
				fragment.setArguments(bundle);
				transaction.replace(R.id.fragment, fragment);
				transaction.addToBackStack(null);
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

				CurPage = 1;
				adapter = null;
				list.clear();
				getJson();
				if (isLoaded) {
					layout.setRefreshing(false);
					isLoaded = false;
				}
			}

		});
	}

	public void getJson() {
		Bundle bundle = getArguments();
		String url = bundle.getString("url") + CurPage;
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

			}

			@Override
			public void onSuccess(ResponseInfo<String> info) {

				String str = info.result;
				JSONArray array;
				try {
					array = new JSONArray(str);
					beans = new ArrayList<DataBean>();

					for (int i = 0; i < array.length(); i++) {

						JSONObject object = array.getJSONObject(i);
						DataBean bean = new DataBean();

						int id = object.getInt("Id");

						String content = object.getString("content");

						String time = object.getString("time");

						bean.setId(id);
						bean.setContent(content);
						bean.setTime(time);

						beans.add(bean);
					}
					list.addAll(beans);

					if (adapter == null) {
						adapter = new BarAdapter(beans);
						lv.setAdapter(adapter);
					} else {
						adapter.setData(beans);
						adapter.notifyDataSetChanged();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				isLoaded = true;
			}
		});
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (scrollState == SCROLL_STATE_IDLE) {
			if (isBottom) {
				CurPage++;
				try {
					Thread.sleep(500);
					getJson();
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

}
