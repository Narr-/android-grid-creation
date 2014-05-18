package com.narr.creategrid;

import android.util.Log;

public class Logger {
	private static boolean mIsLogEnable = false;

	private static String AppName;

	public static void init(String appName, boolean isEnable) {
		AppName = appName;
		mIsLogEnable = isEnable;
	}

	public static void d(String tag, String message) {
		if (mIsLogEnable) {
			Log.d(AppName, "[" + tag + "]" + message);
		}
	}

	public static void e(String tag, String message) {
		if (mIsLogEnable) {
			Log.e(AppName, "[" + tag + "]" + message);
		}
	}

	public static void i(String tag, String message) {
		if (mIsLogEnable) {
			Log.i(AppName, "[" + tag + "]" + message);
		}
	}

	public static void d(Object obj, String message) {
		if (mIsLogEnable) {
			Log.d(AppName, "[" + obj.getClass().getSimpleName() + "]" + message);
		}
	}

	public static void e(Object obj, String message) {
		if (mIsLogEnable) {
			Log.e(AppName, "[" + obj.getClass().getSimpleName() + "]" + message);
		}
	}

	public static void i(Object obj, String message) {
		if (mIsLogEnable) {
			Log.i(AppName, "[" + obj.getClass().getSimpleName() + "]" + message);
		}
	}
}