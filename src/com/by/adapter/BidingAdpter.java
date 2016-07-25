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
		// TODO �Զ����ɵķ������
		return beans.size() > 0 ? beans.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO �Զ����ɵķ������
		return beans.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO �Զ����ɵķ������
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
		holder.bid_name.setText(bean.getChangci_MingCheng());// ��������
		holder.bid_time.setText(formatDate2(bean.getJingmaiDate()));// �ϼ�ʱ��
		holder.bid_style.setText("���۷�ʽ: " + bean.getJinjiafangshi());
		// holder.name.setText("��˾����: " + bean.getGongsiname());

		holder.time_start.setText(formatDate(bean.getKaishiDate()));
		holder.time_end.setText(formatDate(bean.getJieshuDate()));
		holder.baozhengjin.setText("��֤��: " + bean.getBaozhengjin());

		if (!"����".equals(bean.getZhuangtai())) {
			holder.session_state.setText("ȥ����");
		}
		holder.session_state.setOnClickListener(new OnClickListener() {// ��ת���ҵľ���

					@Override
					public void onClick(View v) {// �õ��Ƿ�ɽ���������ľ��۵�Ȩ��

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

										if ("1".equals(json)) {// ���뾺��

											Config.ITEM_POSITION = 331;
											activity.setFragmentItem();
										}
										if ("2".equals(json)) {// �Ѿ��μ�
											Toast.makeText(activity, "�þ����Ѳμ�",
													0).show();
											Config.ITEM_POSITION = 331;
											activity.setFragmentItem();
										}
										if ("3".equals(json)) {// δ��ʼ
											Toast.makeText(activity, "�þ��Ļ�δ��ʼ",
													0).show();
										}
										if ("0".equals(json)) {// ʧ��

											Toast.makeText(activity, "�μӾ���ʧ��",
													0).show();
										}
										if ("-1".equals(json)) {// δ��¼

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

		TextView bid_name;// ���۳�����
		TextView bid_time;// ����ʱ��
		TextView bid_style;// ���۷�ʽ
		TextView name;// ��˾��
		TextView baozhengjin;// ��֤��
		Button session_state;// ����״̬
		TextView time_start;// ��ʼʱ��
		TextView time_end;// ����ʱ��

	}

	// ʱ�����ת��----��ȡ�ַ���
	public String formatDate(String str) {
		String timestamp = str.substring(6, 19);
		Date date = new Date(Long.parseLong(timestamp));
		// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  hh:mm");
		SimpleDateFormat format = new SimpleDateFormat("hh:mm");
		return format.format(date);
	}

	// ʱ�����ת��----��ȡ�ַ���
	public String formatDate2(String str) {
		String timestamp = str.substring(6, 19);
		Date date = new Date(Long.parseLong(timestamp));
		SimpleDateFormat format = new SimpleDateFormat("hh:mm");
		return "(" + format.format(date) + ")";
	}
}
