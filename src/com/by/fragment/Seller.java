package com.by.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.by.fragment_levelexpand.Seller_Indent;
import com.by.fragment_levelexpand.Session;
import com.by.utils.Config;
import com.example.baoyisteel.R;

public class Seller extends Fragment {

	private View inflate;
	private int[] images = new int[] { R.drawable.dindan, R.drawable.jingjia };
	private String[] strs = new String[] { "我的订单", "我的场次" };
	private GridView gv;
	private SharedPreferences sharedPreferences;
	private TextView rem;
	private FragmentManager manager;
	private SellAdapter adapter;
	private Activity mActivity;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				Config.FIRSIT_IN = true;
			}
		};
	};
	private TextView title;

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		mActivity = activity;
		Config.ITEM_POSITION = 4;
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
		title.setText("卖家中心");
		sharedPreferences = getActivity().getSharedPreferences("LoginInfo", 0);
		String num = sharedPreferences.getString("name", null);
		if (num != null) {
			rem.setText(num);
		} else {
			// 未登录状态可显示功能模块
			if (Config.FIRSIT_IN) {
				Config.FIRSIT_IN = false;
				handler.sendEmptyMessageDelayed(0, 60000);
				Intent intent = new Intent(mActivity, LoginActivity.class);
				startActivity(intent);
			} else {
				rem.setText("未登录");
			}

		}
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
				case 0:// 订单碎片
					Seller_Indent indent = new Seller_Indent();
					Config.INDENT_TITLE = "Seller.asmx";
					// Bundle bundle = new Bundle();
					// String title = "Seller.asmx";
					// bundle.putString("url", title);
					// indent.setArguments(bundle);
					Log.e("TAG", "SEllER--setargument");
					transaction.replace(R.id.fragment, indent);

					break;
				case 1:// 竞价碎片
					transaction.replace(R.id.fragment, new Session());
					break;
				case 2:

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
			if (position == clickTemp)// 设置点击背景
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
	public void onResume() {

		adapter.setSeclection(-1);
		adapter.notifyDataSetChanged();
		super.onResume();
	}

}
