package com.fada.sellsteward.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ranhao on 2016/7/6.
 */
public class MyDateUtils {

    public static String formatTime(long lefttime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",Locale.CHINA);
        String sDateTime = sdf.format(lefttime); // 得到精确到秒的表示：08/31/2006
        // 21:08:00
        return sDateTime;
    }

    public static String formatDateAndTime(long lefttime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA);
        String sDateTime = sdf.format(lefttime); // 得到精确到秒的表示：08/31/2006
        return sDateTime;
    }


    public static long formatToLong(String time,String template) {
        SimpleDateFormat sdf = new SimpleDateFormat(template, Locale.CHINA);
        try {
            Date d = sdf.parse(time);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            long l = c.getTimeInMillis();
            return l;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int formatYear(long lefttime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy",Locale.CHINA);
        String sDateTime = sdf.format(lefttime); // 得到精确到秒的表示：08/31/2006
        int i = Integer.parseInt(sDateTime);
        return i;
    }

    public static int formatMonth(long lefttime) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM",Locale.CHINA);
        String sDateTime = sdf.format(lefttime); // 得到精确到秒的表示：08/31/2006
        int i = Integer.parseInt(sDateTime);
        return i;
    }

}
