package com.by.adapter;

import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.by.fragment_levelexpand.SteelBidding;
import com.by.javabean.AuctionBean;
import com.example.baoyisteel.R;

public class AuctionAdapter extends BaseAdapter {

	private List<AuctionBean> beans;
	private Activity activity;

	public AuctionAdapter(List<AuctionBean> list, Activity a) {
		super();
		beans = list;
		activity = a;
	}

	public void setData(List<AuctionBean> list) {
		beans.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return beans.size();
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

			convertView = activity.getLayoutInflater().inflate(
					R.layout.auction_item, null);

			holder.pinpanName = (TextView) convertView
					.findViewById(R.id.pinpainame);
			holder.gongsiname = (TextView) convertView
					.findViewById(R.id.seller);
			holder.baozhengjin = (TextView) convertView
					.findViewById(R.id.baozhengjin);
			holder.joinBid = (TextView) convertView.findViewById(R.id.join);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final AuctionBean bean = beans.get(position);

		holder.pinpanName.setText(bean.getMingCheng());
		holder.gongsiname.setText(bean.getGongsiname());
		holder.baozhengjin.setText("¥" + bean.getBaoZhengJin());

		holder.joinBid.setOnClickListener(new OnClickListener() {
			// 进入拍卖场次

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				String ccId = bean.getCcId();
				// SharedPreferences sp = activity.getSharedPreferences(
				// "LoginInfo", 0);
				// String openId = sp.getString("openId", null);
				//
				// HttpUtils utils=new HttpUtils();
				// RequestParams params=new RequestParams();
				// params.addBodyParameter("", value);
				SteelBidding fragment = new SteelBidding();
				FragmentManager manager = activity.getFragmentManager();
				Bundle bundle = new Bundle();
				bundle.putString("ccid", ccId);
				fragment.setArguments(bundle);
				manager.beginTransaction().replace(R.id.fragment, fragment)
						.addToBackStack(null).commit();

			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView pinpanName;
		TextView baozhengjin;
		TextView gongsiname;
		TextView joinBid;

	}
}
