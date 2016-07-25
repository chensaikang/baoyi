package com.by.home.fragment;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.by.javabean.DataBean;
import com.example.baoyisteel.R;

public class BarAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<DataBean> beans;

	public BarAdapter(List<DataBean> list) {
		beans = list;
	}

	public void setData(List<DataBean> list) {

		beans.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return beans.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO

		return beans.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			Context context = parent.getContext();
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.bar_layout, null);
			holder.content = (TextView) convertView
					.findViewById(R.id.bar_content);
			holder.time = (TextView) convertView.findViewById(R.id.bar_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 填充数据
		DataBean bean = beans.get(position);
		String bar_content = bean.getContent();
		String bar_time = bean.getTime();
		holder.content.setText(bar_content);
		holder.time.setText(bar_time);

		return convertView;
	}

	class ViewHolder {
		TextView content;
		TextView time;
	}
}
