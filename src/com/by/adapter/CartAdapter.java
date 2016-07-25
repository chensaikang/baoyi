package com.by.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.by.interf.OnLoginListener;
import com.by.interf.onGetMoney;
import com.by.javabean.SteelBean;
import com.by.utils.Config;
import com.example.baoyisteel.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class CartAdapter extends BaseAdapter {

	private List<SteelBean> list;
	private LayoutInflater inflater;
	private SteelBean steelBean;
	private onGetMoney getMoney;
	private boolean Flag = false;
	private SharedPreferences sp;
	private Context context;
	private OnLoginListener listener;

	public CartAdapter(Context c, List<SteelBean> beans) {
		super();
		list = beans;
		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		getMoney = (onGetMoney) c;
		sp = c.getSharedPreferences("LoginInfo", 0);
		context = c;
		listener = (OnLoginListener) c;
	}

	public void allSelect(boolean flag) {

		Flag = flag;
		getMoney.getAllMoney(Flag);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return list.size() != 0 ? list.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.cart_item, null);
			holder.box = (CheckBox) convertView.findViewById(R.id.cb_pinming);
			holder.DanJia = (TextView) convertView
					.findViewById(R.id.order_danjia);
			holder.Weight = (TextView) convertView
					.findViewById(R.id.order_zhongliang);
			holder.ZongJia = (TextView) convertView
					.findViewById(R.id.order_zongjia);
			holder.delete = (TextView) convertView
					.findViewById(R.id.order_zaozuo);
			// 全选监听
			holder.box
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								buttonView
										.setButtonDrawable(R.drawable.check_3x);
								if (!Flag) {
									getMoney.getMoney(list.get(position), 1);
								}
							}
							if (!isChecked) {
								buttonView.setButtonDrawable(R.drawable.box_3x);
								getMoney.setAllSelectState(false);
								getMoney.getMoney(list.get(position), 0);

							}
						}
					});
			// 删除监听
			holder.delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					HttpUtils utils = new HttpUtils();
					String url = Config.URL_PATH + "Manager.asmx/DeleteItem";
					RequestParams params = new RequestParams();
					params.addBodyParameter("id",
							String.valueOf(steelBean.getId()));

					params.addBodyParameter("OpenId",
							sp.getString("openId", null));

					utils.send(HttpMethod.POST, url, params,
							new RequestCallBack<String>() {

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {

								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {

									String json = arg0.result;
									if ("-1".equals(json)) {
										listener.onLoginListener();
									} else {
										try {
											JSONObject object = new JSONObject(
													json);
											boolean isOk = object
													.getBoolean("isOk");
											String result = object
													.getString("result");
											if (isOk) {
												Toast.makeText(context, result,
														0).show();
												getMoney.getMoney(
														list.get(position), 0);
												list.remove(steelBean);
												if (list.isEmpty()) {
													getMoney.onLayoutShow(true);
												}
												notifyDataSetChanged();
											} else if (!isOk) {
												Toast.makeText(context, result,
														0).show();
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}

									}
								}
							});

				}
			});

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.box.setChecked(Flag);

		steelBean = list.get(position);
		holder.box.setText(steelBean.getPinMing());
		holder.DanJia.setText("¥" + steelBean.getDanJia());

		holder.Weight.setText(steelBean.getZhongLiang());
		holder.ZongJia.setText("¥" + steelBean.getZongJia());

		return convertView;
	}

	class ViewHolder {

		CheckBox box;
		TextView DanJia;
		TextView ZongJia;
		TextView Weight;
		TextView delete;

	}

}
