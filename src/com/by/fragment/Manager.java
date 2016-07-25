package com.by.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.by.adapter.MangerAdapter;
import com.by.fragment_levelexpand.Search_Pm;
import com.by.javabean.ChildBean;
import com.by.javabean.GroupBean;
import com.by.utils.Config;
import com.by.utils.NetworkUtil;
import com.example.baoyisteel.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Manager extends Fragment {

	private View inflate;
	public int CurrentNum = 1;
	public static String URL_START = Config.URL_PATH
			+ "Manager.asmx/SeahData?pageNo=";
	public static String URL_END = "&ChanDi=&PinMing=&WareHouseName=&CaiZhi=&houDuMin=&houDuMax=&kuanDuMin=&kuanDuMax=&bypar=&orBy=&keys=";
	private ExpandableListView lv;
	@SuppressWarnings("unused")
	private String url;
	private List<List<ChildBean>> childList = new ArrayList<List<ChildBean>>();
	private List<GroupBean> group = new ArrayList<GroupBean>();
	private MangerAdapter adapter;
	private SharedPreferences sharedPreferences;
	@SuppressWarnings("unused")
	// private String openId;
	private boolean isBttom = false;
	private SwipeRefreshLayout refresh;
	private ImageView img;
	private boolean isLoaded = false;
	private TextView tv;
	@SuppressWarnings("unused")
	private FragmentManager manager;
	private Activity mActivity;

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		mActivity = activity;
		Config.ITEM_POSITION = 1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		inflate = inflater.inflate(R.layout.manager, null);
		sharedPreferences = getActivity().getSharedPreferences("LoginInfo", 0);
		// openId = sharedPreferences.getString("openId", null);
		manager = getFragmentManager();
		initView();
		getData();
		return inflate;
	}

	public void initView() {

		lv = (ExpandableListView) inflate.findViewById(R.id.ep_lv);
		tv = (TextView) inflate.findViewById(R.id.empty);
		lv.setGroupIndicator(null);
		img = (ImageView) inflate.findViewById(R.id.search);
		lv.setEmptyView(tv);
		adapter = null;
		lv.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {

				for (int i = 0; i < group.size(); i++) {
					if (groupPosition != i) {
						lv.collapseGroup(i);
					}
				}
			}
		});

		refresh = (SwipeRefreshLayout) inflate
				.findViewById(R.id.swipeRefreshLayout);
		refresh.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		refresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				CurrentNum = 1;
				adapter = null;
				getData();
				if (isLoaded) {

					refresh.setRefreshing(false);
					isLoaded = false;
				}

			}
		});

		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 碎片切换
				FragmentManager manager = getFragmentManager();
				FragmentTransaction transaction = manager.beginTransaction();
				transaction.replace(R.id.fragment, new Search_Pm());
				transaction.addToBackStack(null);
				transaction.commit();

			}

		});

		/*********************************** listview监听 ***********************************************/
		lv.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				return true;
			}
		});
		lv.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO
				if (scrollState == SCROLL_STATE_IDLE) {
					if (isBttom) {
						CurrentNum++;
						// 重置isBttom;并使数据加载延迟
						try {
							Thread.sleep(500);
							getData();
							isBttom = false;
						} catch (InterruptedException e) {
							//
							e.printStackTrace();
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				if ((firstVisibleItem + visibleItemCount == totalItemCount)) {
					isBttom = true;
				}
			}
		});
	}

	// 获取数据
	private void getData() {

		if (NetworkUtil.isNetworkAvailable(mActivity)) {

			HttpUtils utils = new HttpUtils();
			String urlPath = URL_START + CurrentNum + URL_END;

			utils.send(HttpMethod.GET, urlPath, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Toast.makeText(mActivity, arg0.toString(), 0).show();
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {

					String json = arg0.result;
					try {

						JSONArray array = new JSONArray(json);
						JSONObject object;
						GroupBean groupBean;
						ChildBean childBean;
						if (array.length() == 0) {
							tv.setText("暂无数据...");
						} else {
							for (int i = 0; i < array.length(); i++) {

								List<ChildBean> clist = new ArrayList<ChildBean>();
								object = array.getJSONObject(i);
								groupBean = new GroupBean();
								childBean = new ChildBean();

								String chanPin = object.getString("chanPin");
								String caiZhi = object.getString("caiZhi");
								String zhongLiang = object
										.getString("zhongLiang");
								// String danJia = object.getString("danJia");
								String danJia = object.getString("danJia");
								String id = object.getString("Id");
								groupBean.setCaizhi(caiZhi);
								groupBean.setZhongLiang(zhongLiang);
								groupBean.setChanPin(chanPin);
								groupBean.setDanJia(danJia);
								groupBean.setId(id);

								group.add(groupBean);

								String canKaoHouDu = object
										.getString("canKaoHouDu");
								String kunBaoHao = object
										.getString("kunBaoHao");
								String jiZhongFangShi = object
										.getString("jiZhongFangShi");
								String wuLiaoHao = object
										.getString("wuLiaoHao");
								String maiJia = object.getString("maiJia");
								String bianBu = object.getString("bianBu");
								String guiGe = object.getString("guiGe");
								String beiZhu = object.getString("beiZhu");
								String zhiXingBiaoZhun = object
										.getString("zhiXingBiaoZhun");
								String cangku = object.getString("cangKu");

								childBean.setBeiZhu(beiZhu);
								childBean.setBianBu(bianBu);
								childBean.setCanKaoHouDu(canKaoHouDu);
								childBean.setJiZhongFangShi(jiZhongFangShi);
								childBean.setKunBaoHao(kunBaoHao);
								childBean.setMaiJia(maiJia);
								childBean.setWuLiaoHao(wuLiaoHao);
								childBean.setZhiXingBiaoZhun(zhiXingBiaoZhun);
								childBean.setStorage(cangku);
								childBean.setGuiGe(guiGe);

								clist.add(childBean);

								childList.add(clist);
							}

							if (adapter == null) {
								// 实例化
								adapter = new MangerAdapter(mActivity, group,
										childList);
								lv.setAdapter(adapter);
							} else {
								adapter.setData(group, childList);
								adapter.notifyDataSetChanged();// 刷新
							}
							isLoaded = true;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	public void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();
		// toggleInput();
	}

	// 关闭虚拟键盘
	@SuppressWarnings({ "static-access", "unused" })
	private void toggleInput() {
		// InputMethodManager imm = (InputMethodManager) mActivity
		// .getSystemService(mActivity.INPUT_METHOD_SERVICE);
		// if (imm.isActive()) {
		// //imm.toggleSoftInput(showFlags, hideFlags);
		// // imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
		// // InputMethodManager.HIDE_NOT_ALWAYS);
		// imm.hideSoftInputFromInputMethod(token, flags);
		// }
		((InputMethodManager) mActivity
				.getSystemService(mActivity.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(mActivity.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
