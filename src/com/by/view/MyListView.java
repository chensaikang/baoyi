package com.by.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyListView extends ListView {

	public MyListView(Context context) {
		super(context);
		// TODO �Զ����ɵĹ��캯�����
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO �Զ����ɵĹ��캯�����
	}

	public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO �Զ����ɵĹ��캯�����
	}

	// ��д�˷�������ֹ������ͻ
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO �Զ����ɵķ������
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
