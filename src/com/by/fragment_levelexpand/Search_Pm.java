package com.by.fragment_levelexpand;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.by.adapter.MangerAdapter;
import com.by.interf.OnLoginListener;
import com.by.javabean.ChildBean;
import com.by.javabean.GroupBean;
import com.by.utils.Config;
import com.by.utils.LoginUtils;
import com.by.utils.NetworkUtil;
import com.example.baoyisteel.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Search_Pm extends Fragment implements OnLoginListener,
		OnScrollListener, OnClickListener {

	private ImageView search;
	public static String URL_START = Config.URL_PATH
			+ "Manager.asmx/SeahData?pageNo=";
	public static String URL_END = "&ChanDi=&PinMing=&WareHouseName=&CaiZhi=&houDuMin="
			+ "&houDuMax=&kuanDuMin=&kuanDuMax=&bypar=";
	private static String ORBY = "&orBy=";
	private static String KEYS = "&keys=";
	private String bypar = null;
	private String orby = null;//
	private int CurPage = 1;
	private String Keys = null;
	private List<List<ChildBean>> childList = new ArrayList<List<ChildBean>>();
	private List<GroupBean> group = new ArrayList<GroupBean>();
	private ExpandableListView lv;
	private MangerAdapter adapter;
	private boolean isBottom = false;
	private SwipeRefreshLayout refresh;
	private boolean isLoaded = false;
	private LinearLayout ofWeight;
	private LinearLayout ofPrice;
	private LinearLayout ofNormal;
	private View inflate;
	private TextView empty;
	private Activity mActivity;
	private InputMethodManager inputService;
	private EditText et;
	private ImageView icon_weight;
	private ImageView icon_danjia;
	private ImageView icon_normal;
	private TextView tv_price;
	private TextView tv_normal;
	private TextView tv_weight;

	@Override
	public void onAttach(Activity activity) {
		// TODO
		super.onAttach(activity);
		mActivity = activity;
		// Log.e("TAG", "POSITION==" + Config.ITEM_POSITION);
		Config.ITEM_POSITION = 11;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		inflate = inflater.inflate(R.layout.search, null);
		initView();

		return inflate;
	}

	private void initView() {

		lv = (ExpandableListView) inflate.findViewById(R.id.search_lv);
		et = (EditText) inflate.findViewById(R.id.content);
		search = (ImageView) inflate.findViewById(R.id.iv_query);
		search.setOnClickListener(this);
		inflate.findViewById(R.id.iv_back).setOnClickListener(this);
		refresh = (SwipeRefreshLayout) inflate
				.findViewById(R.id.swipeRefreshLayout);

		ofWeight = (LinearLayout) inflate.findViewById(R.id.ofweight);
		ofPrice = (LinearLayout) inflate.findViewById(R.id.ofprice);
		ofNormal = (LinearLayout) inflate.findViewById(R.id.offormal);
		empty = (TextView) inflate.findViewById(R.id.empty);
		icon_weight = (ImageView) inflate.findViewById(R.id.iv_weight);
		icon_danjia = (ImageView) inflate.findViewById(R.id.iv_danjia);
		icon_normal = (ImageView) inflate.findViewById(R.id.iv_normal);

		tv_price = (TextView) inflate.findViewById(R.id.tv_price);
		tv_normal = (TextView) inflate.findViewById(R.id.tv_normal);
		tv_weight = (TextView) inflate.findViewById(R.id.tv_weight);
		tv_normal.setEnabled(true);
		tv_price.setEnabled(false);
		tv_weight.setEnabled(false);

		ofNormal.setOnClickListener(this);
		ofWeight.setOnClickListener(this);
		ofPrice.setOnClickListener(this);

		// search.setIconifiedByDefault(false);
		lv.setGroupIndicator(null);
		// search.setQueryHint("锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷");
		// search.setOnQueryTextListener(this);
		lv.setOnScrollListener(this);
		lv.setEmptyView(empty);

		inputService = (InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);// 获取输入管理器

		refresh.setColorSchemeColors(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		refresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO
				if (et.equals("请输入查询内容")) {
					refresh.setRefreshing(false);
					Toast.makeText(mActivity, "未输入查询内容，无法进行数据刷新", 0).show();
				} else {
					CurPage = 1;
					getData();
					if (isLoaded) {
						refresh.setRefreshing(false);
						isLoaded = false;
					}
				}
			}
		});
	}

	public void getQueryContent(EditText et) {

		String content = et.getText().toString();
		Keys = content;
		if (Keys != null) {
			Config.SEARCH_KEYS = Keys;
		}
		adapter = null;
		group.clear();// 清空
		childList.clear();
		getData();

	}

	public void getData() {

		if (NetworkUtil.isNetworkAvailable(mActivity)) {
			String urlPath = URL_START + CurPage + URL_END + bypar + ORBY
					+ orby + KEYS + Config.SEARCH_KEYS;

			HttpUtils utils = new HttpUtils();
			utils.send(HttpMethod.GET, urlPath, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {

					String json = arg0.result;
					try {

						JSONArray array = new JSONArray(json);
						JSONObject object;
						GroupBean groupBean;
						ChildBean childBean;

						for (int i = 0; i < array.length(); i++) {

							List<ChildBean> clist = new ArrayList<ChildBean>();
							object = array.getJSONObject(i);
							groupBean = new GroupBean();
							childBean = new ChildBean();

							String chanPin = object.getString("chanPin");
							String caiZhi = object.getString("caiZhi");
							String zhongLiang = object.getString("zhongLiang");
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
							String kunBaoHao = object.getString("kunBaoHao");
							String jiZhongFangShi = object
									.getString("jiZhongFangShi");
							String wuLiaoHao = object.getString("wuLiaoHao");
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
						if (Config.SEARCH_KEYS != null) {

							if (adapter == null) {

								adapter = new MangerAdapter(mActivity, group,
										childList);
								lv.setAdapter(adapter);
							} else {
								adapter.setData(group, childList);
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
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ofweight:// 重量排序

			bypar = "weight";
			setButtonPressed(1);
			sortRefresh();

			break;
		case R.id.ofprice:// 单价排序

			bypar = "price";
			setButtonPressed(2);
			sortRefresh();

			break;
		case R.id.offormal:// 默认排序

			bypar = "null";
			setButtonPressed(3);
			sortRefresh();

			break;
		case R.id.iv_back:// 回退

			getFragmentManager().popBackStackImmediate();

			break;
		case R.id.iv_query:// 查询

			hideKeyboard();
			getQueryContent(et);

			break;
		default:

			break;
		}
	}

	public void setButtonPressed(int position) {

		switch (position) {
		case 1:

			icon_weight.setBackgroundResource(R.drawable.sort_on);
			icon_normal.setBackgroundResource(R.drawable.sort_off);
			icon_danjia.setBackgroundResource(R.drawable.sort_off);

			tv_weight.setEnabled(true);
			tv_normal.setEnabled(false);
			tv_price.setEnabled(false);
			break;
		case 2:

			icon_weight.setBackgroundResource(R.drawable.sort_off);
			icon_danjia.setBackgroundResource(R.drawable.sort_on);
			icon_normal.setBackgroundResource(R.drawable.sort_off);

			tv_normal.setEnabled(false);
			tv_weight.setEnabled(false);
			tv_price.setEnabled(true);
			break;

		case 3:

			icon_weight.setBackgroundResource(R.drawable.sort_off);
			icon_normal.setBackgroundResource(R.drawable.sort_on);
			icon_danjia.setBackgroundResource(R.drawable.sort_off);

			tv_normal.setEnabled(true);
			tv_weight.setEnabled(false);
			tv_price.setEnabled(false);
			break;
		}

	}

	/**
	 * @pama 当检测到页面有虚拟键盘显示时关闭隐藏键盘
	 */
	private void hideKeyboard() {
		if (inputService.isActive()) {
			inputService.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void sortRefresh() {

		if (adapter == null) {
			Toast.makeText(getActivity(), "请输入查询内容 ", 0).show();
		} else {

			group.clear();
			childList.clear();
			getData();
		}
	}

	@Override
	public void onLoginListener() {

		LoginUtils.onLogin(getActivity(), false, 0);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			if (isBottom) {
				CurPage++;
				try {
					Thread.sleep(500);
					getData();
					// foot.setVisibility(View.GONE);
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
		if (firstVisibleItem + visibleItemCount == totalItemCount)
			isBottom = true;
	}

	@Override
	public void onResume() {

		super.onResume();
		getData();
	}

	@Override
	public void onStop() {
		// TODO
		super.onStop();
		hideKeyboard();
	}

	@Override
	public void onPause() {
		// TODO
		super.onPause();
		hideKeyboard();
	}

	@Override
	public void onDestroy() {
		// TODO
		super.onDestroy();
		Config.SEARCH_KEYS = null;
	}
}
