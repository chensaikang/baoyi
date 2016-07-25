package com.by.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.by.javabean.Seller_OrderBean;
import com.example.baoyisteel.R;

public class SellerIndentAdapter extends BaseAdapter {

	private List<Seller_OrderBean> infos;
	private LayoutInflater inflater;

	public SellerIndentAdapter(List<Seller_OrderBean> list, Context c) {
		super();
		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.infos = list;
		notifyDataSetChanged();
	}

	public void setData(List<Seller_OrderBean> list) {
		infos.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return infos.size() != 0 ? infos.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.intent_item, null);
			holder.DD = (TextView) convertView.findViewById(R.id.order_num);
			holder.WEIGHT = (TextView) convertView
					.findViewById(R.id.order_weight);
			holder.STATE = (TextView) convertView
					.findViewById(R.id.order_state);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Seller_OrderBean bean = infos.get(position);
		holder.DD.setText(bean.getDingDanHao());
		holder.WEIGHT.setText(bean.getZhongLiang() + "");
		holder.STATE.setText(bean.getZhuangTai());

		return convertView;
	}

	class ViewHolder {
		TextView DD;
		TextView WEIGHT;
		TextView STATE;
	}
}
