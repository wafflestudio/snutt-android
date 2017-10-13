package com.wafflestudio.snutt;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

import com.wafflestudio.snutt.model.TagType;

/**
 * Created by makesource on 2016. 1. 24..
 */
public class SNUTTUtils {

    public static Context context;

    public static int wdayToNumber(String wday){
        if (wday.equals("월")) return 0;
        if (wday.equals("화")) return 1;
        if (wday.equals("수")) return 2;
        if (wday.equals("목")) return 3;
        if (wday.equals("금")) return 4;
        if (wday.equals("토")) return 5;
        if (wday.equals("일")) return 6;
        return -1;
    }

    public static String numberToWday(int wday){
        switch (wday){
            case 0: return "월";
            case 1: return "화";
            case 2: return "수";
            case 3: return "목";
            case 4: return "금";
            case 5: return "토";
            case 6: return "일";
        }
        return null;
    }

    public static String numberToTime(float num) {

        int hour =  8 + (int) num;
        String minute;

        if ( Math.floor(num) == num ) minute = "00";
        else minute = "30";

        String time = String.valueOf(hour) + ":" + minute;
        return time;
    }

    public static String[] getTimeList(int from, int to) {
        String[] list = new String[to-from+1];
        for (int i=from;i<=to;i++) list[i-from] = numberToTime(i / 2f);
        return list;
    }

    public static String zeroStr(int number){
        if (number < 10) return "0" + number;
        return "" + number;
    }

    //dp to px
    public static float dpTopx(float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
    //px to dp
    public static float pxTodp(float px){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static float getDisplayWidth() {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static float getDisplayHeight() {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getTagColor(TagType type) {
        switch (type) {
            case ACADEMIC_YEAR:
                return Color.rgb(229, 68, 89);
            case CLASSIFICATION:
                return Color.rgb(245, 141, 61);
            case CREDIT:
                return Color.rgb(166, 217, 48);
            case DEPARTMENT:
                return Color.rgb(27, 208, 200);
            case INSTRUCTOR:
                return Color.rgb(29, 153, 232);
            case CATEGORY:
                return Color.rgb(175, 86, 179);
        }
        return Color.RED;
    }
}
