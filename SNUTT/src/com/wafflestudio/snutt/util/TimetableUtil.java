package com.wafflestudio.snutt.util;

public class TimetableUtil {
	
	public static int wdayToNumber(String wday){
		if (wday.equals("월")) return 0;
		if (wday.equals("화")) return 1;
		if (wday.equals("수")) return 2;
		if (wday.equals("목")) return 3;
		if (wday.equals("금")) return 4;
		if (wday.equals("토")) return 5;
		return -1;
	}
	public static String zeroStr(int number){
		if (number < 10) return "0" + number;
		return "" + number;
	}
	public static String getMyLecturesPrefKey(){
		int year = SharedPrefUtil.getInstance().getInt(SharedPrefUtil.PREF_KEY_CURRENT_YEAR);
		String semester = SharedPrefUtil.getInstance().getString(SharedPrefUtil.PREF_KEY_CURRENT_SEMESTER);
		
		return "MY_LECTURES_" + year + "_" + semester;
	}
}
