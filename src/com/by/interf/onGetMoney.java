package com.by.interf;

import com.by.javabean.SteelBean;

public interface onGetMoney {

	public void getMoney(SteelBean bean, int i);

	public void getAllMoney(boolean flag);

	// public void setAllSelectState(boolean state,int select);
	public void setAllSelectState(boolean state);

	public void onLayoutShow(boolean flag);
}
