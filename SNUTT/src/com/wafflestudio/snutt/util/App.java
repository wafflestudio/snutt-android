package com.wafflestudio.snutt.util;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class App extends Application{
	private static Context context;
	public static String appName = "com.wafflestudio.snutt";

	public void onCreate(){
		super.onCreate();
		App.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return App.context;
	}
	
	public static int getAppVersion(){
		PackageInfo pInfo;
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pInfo.versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}
	}

	//dp to px
	public static float dpTopx(float dp){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi/160f);
		return px;
	}
	//px to dp
	public static float pxTodp(float px){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}


	//sp to px
	public static float spTopx(float sp) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return sp*scaledDensity;
	}

	//px to sp
	public static float pxTosp(float px) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return px/scaledDensity;
	}
}
