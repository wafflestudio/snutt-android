package com.wafflestudio.snutt_staging;

import java.util.regex.Pattern;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by makesource on 2016. 1. 24..
 */
public class SNUTTUtils {

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

    public static String getBodyString(Response response) {
        if (response != null) {
            String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
            return bodyString;
        }
        return null;
    }

    public static boolean checkId(String id) {
        return Pattern.matches("^[a-z0-9]{4,32}$", id);
    }

    public static boolean checkPassword(String password) {
        return Pattern.matches("^(?=.*\\d)(?=.*[a-z])\\S{6,20}$", password);
    }
}
