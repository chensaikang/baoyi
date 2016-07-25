package com.by.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.by.adapter.CartAdapter;
import com.by.interf.OnLoginListener;
import com.by.interf.onGetMoney;
import com.by.javabean.SteelBean;
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

public class ShopCarActivity extends Activity implements
		OnCheckedChangeListener, onGetMoney, OnClickListener, OnLoginListener {

	private LinearLayout noLayout;
	private LinearLayout yesLayout;
	private ListView lv;
	private String url = Config.URL_PATH + "buyer.asmx/MyCart";
	private SharedPreferences sharedPreferences;
	private CheckBox all;
	private TextView Money;
	private TextView totalNum;
	private int num = 0;
	private float allmoneyGet = 0;
	private float preMoney;
	private Button toOrder;
	private String OpenId;
	private CartAdapter adapter;
	public List<SteelBean> list = new ArrayList<SteelBean>();
	private List<SteelBean> beans;
	private Button shop;
	private HttpUtils httpUtils;
	private View inflate;// listview--foot

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shopcar);
		initView();// 初始化

	}

	private void getData() {
		if (NetworkUtil.isNetworkAvailable(this)) {

			// 获取登录信息
			sharedPreferences = getSharedPreferences("LoginInfo", 0);
			OpenId = sharedPreferences.getString("openId", null);
			httpUtils = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("OpenId", OpenId);
			httpUtils.send(HttpMethod.POST, url, params,
					new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException arg0, String arg1) {

							// if (NetworkUtil
							// .isNetworkAvailable(ShopCarActivity.this))
							// reLogin();

						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {

							String json = arg0.result;
							if ("-1".equals(json)) {
								reLogin();
							} else {
								jsonParse(json);
								if (beans.isEmpty()) {
									noLayout.setVisibility(View.VISIBLE);
								} else {
									yesLayout.setVisibility(View.VISIBLE);
									inflateFooter();// 该操作必须在添加适配器之前
									// 添加listview适配器
									adapter = new CartAdapter(
											ShopCarActivity.this, beans);
									lv.setAdapter(adapter);
								}
							}
						}
					});
		}
	}

	private void initView() {
		noLayout = (LinearLayout) findViewById(R.id.ll_no);// 无商品视图
		yesLayout = (LinearLayout) findViewById(R.id.ll_yes);// 有商品视图
		findViewById(R.id.iv_navigation).setOnClickListener(this);// 返回键监听

		shop = (Button) findViewById(R.id.toshop);// 无视图中去购买btn
		lv = (ListView) findViewById(R.id.car_lv);
		all = (CheckBox) findViewById(R.id.all_select);// 全选按钮
		all.setOnCheckedChangeListener(this);// 监听
		shop.setOnClickListener(new OnClickListener() {// 监听
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(ShopCarActivity.this,
						BaseActivity.class);
				// intent.putExtra("position", 1);
				Config.ITEM_POSITION = 1;
				startActivity(intent);

			}
		});

	}

	// json解析
	private void jsonParse(String json) {

		beans = new ArrayList<SteelBean>();
		// beans = JSON.parseArray(json, SteelBean.class);
		try {
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				SteelBean bean = new SteelBean();
				bean.setId(object.getInt("Id"));
				bean.setPid(object.getInt("pid"));
				bean.setCaiZhi(object.getString("caiZhi"));
				bean.setPinMing(object.getString("pinMing"));
				bean.setGuiGe(object.getString("guiGe"));
				bean.setChanDi(object.getString("chanDi"));
				bean.setCangKu(object.getString("cangKu"));
				bean.setMaiJia(object.getString("maiJia"));
				bean.setBiaoMian(object.getString("biaoMian"));
				bean.setBiaoShu(object.getString("biaoShu"));
				bean.setZhongLiang(object.getString("zhongLiang"));
				bean.setZongJia(object.getDouble("zongJia"));
				bean.setDanJia(object.getDouble("danJia"));
				beans.add(bean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 全选状态总金额
	public void getAllMoney() {

		allmoneyGet = 0;
		for (int i = 0; i < beans.size(); i++) {
			String zongjia = beans.get(i).getZongJia() + "";
			float money = Float.parseFloat(zongjia);
			allmoneyGet += money;
		}
		totalNum.setText(beans.size() + "件商品总额:");
		Money.setText("¥" + allmoneyGet);
	}

	/****************************************** 全选监听 **********************************************/
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (isChecked) {
			buttonView.setButtonDrawable(R.drawable.check_3x);
			num = beans.size();

		} else {
			buttonView.setButtonDrawable(R.drawable.box_3x);
			num = 0;
			Money.setText("¥" + 0.00);
			totalNum.setText(num + "件商品总额:");

		}
		int id = buttonView.getId();
		switch (id) {
		case R.id.all_select: // 全选

			adapter.notifyDataSetChanged();

			adapter.allSelect(isChecked);

			break;
		case R.id.delete_all: // 全删，待开发

			break;

		default:
			break;
		}
	}

	// 实现提交订单功能
	private void inflateFooter() {

		inflate = getLayoutInflater().inflate(R.layout.cart_foot, null);
		Money = (TextView) inflate.findViewById(R.id.totalMoney);
		totalNum = (TextView) inflate.findViewById(R.id.totalNum);
		toOrder = (Button) inflate.findViewById(R.id.pay);
		lv.addFooterView(inflate);

		toOrder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
				String ids = null;
				if (all.isChecked())
					list = beans;
				for (int i = 0; i < list.size(); i++) {
					SteelBean steelBean = list.get(i);
					int id = steelBean.getPid();
					if (i == 0) {
						ids = String.valueOf(id);
					} else {
						ids += "," + id;
					}
				}
				String url = Config.URL_PATH + "buyer.asmx/ToOrder?OpenId="
						+ OpenId + "&ids=" + ids + "&type="
						+ Config.DEVICE_TYPE;

				httpUtils.send(HttpMethod.GET, url,
						new RequestCallBack<String>() {
							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								// TODO
								String str = arg0.result;
								if ("-1".equals(str)) {
									reLogin();
								}
								if ("1".equals(str)) {
									Intent intent = new Intent(
											ShopCarActivity.this,
											IndentStateActivity.class);
									startActivity(intent);
								}
								if ("10".equals(str))
									Toast.makeText(ShopCarActivity.this,
											"提交订单失败", 0).show();
							}
						});
			}
		});

		CheckBox deleteAll = (CheckBox) inflate.findViewById(R.id.delete_all);
		TextView delete_all = (TextView) inflate.findViewById(R.id.determine);

		/****************************** 待开发 ***********************************************************/

		delete_all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		deleteAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked)
					buttonView.setButtonDrawable(R.drawable.check_3x);

				else {
					buttonView.setButtonDrawable(R.drawable.box_3x);
				}
			}
		});
		/***************************************** 登录的接口回调 *************************************************************************/
	}

	private void reLogin() {
		// LoginUtils.onLogin(this,true,1);
		LoginUtils.onLogin(this, true, Config.CLASS_TYPE);
	}

	@Override
	public void setAllSelectState(boolean state) {

		all.setChecked(false);

	}

	// 全选标志位
	@Override
	public void getAllMoney(boolean flag) {

		if (flag) {
			this.getAllMoney();
		}
	}

	// 接口回调机制
	@Override
	public void getMoney(SteelBean bean, int i) {
		double money = bean.getZongJia();
		// float money = Float.parseFloat(zongjia);
		String pre = Money.getText().toString();
		String substring = pre.substring(1);
		preMoney = Float.parseFloat(substring);
		switch (i) {
		case 1:
			num++;
			list.add(bean);
			preMoney += money;

			Money.setText("¥" + preMoney);
			totalNum.setText(num + "件商品总金额:");

			if (num == beans.size()) {
				list.clear();
				all.setChecked(true);
			}
			break;

		case 0:
			num--;
			list.remove(bean);
			preMoney -= money;
			if (num < beans.size()) {

				all.setChecked(false);
			}
			if (num <= 0) {

				num = 0;
				Money.setText("¥" + 0.00);
				totalNum.setText(0 + "件商品总金额");
				all.setChecked(false);
				adapter.notifyDataSetChanged();
			} else {
				Money.setText("¥" + preMoney);
				totalNum.setText(num + "件商品总金额:");
			}
			break;
		}
	}

	@Override
	protected void onRestart() {

		super.onRestart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		getData();

		JPushInterface.onResume(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onClick(View v) {

		finish();
	}

	@Override
	public void onLoginListener() {
		reLogin();
	}

	@Override
	public void onLayoutShow(boolean flag) {
		if (flag) {
			lv.removeFooterView(inflate);
			noLayout.setVisibility(View.VISIBLE);
			yesLayout.setVisibility(View.GONE);
		}
	}

}
