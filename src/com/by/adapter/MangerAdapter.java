package com.by.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.by.activity.InToCartActivity;
import com.by.interf.OnLoginListener;
import com.by.javabean.ChildBean;
import com.by.javabean.GroupBean;
import com.by.utils.Config;
import com.by.utils.FastClickUtils;
import com.by.utils.NetworkUtil;
import com.example.baoyisteel.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class MangerAdapter extends BaseExpandableListAdapter {

	@SuppressWarnings("unused")
	private LayoutInflater inflater;
	public List<GroupBean> groupBeans;
	public List<List<ChildBean>> list;
	private GroupBean groupBean;
	private ChildBean childBean;
	private String openId;
	private OnLoginListener listener;
	private Activity activity;

	public MangerAdapter(Context c, List<GroupBean> gBeans,
			List<List<ChildBean>> cBeans) {

		// inflater = (LayoutInflater) c
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		groupBeans = gBeans;
		list = cBeans;
		listener = (OnLoginListener) c;
		activity = (Activity) c;
	}

	/**
	 * 
	 * @param gBeans
	 * @param cBeans
	 */
	public void setData(List<GroupBean> gBeans, List<List<ChildBean>> cBeans) {

		groupBeans = gBeans;
		list = cBeans;
		notifyDataSetChanged();
	}

	@Override
	public int getGroupCount() {
		return groupBeans.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {

		return list.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {

		return groupBeans.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {

		return list.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {

		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {

		return childPosition;
	}

	@Override
	public boolean hasStableIds() {

		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupHolder holder;
		if (convertView == null) {

			holder = new GroupHolder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.ep_f, null);

			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.material = (TextView) convertView
					.findViewById(R.id.material);
			holder.price = (TextView) convertView.findViewById(R.id.price);
			holder.weight = (TextView) convertView.findViewById(R.id.weight);

			convertView.setTag(holder);
		} else {
			holder = (GroupHolder) convertView.getTag();
		}
		groupBean = groupBeans.get(groupPosition);

		holder.name.setText(groupBean.getChanPin());
		holder.material.setText(groupBean.getCaizhi());
		holder.price.setText("¥ " + groupBean.getDanJia());
		holder.weight.setText(groupBean.getZhongLiang());

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, final ViewGroup parent) {

		final ChildHolder holder;
		if (convertView == null) {
			holder = new ChildHolder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.ep_second, null);

			holder.cankaohoudu = (TextView) convertView
					.findViewById(R.id.tv_cankaohoudu);
			holder.num = (TextView) convertView
					.findViewById(R.id.tv_kunbanghao);
			holder.et_sell = (TextView) convertView
					.findViewById(R.id.tv_et_sell);
			holder.stg_dre = (TextView) convertView.findViewById(R.id.tv_stg);
			holder.btn = (Button) convertView.findViewById(R.id.add);
			holder.jizhongfangshi = (TextView) convertView
					.findViewById(R.id.tv_jizhongfagnshi);
			holder.standard = (TextView) convertView
					.findViewById(R.id.tv_zhixingbiaozhun);
			holder.wuliaohao = (TextView) convertView
					.findViewById(R.id.tv_wuliaohao);
			holder.beizhu = (TextView) convertView.findViewById(R.id.tv_mark);
			holder.bian = (TextView) convertView.findViewById(R.id.tv_bian);
			holder.guige = (TextView) convertView.findViewById(R.id.tv_guige);

			// holder.guige=(TextView) convertView.findViewById(R.id.tv_guige);
			convertView.setTag(holder);

		} else {
			holder = (ChildHolder) convertView.getTag();
		}

		childBean = list.get(groupPosition).get(childPosition);

		holder.bian.setText("边部/表面:" + childBean.getBianBu());
		holder.et_sell.setText(childBean.getMaiJia());
		holder.stg_dre.setText(childBean.getStorage());
		holder.num.setText("捆绑号:" + childBean.getKunBaoHao());
		holder.standard.setText("执行标准:" + childBean.getZhiXingBiaoZhun());
		holder.jizhongfangshi.setText("计重方式:" + childBean.getJiZhongFangShi());
		holder.wuliaohao.setText("物料号:" + childBean.getWuLiaoHao());
		holder.cankaohoudu.setText("参考厚度:" + childBean.getCanKaoHouDu());
		holder.beizhu.setText(childBean.getBeiZhu());

		holder.guige.setText("规 格:" + childBean.getGuiGe());

		holder.btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 防止暴力点击
				if (FastClickUtils.isFastDoubleClick()) {
					return;
				} else {

					openId = parent.getContext()
							.getSharedPreferences("LoginInfo", 0)
							.getString("openId", null);

					if (NetworkUtil.isNetworkAvailable(activity)) {

						HttpUtils utils = new HttpUtils();
						String url = Config.URL_PATH + "Manager.asmx/AddToCart";
						RequestParams params = new RequestParams();

						params.addBodyParameter("pid", groupBean.getId());

						params.addBodyParameter("OpenId", openId);

						utils.send(HttpMethod.POST, url, params,
								new RequestCallBack<String>() {

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {

										Toast.makeText(activity, arg1, 0)
												.show();
										// listener.onLoginListener();
									}

									// ************************************************************
									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {

										String json = arg0.result;

										if ("-1".equals(json)) {
											listener.onLoginListener();
										}
										try {
											JSONObject object = new JSONObject(
													json);
											boolean isOk = object
													.getBoolean("isOk");
											if (isOk) {
												Log.e("TAG", "ok===="
														+ arg0.result);
												Intent intent = new Intent(
														parent.getContext(),
														InToCartActivity.class);
												Bundle bundle = new Bundle();
												bundle.putString("pinming",
														groupBean.getChanPin());
												bundle.putString("danjia",
														groupBean.getDanJia());
												bundle.putString(
														"zhongliang",
														groupBean
																.getZhongLiang());
												bundle.putString("caizhi",
														groupBean.getCaizhi());
												bundle.putString("guige",
														childBean.getGuiGe());
												intent.putExtra("Info", bundle);
												parent.getContext()
														.startActivity(intent);
											} else if (!isOk) {
												Toast.makeText(
														activity,
														object.getString("result"),
														0).show();
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								});
					}
				}
			}
		});

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {

		return true;
	}

	class GroupHolder {
		TextView name;
		TextView material;
		TextView weight;
		TextView price;
	}

	class ChildHolder {

		TextView cankaohoudu;
		TextView jizhongfangshi;// 璁￠噸鏂瑰紡
		TextView wuliaohao;// 鐗╂枡鍙
		TextView num;// 鎹嗙粦鍙
		TextView standard;// 鎵ц鏍囧噯
		TextView guige;// 瑙勬牸
		TextView bian;// 杈
		TextView stg_dre;// 浠撳簱
		TextView et_sell;// 鍗栧
		TextView beizhu;// 澶囨敞
		Button btn;// 璇︽儏

	}

}
