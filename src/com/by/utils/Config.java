package com.by.utils;

/**
 * 
 * @author 此类意在创建全局变量，方便信息传递
 * 
 */

public class Config {

	public static final int STRING_TYPE = 0;
	public static final int BITMAP_TYPE = 1;
	// public static String OPENIDS = null;
	public static int NOTALLSELECT = 100;
	public static int ALLSELECT = 99;
	public static boolean ISLOGIN = false;
	// public static String MANAGER_PATHString =
	// "http://app.byssteel.com/Manager.asmx/SeahData?pageNo=1&ChanDi=&PinMing=&WareHouseName=&CaiZhi=&houDuMin=&houDuMax=&kuanDuMin=&kuanDuMax=&bypar=&orBy=&keys=";

	public static String Home_MNotice = "Home.asmx/MNoticeMore?pageNo=";// fragemnt的url
	public static String Home_BNotice = "Home.asmx/BNoticeMore?pageNo=";
	public static String Home_ZNotice = "Home.asmx/ZNoticeMore?pageNo=";

	public static String JJinfo = "home.asmx/JJinfo";

	// public static String URL_PATH = "http://apptest.baoyi188.com/";// 测试专用
	// public static String URL_PATH = "http://app.byssteel.com/";// 实用版
	public static String URL_PATH = "http://192.168.1.188:81/";// 内网
	// public static String URL_PATH = "http://192.168.1.4:8080/";// 内网
	public static int ITEM_POSITION = 0;// 碎片位置
	public static boolean LOGIN_FLAG = false;// 登录状态
	public static boolean FIRSIT_IN = true;// 买家未登录显示视图效果
	public static int ORDERTAIL_OID = 0;
	public static String OPENID = null;
	public static String SEARCH_KEYS = null;
	public static String INDENT_TITLE = null;

	public static int CLASS_TYPE = 1;// 提示登陆返回机制的标识，判断是activity/fragment
	public static int FRAGMENT_TYPE = 0;

	public static String DEVICE_TYPE = "android";// 设备类型-提交订单所需参数

}
