package com.by.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.by.interf.OnBidingDataRefreshListener;
import com.by.javabean.BidingJoinBean;
import com.by.utils.Config;
import com.example.baoyisteel.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class BuyerJoinBidingAdapter extends BaseAdapter {

	private List<BidingJoinBean> beans;
	private Context context;
	private BidingJoinBean bean;
	private OnBidingDataRefreshListener listener;

	public BuyerJoinBidingAdapter(List<BidingJoinBean> list, Fragment f) {
		super();
		beans = list;
		listener = (OnBidingDataRefreshListener) f;

	}

	public void setData(List<BidingJoinBean> list) {
		beans.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return beans.size() <= 0 ? 0 : beans.size();
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
		if (convertView == null) {
			holder = new ViewHolder();
			context = parent.getContext();
			LayoutInflater inflate = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflate.inflate(R.layout.steelbid_item, null);
			holder.pinpainame = (TextView) convertView
					.findViewById(R.id.pinpainame);
			holder.weight = (TextView) convertView.findViewById(R.id.weight);
			holder.endtime = (TextView) convertView.findViewById(R.id.endtime);
			// holder.tidu = (TextView) convertView.findViewById(R.id.tidu);
			// holder.qipaijia = (TextView) convertView
			// .findViewById(R.id.qipaijia);
			// holder.dangqianjia = (TextView) convertView
			// .findViewById(R.id.dangqianjia);
			holder.ruweistate = (TextView) convertView
					.findViewById(R.id.ruweistate);
			holder.bidstate = (TextView) convertView
					.findViewById(R.id.bidstate);
			holder.bid = (TextView) convertView.findViewById(R.id.bid);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		bean = beans.get(position);
		holder.pinpainame.setText(bean.getPingpanname());
		holder.weight.setText(bean.getZhongliang());
		// holder.tidu.setText("¥" + bean.getTidu());
		// holder.qipaijia.setText("¥" + bean.getQipaijia());
		// holder.dangqianjia.setText("¥" + bean.getDangqianjia());
		holder.ruweistate.setText(bean.getRuweizhuangtai());
		holder.bidstate.setText(bean.getJingjiazhuangtai());

		@SuppressWarnings("unused")
		String date = bean.getJieshushijian();
		holder.endtime.setText(formatDate(bean.getJieshushijian()));
		// 竞拍加价
		holder.bid.setOnClickListener(new OnClickListener() {

			private Dialog dialog;
			private double p;

			@Override
			public void onClick(View v) {

				listener.onBidingDataRefreshListener(false);

				dialog = new Dialog(context,
						android.R.style.Theme_Translucent_NoTitleBar);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.bid_dialog);
				TextView name = (TextView) dialog.findViewById(R.id.pinpainame);

				TextView qipaijia = (TextView) dialog
						.findViewById(R.id.qipaijia);// 起拍价
				qipaijia.setText("¥" + bean.getQipaijia());
				TextView tidu = (TextView) dialog.findViewById(R.id.tidu);// 梯度
				tidu.setText("¥" + bean.getTidu());
				TextView dangqianjia = (TextView) dialog
						.findViewById(R.id.dangqianjia);
				dangqianjia.setText("¥" + bean.getDangqianjia());
				// TextView jingpaijia = (TextView) dialog
				// .findViewById(R.id.jingpaijia);
				final TextView price = (TextView) dialog
						.findViewById(R.id.jingpaijia);
				Button add = (Button) dialog.findViewById(R.id.add);// 加价
				Button subtract = (Button) dialog.findViewById(R.id.subtract);// 减价
				Button confirm = (Button) dialog.findViewById(R.id.sure);// 加价
				Button cancel = (Button) dialog.findViewById(R.id.cancel);// 减价
				name.setText(bean.getPingpanname());
				price.setText("¥" + bean.getDangqianjia());
				p = Double.valueOf(bean.getDangqianjia());
				// 加价
				add.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						double Price = p + Double.valueOf(bean.getTidu());
						price.setText("¥" + String.valueOf(Price));
						p = Price;

					}
				});
				// 减价
				subtract.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						double Price = p - Double.valueOf(bean.getTidu());
						price.setText("¥" + String.valueOf(Price));
						p = Price;
					}
				});
				cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						listener.onBidingDataRefreshListener(true);
					}
				});
				confirm.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 竞价接口
						HttpUtils utils = new HttpUtils();
						String openId = context.getSharedPreferences(
								"LoginInfo", 0).getString("openId", null);
						RequestParams params = new RequestParams();
						params.addBodyParameter("OpenId", openId);
						params.addBodyParameter("Chumoney", String.valueOf(p));
						params.addBodyParameter("ppid", bean.getPpId());
						utils.send(HttpMethod.POST, Config.URL_PATH
								+ "buyer.asmx/Chujia", params,
								new RequestCallBack<String>() {

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {

										ToastShow(arg0.toString());

									}

									// 0未开始；1成功；-1失败；3竞价结束
									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {
										String json = arg0.result;
										if ("0".equals(json)) {
											ToastShow("竞价还未开始，请耐心等待");
										}
										if ("-1".equals(json)) {
											ToastShow("竞价失败,请重新竞价");
										}
										if ("1".equals(json)) {
											ToastShow("竞价成功,等待竞拍结果");
										}
										if ("3".equals(json)) {
											ToastShow("竞价已结束，谢谢关注");
										}
									}
								});
						dialog.dismiss();
						listener.onBidingDataRefreshListener(true);
					}
				});
				dialog.show();
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView pinpainame;
		TextView weight;
		TextView endtime;
		TextView tidu;
		TextView qipaijia;
		TextView dangqianjia;
		TextView ruweistate;
		TextView bidstate;
		TextView bid;
	}

	// 时间戳的转换----截取字符串
	public String formatDate(String str) {
		if (str != null) {

			String timestamp = str.substring(6, 19);
			Date date = new Date(Long.parseLong(timestamp));
			SimpleDateFormat format = new SimpleDateFormat("hh:mm");
			return format.format(date);
		} else
			return null;
	}

	public void ToastShow(String str) {

		Toast.makeText(context, str, 0).show();
	}
}
