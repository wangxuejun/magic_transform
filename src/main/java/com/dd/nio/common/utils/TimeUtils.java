package com.dd.nio.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {
    /**
     * 比较两个字符串时间大小
     */
    public static int compareTwoTime(String time1, String time2) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        int flagValue = 0;
        try {
            Date date1;
            Date date2;
            date1 = simpleDateFormat.parse(time1);
            date2 = simpleDateFormat.parse(time2);
            long millisecond = date1.getTime() - date2.getTime();
            if (millisecond > 0) {
                flagValue = 1;
            } else if (millisecond < 0) {
                flagValue = -1;
            } else if (millisecond == 0) {
                flagValue = 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (flagValue);
    }

    /**
     * 表两个时间差
     */
    public static int compareTwoTime(Date time1, Date time2) {
        int flagValue = 0;
        try {
            long millisecond = time1.getTime() - time2.getTime();
            if (millisecond > 0) {
                flagValue = 1;
            } else if (millisecond < 0) {
                flagValue = -1;
            } else if (millisecond == 0) {
                flagValue = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (flagValue);
    }

    /**
     * 比较两个时间相差天数
     */
    public static float calculateTimeGapDay(String time1, String time2) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        float day = 0;
        Date date1 = null;
        Date date2 = null;

        try {
            date1 = simpleDateFormat.parse(time1);
            date2 = simpleDateFormat.parse(time2);
            long millisecond = date2.getTime() - date1.getTime();
            day = millisecond / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (day);
    }

    /**
     * 比较两个时间相差天数
     */
    public static float calculateTimeGapDay(Date time1, Date time2) {
        float day = 0;
        try {
            Date date1;
            Date date2;
            date1 = time1;
            date2 = time2;
            long millisecond = date2.getTime() - date1.getTime();
            day = millisecond / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (day);
    }

    /**
     * 比较两个时间相差小时
     */
    public static double calculatetimeGapHour(String time1, String time2) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        double hour = 0;
        try {
            Date date1;
            Date date2;
            date1 = simpleDateFormat.parse(time1);
            date2 = simpleDateFormat.parse(time2);
            double millisecond = date2.getTime() - date1.getTime();
            hour = millisecond / (60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hour;
    }

    /**
     * 比较两个时间相差小时
     */
    public static double calculatetimeGapHour(Date date1, Date date2) {
        double hour = 0;
        double millisecond = date2.getTime() - date1.getTime();
        hour = millisecond / (60 * 60 * 1000);
        return hour;
    }

    /**
     * 比较两个时间相差分钟
     */
    public static double calculatetimeGapMinute(String time1, String time2) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        double minute = 0;
        try {
            Date date1;
            Date date2;
            date1 = simpleDateFormat.parse(time1);
            date2 = simpleDateFormat.parse(time2);
            double millisecond = date2.getTime() - date1.getTime();
            minute = millisecond / (60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return minute;
    }

    /**
     * 比较两个时间相差分钟
     */
    public static double calculatetimeGapMinute(Date date1, Date date2) {
        double minute = 0;
        double millisecond = date2.getTime() - date1.getTime();
        minute = millisecond / (60 * 1000);
        return minute;
    }

    /**
     * 比较两个时间相差秒
     */
    public static double calculatetimeGapSecond(String time1, String time2) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        double second = 0;
        try {
            Date date1;
            Date date2;
            date1 = simpleDateFormat.parse(time1);
            date2 = simpleDateFormat.parse(time2);
            double millisecond = date2.getTime() - date1.getTime();
            second = millisecond / (1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return second;
    }

    /**
     * 比较两个时间相差秒
     */
    public static double calculatetimeGapSecond(Date date1, Date date2) {
        double second = 0;
        double millisecond = date2.getTime() - date1.getTime();
        second = millisecond / (1000);
        return second;
    }

    public static String getLastMonth() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        c.add(Calendar.MONTH, -1);
        String startDate = formatter.format(c.getTime());

        return startDate;
    }

    public static Date strToDate(String str) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = formatter.parse(str);
        return parse;
    }

    public static String dateToString(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = formatter.format(date);
        return format;
    }

    public static Date getNowDateShort(Date date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        Date currentTime = formatter.parse(dateString);
        return currentTime;
    }

    public static Date localToUTC(String localTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date localDate = null;
        try {
            localDate = sdf.parse(localTime);

        } catch (ParseException e) {
            e.printStackTrace();

        }
        long localTimeInMillis = localDate.getTime();
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(localTimeInMillis);

        int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);

        int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
        calendar.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        Date utcDate = new Date(calendar.getTimeInMillis());
        return utcDate;
    }

    public static void dateToUtc(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
}
