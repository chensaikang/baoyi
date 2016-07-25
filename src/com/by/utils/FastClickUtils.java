package com.by.utils;

/**
 * 
 * @author °´Å¥µÄ±©Á¦²âÊÔ
 * 
 */
public class FastClickUtils {

	private static long lastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		if (time - lastClickTime < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
}
