package com.by.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.by.javabean.BiddingBean;
import com.by.utils.Config;
import com.by.utils.LoginUtils;
import com.by.utils.NetworkUtil;
import com.example.baoyisteel.BaseActivity;
import com.example.baoyisteel.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class BidingAdpter extends BaseAdapter {
	private List<BiddingBean> beans;
	// private FragmentManager manager;
	private BaseActivity activity;

	public BidingAdpter(List<BiddingBean> beas, Activity a) {
		super();
		beans = beas;
		// manager = fragmentManager;
		activity = (BaseActivity) a;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return beans.size() > 0 ? beans.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return beans.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		LayoutInflater inflate = (LayoutInflater) parent.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflate.inflate(R.layout.biding_item, null);
			holder.bid_name = (TextView) convertView
					.findViewById(R.id.bid_name);
			holder.bid_time = (TextView) convertView
					.findViewById(R.id.bid_time);
			holder.bid_style = (TextView) convertView
					.findViewById(R.id.bid_style);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.baozhengjin = (TextView) convertView
					.findViewById(R.id.baozhengjin);
			holder.session_state = (Button) convertView
					.findViewById(R.id.session_state);
			holder.time_start = (TextView) convertView
					.findViewById(R.id.time_start);
			holder.time_end = (TextView) convertView
					.findViewById(R.id.time_end);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final BiddingBean bean = beans.get(position);
		holder.bid_name.setText(bean.getChangci_MingCheng());// 场次名称
		holder.bid_time.setText(formatDate2(bean.getJingmaiDate()));// 上架时间
		holder.bid_style.setText("竞价方式: " + bean.getJinjiafangshi());
		// holder.name.setText("公司名称: " + bean.getGongsiname());

		holder.time_start.setText(formatDate(bean.getKaishiDate()));
		holder.time_end.setText(formatDate(bean.getJieshuDate()));
		holder.baozhengjin.setText("保证金: " + bean.getBaozhengjin());

		if (!"结束".equals(bean.getZhuangtai())) {
			holder.session_state.setText("去竞拍");
		}
		holder.session_state.setOnClickListener(new OnClickListener() {// 跳转到我的竞价

					@Override
					public void onClick(View v) {// 拿到是否可进入买家中心竞价的权限

						HttpUtils utils = new HttpUtils();
						RequestParams params = new RequestParams();
						SharedPreferences sp = activity.getSharedPreferences(
								"LoginInfo", 0);
						String openId = sp.getString("openId", null);
						params.addBodyParameter("OpenId", openId);
						params.addBodyParameter("ccid",
								String.valueOf(bean.getChangci_ID()));
						String url = Config.URL_PATH + "home.asmx/JoinAuction";
						utils.send(HttpMethod.POST, url, params,
								new RequestCallBack<String>() {

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										NetworkUtil.isNetworkConnect(activity);

									}

									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {
										String json = arg0.result;

										if ("1".equals(json)) {// 进入竞价

											Config.ITEM_POSITION = 331;
											activity.setFragmentItem();
										}
										if ("2".equals(json)) {// 已经参加
											Toast.makeText(activity, "该竞拍已参加",
													0).show();
											Config.ITEM_POSITION = 331;
											activity.setFragmentItem();
										}
										if ("3".equals(json)) {// 未开始
											Toast.makeText(activity, "该竞拍还未开始",
													0).show();
										}
										if ("0".equals(json)) {// 失败

											Toast.makeText(activity, "参加竞拍失败",
													0).show();
										}
										if ("-1".equals(json)) {// 未登录

											LoginUtils.onLogin(activity, false,
													Config.FRAGMENT_TYPE);
										}
									}
								});

					}
				});
		return convertView;
	}

	class ViewHolder {

		TextView bid_name;// 竞价场次名
		TextView bid_time;// 竞价时间
		TextView bid_style;// 竞价方式
		TextView name;// 公司名
		TextView baozhengjin;// 保证金
		Button session_state;// 场次状态
		TextView time_start;// 开始时间
		TextView time_end;// 结束时间

	}

	// 时间戳的转换----截取字符串
	public String formatDate(String str) {
		String timestamp = str.substring(6, 19);
		Date date = new Date(Long.parseLong(timestamp));
		// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  hh:mm");
		SimpleDateFormat format = new SimpleDateFormat("hh:mm");
		return format.format(date);
	}

	// 时间戳的转换----截取字符串
	public String formatDate2(String str) {
		String timestamp = str.substring(6, 19);
		Date date = new Date(Long.parseLong(timestamp));
		SimpleDateFormat format = new SimpleDateFormat("hh:mm");
		return "(" + format.format(date) + ")";
	}
}
