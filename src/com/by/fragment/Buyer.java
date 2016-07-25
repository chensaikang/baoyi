package com.by.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.by.activity.LoginActivity;
import com.by.activity.ShopCarActivity;
import com.by.fragment_levelexpand.BuyerBiding;
import com.by.fragment_levelexpand.Indent;
import com.by.utils.Config;
import com.example.baoyisteel.R;

public class Buyer extends Fragment {

	private View inflate;
	private int[] images = new int[] { R.drawable.shopping, R.drawable.dindan,
			R.drawable.jingjia, R.drawable.daiyun, R.drawable.wuliu,
			R.drawable.tihuo };
	private String[] strs = new String[] { "我的购物车", "我的订单", "我的竞价", "我的代运",
			"物流管理", "提货人管理" };
	private GridView gv;
	private SharedPreferences sharedPreferences;
	private TextView rem;
	public static final long TWO_SECOND = 2 * 1000;
	public long preTime;
	private FragmentManager manager;
	private SellAdapter adapter;
	private Activity mActivity;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				Config.FIRSIT_IN = true;
			}
		};
	};
	@SuppressWarnings("unused")
	private boolean flag;
	private TextView title;

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		mActivity = activity;
		Config.ITEM_POSITION = 3;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		inflate = inflater.inflate(R.layout.buy, null);
		initView();
		return inflate;
	}

	private void initView() {

		gv = (GridView) inflate.findViewById(R.id.gv);
		rem = (TextView) inflate.findViewById(R.id.rem_num);
		title = (TextView) inflate.findViewById(R.id.name);
		title.setText("买家中心");
		adapter = new SellAdapter();
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				adapter.notifyDataSetChanged();

				manager = getFragmentManager();
				FragmentTransaction transaction = manager.beginTransaction();

				switch (position) {
				case 0:
					Intent intent;
					intent = new Intent(mActivity, ShopCarActivity.class);
					startActivity(intent);
					break;
				case 1:
					Config.INDENT_TITLE = "Buyer.asmx";
					Indent indent = new Indent();
					// Bundle bundle = new Bundle();
					// String title = "Buyer.asmx";
					// bundle.putString("url", title);
					// indent.setArguments(bundle);
					// Log.e("TAG", "SEllER--setargument");
					transaction.replace(R.id.fragment, indent);

					break;
				case 2:// 竞价
					transaction.replace(R.id.fragment, new BuyerBiding());
					break;

				}
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
	}

	// 适配器
	class SellAdapter extends BaseAdapter {

		private int clickTemp = -1;

		public void setSeclection(int position) {
			clickTemp = position;
		}

		@Override
		public int getCount() {

			return images.length;
		}

		@Override
		public Object getItem(int position) {

			return images[position];
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
				Context context = parent.getContext();
				LayoutInflater layout = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = layout.inflate(R.layout.gv_item, null);

				holder.iView = (ImageView) convertView
						.findViewById(R.id.iv_log);
				holder.tView = (TextView) convertView.findViewById(R.id.tv_log);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position == clickTemp)
				convertView.setBackgroundResource(R.color.gray);
			else {
				convertView.setBackgroundResource(R.color.White);
			}
			holder.iView.setImageResource(images[position]);
			holder.tView.setText(strs[position]);

			return convertView;
		}

		class ViewHolder {
			ImageView iView;
			TextView tView;
		}
	}

	@Override
	public void onStart() {
		// TODO 自动生成的方法存根
		super.onStart();

	}

	@Override
	public void onResume() {

		sharedPreferences = mActivity.getSharedPreferences("LoginInfo", 0);
		String num = sharedPreferences.getString("name", null);
		// Log.e("TAG", "name==" + num);
		if (num != null) {
			flag = true;
			rem.setText(num);
		} else {
			// Log.e("TAG", "FLAG=" + Config.FIRSIT_IN);
			if (Config.FIRSIT_IN) {
				Config.FIRSIT_IN = false;
				handler.sendEmptyMessageDelayed(0, 60000);
				// 跳转登陆页
				Intent intent = new Intent(mActivity, LoginActivity.class);
				startActivity(intent);
			} else {
				rem.setText("未登录");
			}
		}
		if (adapter != null) {

			adapter.setSeclection(-1);
			adapter.notifyDataSetChanged();
		}

		super.onResume();
	}

}
