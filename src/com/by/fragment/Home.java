package com.by.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.by.activity.LoginActivity;
import com.by.home.fragment.NewsFragment;
import com.by.utils.Config;
import com.by.utils.NetworkUtil;
import com.example.baoyisteel.R;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Home extends Fragment implements OnClickListener {

	private ViewPager iv_vp;
	// private String diskCachePath = Environment.getExternalStorageDirectory()
	// .getAbsolutePath() + File.separator + File.separator + "pictures";// +
	// "com.example.viewPagerDemo"
	private int currPage = 0;
	private FragmentManager manager;
	@SuppressWarnings("unused")
	private List<String> urlList;
	private List<ImageView> ivList;
	private SharedPreferences sharedPreferences;
	@SuppressWarnings("unused")
	private Editor edit;
	@SuppressWarnings("unused")
	private ImageView iv;
	@SuppressWarnings("unused")
	private long exitTime = 0;
	private TextView bid;
	private TextView sell;
	private TextView inner;
	private List<String> imgRes;
	private ImageView empty;
	public static final long TWO_SECOND = 2 * 1000;
	@SuppressWarnings("unused")
	private WindowManager windowManager;
	@SuppressWarnings("unused")
	private int width;
	@SuppressWarnings("unused")
	private int height;
	private View inflate;
	@SuppressWarnings("unused")
	private int heig;
	@SuppressWarnings("unused")
	private ListView lv;
	@SuppressWarnings("unused")
	private SwipeRefreshLayout refresh;
	private Activity mActivity;

	@Override
	public void onAttach(Activity activity) {
		// TODO
		super.onAttach(activity);
		mActivity = activity;
		Config.ITEM_POSITION = 0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		inflate = inflater.inflate(R.layout.home, null);

		inItView();
		// 可进行UI操作的可延迟的线程
		// 实例化
		ScheduledExecutorService scheduled = Executors
				.newSingleThreadScheduledExecutor();
		// runnable 对象 执行操作
		ViewPagerTask pagerTask = new ViewPagerTask();
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
		scheduled.scheduleAtFixedRate(pagerTask, 2, 2, TimeUnit.SECONDS);

		return inflate;
	}

	private void inItView() {

		// 获取sp
		sharedPreferences = getActivity().getSharedPreferences("LoginInfo", 0);
		// 初始化碎片
		manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		NewsFragment fragment = new NewsFragment();
		Bundle bundle = new Bundle();
		bundle.putString("url", Config.URL_PATH + Config.Home_MNotice);
		fragment.setArguments(bundle);
		transaction.add(R.id.home_fragment, fragment);
		transaction.addToBackStack(null);// 加入回退栈
		transaction.commit();

		// viewPager
		// 图片资源获取
		getImageFromNet();
		iv_vp = (ViewPager) inflate.findViewById(R.id.vp);
		empty = (ImageView) inflate.findViewById(R.id.empty);
		VpListener listener = new VpListener();

		// // 屏幕适配
		// windowManager = (WindowManager) getActivity().getSystemService(
		// Context.WINDOW_SERVICE);
		// width = windowManager.getDefaultDisplay().getWidth();// 锟斤拷幕锟斤拷锟�
		// height = windowManager.getDefaultDisplay().getHeight();// 锟斤拷幕锟竭讹拷
		// heig = width / 5 * 3;// 图片锟侥高讹拷锟斤拷锟斤拷
		// Log.e("TAG", "WID=" + width + "HEI=" + height + "sss=" + heig);

		iv_vp.setOnPageChangeListener(listener);
		bid = (TextView) inflate.findViewById(R.id.bid_bar);
		sell = (TextView) inflate.findViewById(R.id.sell_bar);
		inner = (TextView) inflate.findViewById(R.id.in_bar);
		bid.setOnClickListener(this);
		sell.setOnClickListener(this);
		inner.setOnClickListener(this);
		// sell.setClickable(true);

		ImageView login = (ImageView) inflate.findViewById(R.id.home_login);
		// 添加监听事件
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String str = sharedPreferences.getString("name", "");
				if (str != "") {
					Toast.makeText(getActivity(), "已登录", 0).show();
				} else {
					Intent intent = new Intent(getActivity(),
							LoginActivity.class);
					startActivity(intent);
				}

			}
		});
	}

	// 碎片切换的监听
	@Override
	public void onClick(View v) {

		NewsFragment fragment = new NewsFragment();
		FragmentTransaction transaction = manager.beginTransaction();
		Bundle bundle = new Bundle();
		int id = v.getId();

		switch (id) {
		// 碎片切换
		case R.id.sell_bar:

			sell.setBackgroundResource(R.drawable.kuang);
			bid.setBackgroundResource(R.color.backcolor);
			inner.setBackgroundResource(R.color.backcolor);
			bundle.putString("url", Config.URL_PATH + Config.Home_MNotice);
			break;
		case R.id.bid_bar:
			bid.setBackgroundResource(R.drawable.kuang);
			inner.setBackgroundResource(R.color.backcolor);
			sell.setBackgroundResource(R.color.backcolor);
			bundle.putString("url", Config.URL_PATH + Config.Home_BNotice);
			break;
		case R.id.in_bar:
			inner.setBackgroundResource(R.drawable.kuang);
			bid.setBackgroundResource(R.color.backcolor);
			sell.setBackgroundResource(R.color.backcolor);
			bundle.putString("url", Config.URL_PATH + Config.Home_ZNotice);
			break;

		}
		fragment.setArguments(bundle);
		transaction.replace(R.id.home_fragment, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	/****************************** 轮播 ***************************************/
	private class ViewPagerTask implements Runnable {
		@Override
		public void run() {
			// 切换图片位置 可循环
			currPage = (currPage + 1) % ivList.size();
			// 通知UI变化
			handler.sendEmptyMessage(0);
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 切换
			iv_vp.setCurrentItem(currPage);

		};
	};

	/********************************* vp的适配器 ***************************************/
	class VpAdapter extends PagerAdapter {

		@Override
		public int getCount() {

			return ivList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {

			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			container.removeView(ivList.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			container.addView(ivList.get(position));

			return ivList.get(position);
		}
	}

	class VpListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			currPage = position;
		}
	}

	private void getImageFromNet() {

		HttpUtils utils = new HttpUtils();
		String url = Config.URL_PATH + "Home.asmx/Photo";
		utils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				String json = arg0.result;
				try {
					JSONArray array = new JSONArray(json);
					imgRes = new ArrayList<String>();
					ivList = new ArrayList<ImageView>();
					for (int i = 0; i < array.length(); i++) {
						String imgurl = array.getString(i);
						imgRes.add(imgurl);
					}
					getImage();
					if (ivList.size() == imgRes.size() && ivList.size() != 0) {
						VpAdapter adapter = new VpAdapter();
						empty.setVisibility(View.INVISIBLE);
						iv_vp.setAdapter(adapter);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	// 图片
	public void getImage() {

		// BitmapUtils utils = new BitmapUtils(getActivity(), diskCachePath);
		BitmapUtils utils = new BitmapUtils(mActivity);

		for (int i = 0; i < imgRes.size(); i++) {

			ImageView iv = new ImageView(mActivity);
			// android.view.ViewGroup.LayoutParams params01 = new LayoutParams(
			// width, heig);
			// LayoutParams params = new LayoutParams(width, heig);
			// iv.setLayoutParams(params);
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			ivList.add(iv);
			// BitmapDisplayConfig config = new BitmapDisplayConfig();
			// config.setLoadingDrawable(getResources().getDrawable(
			// R.drawable.loading));
			// config.setLoadFailedDrawable(getResources().getDrawable(
			// R.drawable.ic_launcher));
			String url = "http://" + imgRes.get(i);
			if (NetworkUtil.isNetworkConnect(mActivity)) {

				utils.display(iv, url);
			}

		}
	}
}
