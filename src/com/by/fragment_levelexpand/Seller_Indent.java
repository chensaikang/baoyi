package com.by.fragment_levelexpand;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.by.adapter.SellerIndentAdapter;
import com.by.detail.OrderDetail;
import com.by.javabean.Seller_OrderBean;
import com.by.utils.Config;
import com.by.utils.LoginUtils;
import com.by.utils.NetworkUtil;
import com.example.baoyisteel.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Seller_Indent extends Fragment implements OnClickListener,
		OnScrollListener, OnItemClickListener {

	private ListView lv;
	private EditText from;
	private EditText to;
	private Button query;
	private PopupWindow popup;
	private CalendarView calendar;
	private SharedPreferences sp;
	private String openId;
	private HttpUtils utils;
	private int CurPage = 1;
	private boolean isBottom = false;
	private List<Seller_OrderBean> list;
	private List<Seller_OrderBean> cList;
	private Date fromDate;
	private Date toDate;
	private boolean isLoaded = false;
	// private String URL_t = Config.PATH_TEST + "buyer.asmx/MyOrder?pageNo=";
	private String URL_BETWEEN = "/MyOrder?pageNo=";
	private String OPENID = "&OpenId=";
	private String CALENDAR1 = "&Calendar1=";
	private String CALENDAR2 = "&Calendar2=";
	private SellerIndentAdapter adapter;
	private String urlPath;
	private LinearLayout pop;
	private TextView empty;
	private SwipeRefreshLayout layout;
	private TextView foot;
	private View inflate;
	private FragmentManager manager;
	private Activity mActivity;

	// private boolean isQuery = false;

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		mActivity = activity;
		Config.ITEM_POSITION = 41;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		inflate = inflater.inflate(R.layout.indent, null);

		initView();

		return inflate;
	}

	@Override
	public void onResume() {
		super.onResume();
		adapter = null;
		list = new ArrayList<Seller_OrderBean>();
		getDataFromNet();

	}

	private void getDataFromNet() {

		if (NetworkUtil.isNetworkAvailable(mActivity)) {

			sp = getActivity().getSharedPreferences("LoginInfo", 0);
			openId = sp.getString("openId", "");

			Config.OPENID = openId;

			utils = new HttpUtils();
			urlPath = Config.URL_PATH + Config.INDENT_TITLE + URL_BETWEEN
					+ CurPage + OPENID + openId + CALENDAR1
					+ from.getText().toString() + CALENDAR2
					+ to.getText().toString();
			Log.e("TAG", "INTENT_URL==" + urlPath);
			utils.send(HttpMethod.GET, urlPath, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {

					String json = arg0.result;
					Log.e("TAG", "JSON_INTENT==" + json);

					if ("-1".equals(json)) {
						reLogin();
					} else if ("-2".equals(json)) {
						Toast.makeText(mActivity, "亲，您未开通次服务，请开通。。。", 0).show();
						empty.setText("亲，您未开通次服务，请开通。。。");

					} else {
						try {
							jsonParse(json);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (adapter == null) {
							adapter = new SellerIndentAdapter(cList, mActivity);
							lv.setAdapter(adapter);
							Log.e("TAG", "setAdapter====" + cList.size());
						} else {
							adapter.setData(cList);
							Log.e("TAG", "setData");
						}
						isLoaded = true;
					}
				}
			});
		}
	}

	// 视图初始化
	private void initView() {

		manager = getFragmentManager();

		lv = (ListView) inflate.findViewById(R.id.indent_lv);
		from = (EditText) inflate.findViewById(R.id.from);
		empty = (TextView) inflate.findViewById(R.id.empty);
		to = (EditText) inflate.findViewById(R.id.to);

		from.setSelected(true);
		lv.setEmptyView(empty);
		inflate.findViewById(R.id.iv_navigation).setOnClickListener(this);// 导航

		from.setMarqueeRepeatLimit(4);// 跑马灯
		to.setMarqueeRepeatLimit(4);

		lv.setEmptyView(empty);
		layout = (SwipeRefreshLayout) inflate
				.findViewById(R.id.swipeRefreshLayout);

		to.setInputType(InputType.TYPE_NULL);// 屏蔽虚拟键盘
		from.setInputType(InputType.TYPE_NULL);

		query = (Button) inflate.findViewById(R.id.chaxu);
		pop = (LinearLayout) inflate.findViewById(R.id.ll_pop);
		// 示例话日历控件
		calendar = new CalendarView(mActivity);
		calendar.setShowWeekNumber(false);
		long date = calendar.getDate();
		// Log.e("MES", "current" + date);
		calendar.setBackgroundResource(R.color.gray);
		from.setOnClickListener(this);
		to.setOnClickListener(this);
		query.setOnClickListener(this);

		// 监听设置
		lv.setOnScrollListener(this);
		lv.setOnItemClickListener(this);
		// 刷新控件
		layout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		layout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// 重置
				CurPage = 1;
				adapter = null;
				from.setText("");
				to.setText("");
				list.clear();
				getDataFromNet();
				if (isLoaded) {
					layout.setRefreshing(false);
					isLoaded = false;
				}
			}

		});
	}

	private void reLogin() {

		LoginUtils.onLogin(mActivity, true, 0);
	}

	// json解析
	public void jsonParse(String json) throws JSONException {

		JSONArray array = new JSONArray(json);
		cList = new ArrayList<Seller_OrderBean>();

		// if (array.length() == 0 && isQuery) {
		if (array.length() == 0) {
			Toast.makeText(getActivity(), "无订单信息", 0).show();
			empty.setText("亲，您最近暂无订单信息。。。");
			list.clear();
			adapter = null;
		} else {
			cList = JSON.parseArray(json, Seller_OrderBean.class);
			// for (int i = 0; i < array.length(); i++) {
			//
			// Seller_OrderBean bean = new Seller_OrderBean();
			// JSONObject object = array.getJSONObject(i);
			// // bean.setOid(object.getInt("oid"));
			// // bean.setDaiYun(object.getBoolean("isDaiYun"));
			// bean.setDingDanHao(object.getString("dingDanHao"));
			// bean.setGongSiMing(object.getString("gongSiMing"));
			// bean.setZongJia(object.getDouble("zongJia"));
			// bean.setZhuangTai(object.getString("zhuangTai"));
			// bean.setZhongLiang(object.getDouble("zhongLiang"));
			// cList.add(bean);
			//
			// }
			list.addAll(cList);
		}
		// isQuery = false;
		Log.e("TAG", "INDENT----");
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.from:
			showPopwindow(pop);
			setData(from);
			break;
		case R.id.to:
			if (!from.getText().toString().isEmpty()) {
				showPopwindow(pop);
				setData(to);
			} else {
				Toast.makeText(getActivity(), "请输入查询日期", 0).show();
			}
			break;
		case R.id.chaxu:
			// isQuery = true;
			adapter = null;
			list.clear();// 清空数据
			getDataFromNet();
			// if (fromDate == null || toDate == null) {
			// getDataFromNet();
			// } else if (comparDate(fromDate, toDate)) {
			// getDataFromNet();
			// } else {
			// Toast.makeText(getActivity(), "起始日期需小于截止日期，请重新选择", 0).show();
			// }

			break;

		case R.id.iv_navigation:
			manager.popBackStackImmediate();
			break;
		}

	}

	private void showPopwindow(View v) {

		popup = new PopupWindow(calendar, 700, 700);

		popup.setFocusable(true);
		// 外部可点击
		popup.setOutsideTouchable(true);

		// ���ñ���ͼ
		// Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.listview_background);
		// popup.setBackgroundDrawable(background);
		// popup.showAsDropDown(v, 5, -2);
		popup.showAtLocation(v, Gravity.BOTTOM, 0, 0);

	}

	public void setData(final EditText v) {

		calendar.setOnDateChangeListener(new OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year,
					int month, int dayOfMonth) {
				// TODO Auto-generated method stub
				String date = year + "/" + month + "/" + dayOfMonth;
				int id = v.getId();
				switch (id) {
				case R.id.from:
					fromDate = new Date(year, month, dayOfMonth);
					v.setText(date);
					popup.dismiss();
					break;
				case R.id.to:
					toDate = new Date(year, month, dayOfMonth);
					boolean isOk = comparDate(fromDate, toDate);
					if (isOk) {
						v.setText(date);
					} else {
						Toast.makeText(getActivity(), "选择日期不符，需不下于起始日期", 0)
								.show();
					}
					popup.dismiss();
					break;
				}

			}
		});

	}

	// 日期比较
	public boolean comparDate(Date d1, Date d2) {

		if (d2 == null || d2 == d1)
			return true;
		else
			return d2.after(d1);

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		switch (scrollState) {
		case SCROLL_STATE_FLING:

			break;
		case SCROLL_STATE_IDLE:

			if (isBottom == true) {
				CurPage++;
				try {
					Thread.sleep(500);
					getDataFromNet();
					// foot.setVisibility(View.GONE);
					isBottom = false;
				} catch (InterruptedException e) {

					e.printStackTrace();
				}

			}
			break;
		case SCROLL_STATE_TOUCH_SCROLL:

			break;

		default:
			break;
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount == totalItemCount) {
			isBottom = true;
			// foot.setVisibility(View.VISIBLE);
		}

	}

	// item点击事件响应 待开发
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Seller_OrderBean bean = list.get(position);
		int oid = bean.getOid();
		Config.ORDERTAIL_OID = oid;

		FragmentTransaction transaction = manager.beginTransaction();
		OrderDetail fragmentDetail = new OrderDetail();
		// Bundle args = new Bundle();
		// args.putInt("oid", oid);
		// args.putString("openId", openId);
		// args.putString("title", "seller");// 区分买家和卖家
		// fragmentDetail.setArguments(args);
		transaction.replace(R.id.fragment, fragmentDetail);
		transaction.addToBackStack(null);
		transaction.commit();

	}

	@Override
	public void onPause() {
		// TODO
		super.onPause();
		if (popup != null && popup.isShowing()) {

			popup.dismiss();
		}
	}

	public void onReturn(View v) {
		int id = v.getId();
		if (id == R.id.iv_navigation) {
			manager.popBackStack();
		}
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}
}
