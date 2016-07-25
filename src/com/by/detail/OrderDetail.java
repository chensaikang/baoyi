package com.by.detail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.by.adapter.OrderDetailAdapter;
import com.by.fragment_levelexpand.CddInfo;
import com.by.javabean.OrderDtailItemBean;
import com.by.utils.Config;
import com.by.utils.NetworkUtil;
import com.by.view.MyListView;
import com.example.baoyisteel.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 
 * @author CSK -订单详情
 * 
 */
public class OrderDetail extends Fragment implements OnClickListener {

	@SuppressWarnings("unused")
	private int oid;
	// private String openId;
	private String heTongHao;
	private String qianYueRiQi;
	private String mingCheng;
	private String dianHua;
	private String diZhi;
	private String youBian;
	private String huiYuanDiZhi;
	private String shuiHao;
	private String mingCheng1;
	private String dianHua1;
	private String diZhi1;
	private String youBian1;
	private String huiYuanDaiMa1;
	private String shuiHao1;
	@SuppressWarnings("unused")
	private String daiYunIsOk;
	@SuppressWarnings("unused")
	private String huoWuHeJi;
	private double zongJi;
	private TextView orderHeT;
	private TextView orderDate;
	private TextView orderSell;
	private TextView orderBuy;
	private TextView orderphone;
	private TextView orderphone1;
	private TextView orderaddress;
	private TextView orderaddress1;
	private TextView ordercode;
	private TextView ordercode1;
	private TextView ordertax;
	private TextView ordertax1;
	private TextView ordermenber;
	private TextView ordermenber1;
	private TextView ordertotal;
	private MyListView lv;
	private Button jump;
	private OrderDetailAdapter adapter;
	private String date;
	private String cddurl;
	private LinearLayout layout;
	private View inflate;
	private Activity mActivity;

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		mActivity = activity;
		Config.ITEM_POSITION = 411;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Log.e("TAG", "detail_CreateView");
		inflate = inflater.inflate(R.layout.intent_detail, null);

		// Bundle bundle = getArguments();
		// if (bundle != null) {
		//
		// oid = bundle.getInt("oid");
		// openId = bundle.getString("openId");
		// // title = bundle.getString("title");
		// // Config.ORDERTAIL_OID = oid;
		// // Config.OPENID = openId;
		// Log.e("TAG", "sffasfaonCreateView=====" + Config.ORDERTAIL_OID
		// + "0000000" + Config.OPENID);
		// }
		// initView();

		getDataFromNet();

		return inflate;
	}

	private void initView() {

		layout = (LinearLayout) inflate.findViewById(R.id.detail_layout);
		inflate.findViewById(R.id.iv_orderdetail).setOnClickListener(this);
		orderHeT = (TextView) inflate.findViewById(R.id.cddNum);
		orderHeT.setText("合同号:" + heTongHao);
		orderDate = (TextView) inflate.findViewById(R.id.date);
		orderDate.setText("签约日期:" + formatDate(date));
		orderSell = (TextView) inflate.findViewById(R.id.sellName);
		orderSell.setText("名称:" + mingCheng);
		orderBuy = (TextView) inflate.findViewById(R.id.buyName);
		orderBuy.setText("名称:" + mingCheng1);
		orderphone = (TextView) inflate.findViewById(R.id.sellphone);
		orderphone.setText("电话:" + dianHua);
		orderphone1 = (TextView) inflate.findViewById(R.id.buyphone);
		orderphone1.setText("电话:" + dianHua1);
		orderaddress = (TextView) inflate.findViewById(R.id.selladdress);
		orderaddress.setText("地址:" + diZhi);
		orderaddress1 = (TextView) inflate.findViewById(R.id.buyaddress);
		orderaddress1.setText("地址:" + diZhi1);
		ordercode = (TextView) inflate.findViewById(R.id.sellcode);
		ordercode.setText("邮编:" + youBian);
		ordercode1 = (TextView) inflate.findViewById(R.id.buycode);
		ordercode1.setText("邮编:" + youBian1);
		ordertax = (TextView) inflate.findViewById(R.id.selltax);
		ordertax.setText("税号:" + shuiHao);
		ordertax1 = (TextView) inflate.findViewById(R.id.buytax);
		ordertax1.setText("税号:" + shuiHao1);
		ordermenber = (TextView) inflate.findViewById(R.id.sellmember);
		ordermenber.setText("会员代号:" + huiYuanDiZhi);
		ordermenber1 = (TextView) inflate.findViewById(R.id.buymember);
		ordermenber1.setText("会员代号:" + huiYuanDaiMa1);
		ordertotal = (TextView) inflate.findViewById(R.id.orderTotal);
		ordertotal.setText("¥" + String.valueOf(zongJi));

		lv = (MyListView) inflate.findViewById(R.id.order_lv);
		jump = (Button) inflate.findViewById(R.id.orderCDD);

		jump.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CddInfo cdd = new CddInfo();
				Bundle bundle = new Bundle();
				bundle.putString("url", cddurl);
				cdd.setArguments(bundle);
				getFragmentManager().beginTransaction()
						.replace(R.id.fragment, cdd).addToBackStack(null)
						.commit();
				// Intent intent = new Intent(mActivity, CddInfoActivity.class);
				// intent.putExtra("url", cddurl);
				// startActivity(intent);
				// Toast.makeText(mActivity, "jump", 0).show();

			}
		});

	}

	private void getDataFromNet() {

		if (NetworkUtil.isNetworkAvailable(mActivity)) {
			// Log.e("TAG", "sffasfaonCreateView=====" + Config.ORDERTAIL_OID
			// + "0000000" + Config.OPENID);
			HttpUtils utils = new HttpUtils();
			String url = Config.URL_PATH + "buyer.asmx/Detail?&oid="
					+ Config.ORDERTAIL_OID + "&OpenId=" + Config.OPENID;
			utils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Toast.makeText(mActivity, "请重新登录", 0).show();

				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					String json = arg0.result;
					Log.e("TAG", "RESULT=" + json);
					if ("-1".equals(json)) {
						// LoginUtils.onLogin(mActivity, true, 0);
						if (Config.INDENT_TITLE.equals("Buyer.asmx")) {

							mActivity.getFragmentManager().popBackStack(
									"buyer", 0);
						} else {
							mActivity.getFragmentManager().popBackStack(
									"seller", 0);
						}
					} else {

						try {
							JSONObject object = new JSONObject(json);
							heTongHao = object.getString("heTongHao");
							qianYueRiQi = object.getString("qianYueRiQi");

							date = qianYueRiQi.substring(6, 19);

							mingCheng = object.getString("mingCheng");
							dianHua = object.getString("dianHua");
							diZhi = object.getString("diZhi");
							youBian = object.getString("youBian");
							huiYuanDiZhi = object.getString("huiYuanDiZhi");
							shuiHao = object.getString("shuiHao");
							mingCheng1 = object.getString("mingCheng1");
							dianHua1 = object.getString("dianHua1");
							diZhi1 = object.getString("diZhi1");
							youBian1 = object.getString("youBian1");
							huiYuanDaiMa1 = object.getString("huiYuanDaiMa1");
							shuiHao1 = object.getString("shuiHao1");
							daiYunIsOk = object.getString("daiYunIsOk");
							huoWuHeJi = object.getString("huoWuHeJi");
							zongJi = object.getDouble("zongJi");
							cddurl = object.getString("ServiceInfoUrl");
							// fastjson解析
							String json2 = object.getString("changPin");
							List<OrderDtailItemBean> list = JSON.parseArray(
									json2, OrderDtailItemBean.class);
							if (list.size() != 0) {

								initView();
								adapter = new OrderDetailAdapter(mActivity,
										list);
								lv.setAdapter(adapter);
								layout.setVisibility(View.VISIBLE);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

	// 时间格式

	public String formatDate(String str) {

		Date date = new Date(Long.parseLong(str));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}

	@Override
	public void onClick(View v) {

		mActivity.getFragmentManager().popBackStackImmediate();

	}

	@Override
	public void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();
		adapter = null;
		getDataFromNet();
	}

	@Override
	public void onDestroy() {
		// TODO
		super.onDestroy();
	}

}
