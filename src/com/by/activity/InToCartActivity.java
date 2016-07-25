package com.by.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.example.baoyisteel.R;

public class InToCartActivity extends Activity implements OnClickListener {

	private TextView CaiZhi;
	private TextView GuiGe;
	private TextView Weight;
	private TextView Price;
	private TextView AllMoney;
	private TextView PinMing;
	private Button ToBuy;
	private Button ToCart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intocart);
		initView();
		setData();

	}

	private void initView() {

		CaiZhi = (TextView) findViewById(R.id.cart_caizhi);
		GuiGe = (TextView) findViewById(R.id.cart_guige);
		Weight = (TextView) findViewById(R.id.cart_weight);
		Price = (TextView) findViewById(R.id.cart_price);
		AllMoney = (TextView) findViewById(R.id.cart_totalmoney);
		PinMing = (TextView) findViewById(R.id.cart_name);
		ToBuy = (Button) findViewById(R.id.m_tobuy);
		ToCart = (Button) findViewById(R.id.m_tocart);
		ToBuy.setOnClickListener(this);
		ToCart.setOnClickListener(this);
	}

	// 数据填充
	private void setData() {

		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("Info");
		Price.setText("单价:¥" + bundle.getString("danjia"));
		Weight.setText("重量:" + bundle.getString("zhongliang") + "吨");
		CaiZhi.setText("材质:" + bundle.getString("caizhi"));
		GuiGe.setText("规格:" + bundle.getString("guige"));
		PinMing.setText(bundle.getString("pinming"));
		String weight = bundle.getString("zhongliang");
		String danjia = bundle.getString("danjia");
		float money = Float.parseFloat(weight) * Float.parseFloat(danjia);
		AllMoney.setText("总计:¥ " + money);
	}

	// 按钮反应机制
	@Override
	public void onClick(View v) {

		int id = v.getId();
		switch (id) {
		case R.id.m_tobuy:
			v.setBackgroundResource(R.drawable.unclick);
			finish();
			break;
		case R.id.m_tocart:
			Intent intent = new Intent(this, ShopCarActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	public void onBack(View v) {
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
		JPushInterface.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

}
