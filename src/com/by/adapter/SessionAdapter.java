package com.by.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.by.javabean.SessionBean;
import com.example.baoyisteel.R;

public class SessionAdapter extends BaseAdapter {

	private List<SessionBean> beans;

	public void setData(Context c, List<SessionBean> list) {

		if (beans == null) {
			beans = list;
		} else {
			beans.addAll(list);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return beans.size() != 0 ? beans.size() : 0;
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

		Context context = parent.getContext();
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.session_item, null);
			holder.name = (TextView) convertView
					.findViewById(R.id.session_name);
			holder.baozhengjin = (TextView) convertView
					.findViewById(R.id.baozhengjin);
			holder.start = (TextView) convertView.findViewById(R.id.time_start);
			holder.end = (TextView) convertView.findViewById(R.id.time_end);
			holder.state = (TextView) convertView
					.findViewById(R.id.session_state);
			holder.time = (TextView) convertView
					.findViewById(R.id.session_time);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 数据填充
		SessionBean bean = beans.get(position);
		String string = bean.getMingCheng();

		// 截取场次名称字符串中包含的场次时间
		int start = string.indexOf("（");
		int end = string.indexOf("）");
		String replace = null;
		if (start != -1 && end != -1) {
			String sub = string.substring(start, end + 1);
			replace = string.replace(sub, "");

			holder.time.setText(string.substring(start, end + 1));
			holder.time.setVisibility(View.VISIBLE);
		} else {
			replace = bean.getMingCheng();
			holder.time.setVisibility(View.GONE);
		}

		holder.name.setText(replace);
		holder.name.setText(replace);
		holder.baozhengjin.setText("保证金:" + bean.getBaoZhengJin());
		holder.state.setText(bean.getZhaungTai() + "");

		holder.start.setText(formatDate(bean.getJinJiaQi()).toString());
		holder.end.setText(formatDate(bean.getJinJiaZhi()).toString());

		return convertView;
	}

	class ViewHolder {

		private TextView name;
		private TextView baozhengjin;
		private TextView start;
		private TextView end;
		private TextView state;
		private TextView time;
	}

	// 时间戳的转换----截取字符串
	public String formatDate(String str) {
		String timestamp = str.substring(6, 19);
		Date date = new Date(Long.parseLong(timestamp));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  hh:mm");
		return format.format(date);
	}
}
