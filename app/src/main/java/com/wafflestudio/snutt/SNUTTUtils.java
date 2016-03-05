package com.wafflestudio.snutt;

import java.io.IOException;

import retrofit.client.Response;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

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
        }
        return null;
    }

    public static int numberToTime(String time) {
        return 0;
    }

    public static String numberToTime(int num) {
        int hour =  8 + num / 2;
        String minute;

        if (num%2==1) minute = "30";
        else minute = "00";

        String time = String.valueOf(hour) + ":" + minute;
        return time;
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
}
