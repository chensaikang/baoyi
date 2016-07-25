package com.by.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.by.javabean.OrderDtailItemBean;
import com.example.baoyisteel.R;

public class OrderDetailAdapter extends BaseAdapter {

	private List<OrderDtailItemBean> list;
	private LayoutInflater inflater;

	public void setData(Context c, List<OrderDtailItemBean> beans) {
		this.list = beans;
		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		notifyDataSetChanged();
	}

	public OrderDetailAdapter(Context c, List<OrderDtailItemBean> beans) {
		super();
		this.list = beans;
		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO
		return list.get(position);
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
			convertView = inflater.inflate(R.layout.orderdetail_item, null);
			holder.num = (TextView) convertView.findViewById(R.id.item_num);
			holder.name = (TextView) convertView.findViewById(R.id.item_name);
			holder.caizhi = (TextView) convertView
					.findViewById(R.id.item_caizhi);
			holder.guige = (TextView) convertView.findViewById(R.id.item_guige);
			holder.cankaohoudu = (TextView) convertView
					.findViewById(R.id.item_houdu);
			holder.kunbanghao = (TextView) convertView
					.findViewById(R.id.item_kunbanghao);
			holder.wuliaohao = (TextView) convertView
					.findViewById(R.id.item_wuliaohao);
			holder.jizhongfangshi = (TextView) convertView
					.findViewById(R.id.item_weightmethod);
			holder.zhixingbaozhun = (TextView) convertView
					.findViewById(R.id.item_standard);
			holder.dizhi = (TextView) convertView
					.findViewById(R.id.item_address);
			holder.zhongliang = (TextView) convertView
					.findViewById(R.id.item_weight);
			holder.danjia = (TextView) convertView
					.findViewById(R.id.item_price);
			holder.beizhu = (TextView) convertView.findViewById(R.id.item_mark);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 填充数据
		OrderDtailItemBean bean = list.get(position);
		holder.num.setText(String.valueOf(position + 1));
		holder.name.setText(bean.getChanPin());
		holder.caizhi.setText(bean.getCaiZhi());
		holder.guige.setText(bean.getGuiGe());
		holder.cankaohoudu.setText("参考厚度:" + bean.getCanKaoHouDu());
		holder.kunbanghao.setText("捆绑号:" + bean.getKunBaoHao());
		holder.wuliaohao.setText("物料号:" + bean.getWuLiaoHao());
		holder.jizhongfangshi.setText("计重方式:" + bean.getJiZhongFangShi());
		holder.zhixingbaozhun.setText("执行标准:" + bean.getZhiXingBiaoZhun());
		holder.dizhi.setText(bean.getMaiJia());
		holder.zhongliang.setText(String.valueOf(bean.getZhongLiang()));
		holder.beizhu.setText(bean.getBeiZhu());
		holder.danjia.setText("¥:" + bean.getDanJia());

		return convertView;
	}

	class ViewHolder {
		TextView num;
		TextView name;
		TextView caizhi;
		TextView guige;
		TextView cankaohoudu;
		TextView kunbanghao;
		TextView wuliaohao;
		TextView jizhongfangshi;
		TextView zhixingbaozhun;
		TextView dizhi;
		TextView zhongliang;
		TextView danjia;
		TextView beizhu;
	}
}
