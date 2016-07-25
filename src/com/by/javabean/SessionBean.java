package com.by.javabean;

public class SessionBean {
	/**
	 * "mingCheng": "不锈冷轧期货竞价场次（20150724测试）", "zhaungTai": 3, "jinJiaQi":
	 * "/Date(1437720000000)/", "jinJiaZhi": "/Date(1437721200000)/",
	 * "baoZhengJin": 0 09-25 02:42:57.122: E/TAG(1810):
	 * SESSION--urlhttp://apptest
	 * .baoyi188.com/Seller.asmx/MySession?openId=923e7761
	 * -7935-43ea-a514-3a587a461426&pageNo=1
	 */
	private String mingCheng;
	private int zhaungTai;
	private String jinJiaQi;
	private String jinJiaZhi;
	private String baoZhengJin;

	public String getMingCheng() {
		return mingCheng;
	}

	public void setMingCheng(String mingCheng) {
		this.mingCheng = mingCheng;
	}

	public int getZhaungTai() {
		return zhaungTai;
	}

	public void setZhaungTai(int zhaungTai) {
		this.zhaungTai = zhaungTai;
	}

	public String getJinJiaQi() {
		return jinJiaQi;
	}

	public void setJinJiaQi(String jinJiaQi) {
		this.jinJiaQi = jinJiaQi;
	}

	public String getJinJiaZhi() {
		return jinJiaZhi;
	}

	public void setJinJiaZhi(String jinJiaZhi) {
		this.jinJiaZhi = jinJiaZhi;
	}

	public String getBaoZhengJin() {
		return baoZhengJin;
	}

	public void setBaoZhengJin(String baoZhengJin) {
		this.baoZhengJin = baoZhengJin;
	}

}
