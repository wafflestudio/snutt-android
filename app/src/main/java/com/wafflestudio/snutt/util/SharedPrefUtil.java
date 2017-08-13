package com.wafflestudio.snutt.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtil {
	
	private Context mContext;
	private String mName;
	private final static String PREF_NAME_COMMON = "common_pref";
	
	public static String PREF_KEY_SUGANG_TIMESTAMP = "SUGANG_TIMESTAMP";
	
	public static String PREF_KEY_CURRENT_YEAR = "CURRENT_YEAR";
	public static String PREF_KEY_CURRENT_SEMESTER = "CURRENT_SEMESTER";
	
	private SharedPrefUtil(Context ctx, String name) {
		super();
		mContext = App.getAppContext();
		mName = name;
	}
	
	public static SharedPrefUtil getInstance(){
		return new SharedPrefUtil(App.getAppContext(), PREF_NAME_COMMON);
	}
	
	private SharedPreferences getPref(){
		int currentApiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentApiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
			return mContext.getSharedPreferences(mName, Context.MODE_MULTI_PROCESS);
		} else {
			return mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
		}
	}
	
	public String getString(String key) {
		SharedPreferences mSharedPref = getPref();
		String value = mSharedPref.getString(key, null);
		return value;
	}
	
	public Boolean getBoolean(String key) {
		SharedPreferences mSharedPref = getPref();
		boolean value = mSharedPref.getBoolean(key, false);
		return value;
	}
	
	public int getInt(String key) {
		SharedPreferences mSharedPref = getPref();
		int value = mSharedPref.getInt(key, 0);
		return value;
	}
	
	public long getLong(String key) {
		SharedPreferences mSharedPref = getPref();
		long value = mSharedPref.getLong(key, 0);
		return value;
	}
	
	public void setString(String key, String value) {
		SharedPreferences mSharedPref = getPref();
		SharedPreferences.Editor editor = mSharedPref.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public void setBoolean(String key, boolean value) {
		SharedPreferences mSharedPref = getPref();
		SharedPreferences.Editor editor = mSharedPref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public void setInt(String key, int value) {
		SharedPreferences mSharedPref = getPref();
		SharedPreferences.Editor editor = mSharedPref.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public void setLong(String key, long value) {
		SharedPreferences mSharedPref = getPref();
		SharedPreferences.Editor editor = mSharedPref.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public void remove(String key) {
		SharedPreferences mSharedPref = getPref();
		SharedPreferences.Editor editor = mSharedPref.edit();
		editor.remove(key);
		editor.commit();
	}
	
	public void removeAll() {
		SharedPreferences mSharedPref = getPref();
		SharedPreferences.Editor editor = mSharedPref.edit();
		editor.clear();
		editor.commit();
	}
}
