package com.example.baoyisteel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.by.activity.ShopCarActivity;
import com.by.detail.ManagerDetail;
import com.by.detail.OrderDetail;
import com.by.fragment.Biding;
import com.by.fragment.Buyer;
import com.by.fragment.Home;
import com.by.fragment.Manager;
import com.by.fragment.Seller;
import com.by.fragment.Setting;
import com.by.fragment_levelexpand.BuyerBiding;
import com.by.fragment_levelexpand.Indent;
import com.by.fragment_levelexpand.Search_Pm;
import com.by.fragment_levelexpand.Seller_Indent;
import com.by.fragment_levelexpand.Session;
import com.by.fragment_levelexpand.SteelBidding;
import com.by.interf.OnLoginListener;
import com.by.utils.Config;
import com.by.utils.LoginUtils;

public class BaseActivity extends Activity implements
		android.widget.RadioGroup.OnCheckedChangeListener, OnLoginListener {

	private RadioGroup group;
	private RadioButton home;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private RadioButton pm;
	private RadioButton set;
	private RadioButton buyer;
	private int position = 0;// 当前导航位置
	private RadioButton seller;
	private RadioButton biding;

	public static boolean isForeground = false;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);

		initView();

		registerReceiver();// 注册广播
	}

	private void initView() {

		group = (RadioGroup) findViewById(R.id.group);
		pm = (RadioButton) findViewById(R.id.pm);
		home = (RadioButton) findViewById(R.id.home);
		set = (RadioButton) findViewById(R.id.set);
		buyer = (RadioButton) findViewById(R.id.buyer);
		seller = (RadioButton) findViewById(R.id.seller);
		biding = (RadioButton) findViewById(R.id.biding);

		manager = getFragmentManager();

		setFragmentItem();

		group.setOnCheckedChangeListener(this);// 导航监听

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		FragmentTransaction fragmentTransaction = manager.beginTransaction();

		switch (checkedId) {

		case R.id.home:
			fragmentTransaction.replace(R.id.fragment, new Home());
			fragmentTransaction.addToBackStack("home");
			break;
		case R.id.pm:
			fragmentTransaction.replace(R.id.fragment, new Manager());
			fragmentTransaction.addToBackStack("manager");// 添加回退栈
			break;
		case R.id.biding:// 设置竞价fragment
			fragmentTransaction.replace(R.id.fragment, new Biding());
			fragmentTransaction.addToBackStack("biding");// 添加回退栈
			break;
		case R.id.buyer:

			fragmentTransaction.replace(R.id.fragment, new Buyer());
			fragmentTransaction.addToBackStack("buyer");
			break;
		// 未开通
		case R.id.seller:

			fragmentTransaction.replace(R.id.fragment, new Seller());
			fragmentTransaction.addToBackStack("seller");
			break;
		case R.id.set:
			fragmentTransaction.replace(R.id.fragment, new Setting());
			fragmentTransaction.addToBackStack("set");
			break;

		}
		fragmentTransaction.commit();
	}

	@Override
	protected void onPause() {
		super.onPause();
		isForeground = false;
		JPushInterface.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		isForeground = true;
		JPushInterface.onResume(this);
	}

	@Override
	protected void onRestart() {
		Log.e("TAG", "onRestart==" + Config.ITEM_POSITION);
		// isForeground = true;
		super.onRestart();

		// setFragmentItem();
	}

	public void setFragmentItem() {

		// 保存至回退栈，便于定向

		switch (Config.ITEM_POSITION) {
		case 0:
			home.setChecked(true);

			manager.beginTransaction().replace(R.id.fragment, new Home())
					.addToBackStack("home").commit();
			break;
		case 010:
			home.setChecked(true);

			manager.beginTransaction()
					.replace(R.id.fragment, new ManagerDetail())
					.addToBackStack("home_bar").commit();
			break;
		case 1:
			pm.setChecked(true);
			manager.beginTransaction().replace(R.id.fragment, new Manager())
					.addToBackStack("pm").commit();
			break;
		case 11:
			pm.setChecked(true);
			manager.beginTransaction().replace(R.id.fragment, new Search_Pm())
					.addToBackStack("pm_search").commit();
			break;
		case 2:
			biding.setChecked(true);

			manager.beginTransaction().replace(R.id.fragment, new Biding())
					.addToBackStack("pm_search").commit();
			break;
		case 3:
			buyer.setChecked(true);

			manager.beginTransaction().replace(R.id.fragment, new Buyer())
					.addToBackStack("buyer").commit();
			break;
		case 31:// 为activity
			Intent intent = new Intent(this, ShopCarActivity.class);
			startActivity(intent);
			// buyer.setChecked(true);
			//
			// manager.beginTransaction().replace(R.id.fragment, new Indent())
			// .addToBackStack("indent").commit();
			break;
		case 32:

			buyer.setChecked(true);

			manager.beginTransaction().replace(R.id.fragment, new Indent())
					.addToBackStack("indent").commit();
			break;
		case 321:

			buyer.setChecked(true);

			manager.beginTransaction()
					.replace(R.id.fragment, new OrderDetail())
					.addToBackStack("indent_detail").commit();
			break;
		case 331:

			buyer.setChecked(true);

			manager.beginTransaction()
					.replace(R.id.fragment, new BuyerBiding())
					.addToBackStack("buyerbiding").commit();
			break;
		case 332:

			buyer.setChecked(true);

			manager.beginTransaction()
					.replace(R.id.fragment, new SteelBidding())
					.addToBackStack("mybiding").commit();
			break;
		case 4:

			seller.setChecked(true);
			manager.beginTransaction().replace(R.id.fragment, new Seller())
					.addToBackStack("seller").commit();
			break;
		case 41:

			seller.setChecked(true);
			manager.beginTransaction()
					.replace(R.id.fragment, new Seller_Indent())
					.addToBackStack("seller_indent").commit();
			break;
		case 411:

			seller.setChecked(true);
			manager.beginTransaction()
					.replace(R.id.fragment, new OrderDetail())
					.addToBackStack("seller_indentdetail").commit();
			break;
		case 42:

			seller.setChecked(true);
			manager.beginTransaction().replace(R.id.fragment, new Session())
					.addToBackStack("seller_session").commit();
			break;
		case 5:
			set.setChecked(true);

			manager.beginTransaction().replace(R.id.fragment, new Setting())
					.addToBackStack("set").commit();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO
		super.onNewIntent(intent);
		setFragmentItem();
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onDestroy() {

		Config.OPENID = "";
		Config.ITEM_POSITION = 0;
		Config.FIRSIT_IN = true;
		Config.ORDERTAIL_OID = 0;
		Config.ISLOGIN = false;
		Config.SEARCH_KEYS = "";
		Config.INDENT_TITLE = null;
		Log.e("TAG", "fragment" + Config.ITEM_POSITION);
		unregisterReceiver(receiver);
		super.onDestroy();

	}

	@Override
	public void onLoginListener() {
		LoginUtils.onLogin(this, false, Config.FRAGMENT_TYPE);

	}

	public static final long TWO_SECOND = 2 * 1000;
	public long preTime;
	private MessageReceiver receiver;
	private RadioButton jingjia;

	@Override
	public void onBackPressed() {
		long currentTime = System.currentTimeMillis();

		if ((currentTime - preTime) > TWO_SECOND) {

			Toast.makeText(this, "再次点击可退出程序", 0).show();
			preTime = currentTime;
			return;
		} else {
			Config.ITEM_POSITION = 0;
			Config.FIRSIT_IN = true;
			Config.LOGIN_FLAG = false;

			finish();
		}
	}

	// 接受推送的接收器
	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {

				String messge = intent.getStringExtra(KEY_MESSAGE);
				String extras = intent.getStringExtra(KEY_EXTRAS);

				// showMsg(messge);
				showMsgByNatification(messge);
			}

		}

		private void showMsg(String s) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					BaseActivity.this);
			builder.setTitle("自定义消息");
			builder.setIcon(R.drawable.androidlogo4);
			builder.setMessage(s);
			builder.show();
		}

	}

	public void registerReceiver() {
		receiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(receiver, filter);
	}

	// 推送消息的格式设置
	public void showMsgByNatification(String s) {

		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// 通过builder对象创建nitification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);

		// builder.setSmallIcon(R.drawable.ic_launcher);
		// builder.setContentText("自定义消息");
		// builder.setContentText(s);

		NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
		style.addLine(s);
		builder.setStyle(style);
		RemoteViews views = new RemoteViews(getPackageName(),
				R.layout.notification_layout);
		views.setTextViewText(R.id.msg, s);
		// 自动销毁通知对象
		builder.setAutoCancel(true);
		// 创建跳转对象
		Intent intent = new Intent(this, BaseActivity.class);
		// 通过intent创建PendingIntent对象（上下文，notifyCode，intent，。。。）
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
				intent, PendingIntent.FLAG_ONE_SHOT);
		// 设置监听 并跳转
		views.setOnClickPendingIntent(R.id.notification, pendingIntent);

		builder.setContent(views).setSmallIcon(R.drawable.androidlogo4);
		manager.notify(1, builder.build());
	}
}
