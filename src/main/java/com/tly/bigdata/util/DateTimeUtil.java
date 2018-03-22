package com.tly.bigdata.util;


import com.tly.bigdata.exception.CommonRuntimeException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * <pre>
 * 日期时间辅助工具。
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public class DateTimeUtil {
    public final static int SECOND_IN_MINUTE = 60;                              // 1分钟的秒数
    public final static int SECOND_IN_HOUR = 60 * SECOND_IN_MINUTE;             // 1小时的秒数
    public final static int SECOND_IN_DAY = 24 * SECOND_IN_HOUR;                // 1天的秒数
    public final static int SECOND_IN_WEEK = 7 * SECOND_IN_DAY;                 // 1周的秒数
    
    public final static long MILLISECOND_IN_MINUTE = 1000L * SECOND_IN_MINUTE;  // 1小时的毫秒数
    public final static long MILLISECOND_IN_HOUR = 1000L * SECOND_IN_HOUR;      // 1小时的毫秒数
    public final static long MILLISECOND_IN_DAY = 1000L * SECOND_IN_DAY;        // 1天的毫秒数
    public final static long MILLISECOND_IN_WEEK = 1000L * SECOND_IN_WEEK;      // 1天的毫秒数
    
    /**
     * 计算 毫秒数
     * @param day
     * @param hour
     * @param minute
     * @return
     */
    public static long millis (int day, int hour, int minute) {
        return day * MILLISECOND_IN_DAY
                + hour * MILLISECOND_IN_HOUR
                + minute * MILLISECOND_IN_MINUTE;
    }
    
    /**
     * 计算 秒数
     * @param day
     * @param hour
     * @param minute
     * @return
     */
    public static long seconds (int day, int hour, int minute) {
        return 1L * day * SECOND_IN_DAY
                + 1L * hour * SECOND_IN_HOUR
                + 1L * minute * SECOND_IN_MINUTE;
    }

    private static final int[] monthes = {
            Calendar.JANUARY,
            Calendar.FEBRUARY,
            Calendar.MARCH,
            Calendar.APRIL,
            Calendar.MAY,
            Calendar.JUNE,
            Calendar.JULY,
            Calendar.AUGUST,
            Calendar.SEPTEMBER,
            Calendar.OCTOBER,
            Calendar.NOVEMBER,
            Calendar.DECEMBER
    };

    /**
     * 获取Calendar里面的月份
     * 
     * @return
     */
    public static int[] getMonthesOfCalendar() {
        return monthes;
    }

    /**
     * 获取当前时间 -- 相对于1970的秒数
     * 
     * @return
     */
    public static int getCurrentTimeSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * 解析DateTime -- Time格式是未知的 -- 无视 毫秒
     * 
     * @param text
     * @return
     */
    public static Date parseDateTime4UnknownTimeFormat(String text) {
        if (text == null) {
            return null;
        }

        text = text.trim();
        if (text.isEmpty()) {
            return null;
        }

        if (text.endsWith(":")) {
            text = text.substring(0, text.length() - 1);
        }

        String[] dt = text.split(" ");

        // 解析日期
        String[] arrDate = dt[0].split("-");
        int year = Integer.parseInt(arrDate[0]);
        int month = Integer.parseInt(arrDate[1]);
        int day = Integer.parseInt(arrDate[2]);

        // 解析时间
        int hour = 0, minute = 0, second = 0;
        if (dt.length >= 2) {
            String[] arrTime = dt[1].split(":");
            hour = Integer.parseInt(arrTime[0]);
            if (arrTime.length >= 2) {
                minute = Integer.parseInt(arrTime[1]);
            }
            if (arrTime.length >= 3) {
                second = Integer.parseInt(arrTime[2]);
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }
    
    /**
     * 解析日期格式(yyyy-MM-dd)
     * @param text
     * @return
     */
    public static Date parseDate(String text) {
        return parseDateTime(text, "yyyy-MM-dd");
    }
    
    /**
     * 解析时间格式(HH:mm:ss)
     * @param text
     * @return
     */
    public static Date parseTime(String text) {
        return parseDateTime(text, "HH:mm:ss");
    }
    
    /**
     * 解析日期时间格式(yyyy-MM-dd HH:mm:ss)
     * @param text
     * @return
     */
    public static Date parseDateTime(String text) {
        return parseDateTime(text, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 解析日期时间格式
     * 
     * @param text
     * @param pattern
     *            格式, 参考 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date parseDateTime(String text, String pattern) {
        if (text == null) {
            return null;
        }

        text = text.trim();
        if (text.isEmpty()) {
            return null;
        }

        try {
            return new SimpleDateFormat(pattern).parse(text);
        }
        catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取endDate距离startDate多少天 -- startTimeInMillis 当天为第一天 -- startTimeInMillis
     * 必须<= endTimeInMillis
     * 
     * @param startTimeInMillis
     * @param endTimeInMillis
     * @return
     */
    public static int getDaysFromStartTime(long startTimeInMillis, long endTimeInMillis) {
        if (startTimeInMillis > endTimeInMillis) {
            throw new RuntimeException("getDaysFromStartDate error. startTimeInMillis=" + startTimeInMillis + " must be <= endTimeInMillis=" + endTimeInMillis);
        }

        // 结束时间
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(endTimeInMillis);
        fillTimeToCalendar(cal, 0, 0, 0, 0);
        long end = cal.getTimeInMillis();

        // 开始时间
        cal.setTimeInMillis(startTimeInMillis);
        fillTimeToCalendar(cal, 0, 0, 0, 0);
        long start = cal.getTimeInMillis();

        return (int) ((end - start) / MILLISECOND_IN_DAY) + 1;
    }

    /**
     * 格式化 Calendar (yyyy-MM-dd HH:mm:ss)
     * 
     * @param cal
     * @return
     */
    public static String formatCalendar(Calendar cal) {
        return String.format("%4d-%02d-%02d %02d:%02d:%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
    }

    /**
     * 格式化 日期时间 yyyy-MM-dd HH:mm:ss
     * 
     * @param date
     * @return
     */
    public static String formatDateTime(Date date) {
        return formatDateTime(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 格式化 日期 yyyy-MM-dd
     * 
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return formatDateTime(date, "yyyy-MM-dd");
    }

    /**
     * 格式化 日期
     * 
     * @param date
     * @param plusDays
     * @return
     */
    public static String formatDate(Date date, int plusDays) {
        return formatDate(new Date(date.getTime() + DateTimeUtil.MILLISECOND_IN_DAY * plusDays));
    }

    /**
     * 格式化 时间 HH:mm:ss
     * 
     * @param date
     * @return
     */
    public static String formatTime(Date date) {
        return formatDateTime(date, "HH:mm:ss");
    }

    /**
     * 格式化 日期时间
     * 
     * @param date
     * @param pattern
     *            格式, 参考 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String formatDateTime(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 重新填写Calendar的时间
     * 
     * @param cal
     * @param hour
     * @param minute
     * @param second
     * @param millisecond
     * @return
     */
    public static Calendar fillTimeToCalendar(Calendar cal, int hour, int minute, int second, int millisecond) {
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal;
    }

    /**
     * 重新填写Calendar日期
     * 
     * @param cal
     * @param year
     * @param month
     *            自然月，从1开始
     * @param day
     * @return
     */
    public static Calendar fillDateToCalendar(Calendar cal, int year, int month, int day) {
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal;
    }

    /**
     * 增加days到Calendar
     * 
     * @param cal
     * @param days
     * @return
     */
    public static Calendar plusDaysToCalendar(Calendar cal, int days) {
        cal.set(Calendar.DAY_OF_MONTH, days);
        return cal;
    }
    
    /**
     * 抹去 时/分/秒/毫秒
     * @param date
     * @return
     */
    public static Date trimTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTime();
    }

    /**
     * 抹去毫秒 (毫秒设置为0)
     * 
     * @param cal
     * @return
     */
    public static Calendar trimMillisecondOfCalendar(Calendar cal) {
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * 是否同一天
     * 
     * @param t1
     * @param t2
     * @return
     */
    public static boolean isInSameDay(long t1, long t2) {
        if (t1 == t2) {
            return true;
        }

        long min = t1, max = t2;
        if (min > max) {
            long t = max;
            max = min;
            min = t;
        }

        // max 所在天的凌晨 00:00:00.0
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(max);
        fillTimeToCalendar(cal, 0, 0, 0, 0);
        long midnightOfMax = cal.getTimeInMillis();

        return min >= midnightOfMax;
    }

    /**
     * 是否在今天
     * 
     * @param t
     * @return
     */
    public static boolean isInToday(long t) {
        Calendar cal = Calendar.getInstance();
        // 今天 凌晨 00:00:00.0
        fillTimeToCalendar(cal, 0, 0, 0, 0);
        long midnight = cal.getTimeInMillis();
        if (t < midnight) {
            return false;
        }

        // 明天 凌晨 00:00:00.0
        long midnightOfTomorrow = midnight + 1000L * 3600 * 24;
        if (t >= midnightOfTomorrow) {
            return false;
        }

        return true;
    }

    /**
     * 距离 下个时间点的 毫秒数 (如果当前时间等于指定时间点，则取次日的)
     * 
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static long getMillisToNextClock(int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        final long nowMillis = cal.getTimeInMillis();

        fillTimeToCalendar(cal, hour, minute, second, 0);
        long nextClockMillis = cal.getTimeInMillis();
        if (nextClockMillis <= nowMillis) {
            nextClockMillis += MILLISECOND_IN_DAY;
        }

        return nextClockMillis - nowMillis;
    }

    /**
     * 获取下个周N
     * 
     * @param cal
     * @param dayOfWeek
     * @param excludeToday
     *            是否包括今天
     * @return
     */
    public static long getNextDayOfWeek(Calendar cal, int dayOfWeek, boolean excludeToday) {
        if (excludeToday) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        for (int i = 0; i < 7; i++) {
            if (cal.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                return cal.getTimeInMillis();
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        throw new CommonRuntimeException("getNextDayOfWeek illegal param.dayOfWeek=" + dayOfWeek);
    }

    /**
     * 获取上个周N
     * 
     * @param cal
     * @param dayOfWeek
     * @param excludeToday
     * @return
     */
    public static long getLastDayOfWeek(Calendar cal, int dayOfWeek, boolean excludeToday) {
        if (excludeToday) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        for (int i = 0; i < 7; i++) {
            if (cal.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                return cal.getTimeInMillis();
            }
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        throw new CommonRuntimeException("getLastDayOfWeek illegal param.dayOfWeek=" + dayOfWeek);
    }

    /**
     * 获取指定月份的天数
     * 
     * @param year
     * @param month
     * @return
     */
    public static int getDayCountOfMonth(int year, int monthOfCalendar) {
        // 指定月份的1号
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfCalendar);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        // 下个月1号
        cal.add(Calendar.MONTH, 1);

        // 下个月1号的前一天
        cal.add(Calendar.DAY_OF_MONTH, -1);

        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 是否周末
     * 
     * @param date
     * @return
     */
    public static boolean isWeekend(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY;
    }

    private static volatile Map<String, ThreadLocal<DateFormat>> dateFormatMap;

    /**
     * 创建单例 没有使用单例模式的话,spring在注入数据时如果有逻辑调用了getDateFormat(pattern),会因为dateFormatMap尚未初始化而报NullPoint
     * 
     * @return
     */
    private static Map<String, ThreadLocal<DateFormat>> getDateFormatMap() {
        if (dateFormatMap == null) {
            synchronized (DateTimeUtil.class) {
                if (dateFormatMap == null) {
                    dateFormatMap = new ConcurrentHashMap<>();
                }
            }
        }
        return dateFormatMap;
    }

    /**
     * 获取线程安全的DateFormat SimpleDateFormat有两个问题:
     * 1.非线程安全,仅仅是声明为static,使用时不上锁在并发状况下调用parse()有可能得到错误的时间 
     * 2.频繁实例化有可能导致内存溢出使用ThreadLocal将DateFormat变为线程独享,既可以避免并发问题,又可以减少反复创建实例的开销
     * 
     * @param pattern
     * @return
     */
    public static DateFormat getDateFormat(final String pattern) {
        ThreadLocal<DateFormat> dateFormat = getDateFormatMap().get(pattern);
        if (dateFormat == null) {
            dateFormat = new ThreadLocal<DateFormat>() {
                @Override
                protected DateFormat initialValue() {
                    return new SimpleDateFormat(pattern);
                }
            };
            dateFormatMap.put(pattern, dateFormat);
        }
        return dateFormat.get();
    }

    /**
     * 时间转换字符串
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String date2String(Date date, String pattern) {
        return getDateFormat(pattern).format(date);
    }

    /**
     * 字符串转换时间
     * 
     * @param string
     * @param pattern
     * @return
     */
    public static Date string2Date(String string, String pattern) {
        try {
            return getDateFormat(pattern).parse(string);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("无法将字符串[" + string + "]按格式[" + pattern + "]转换为日期", e);
        }
    }

    /**
     * 给一个时间增加一个固定时间
     * 
     * @param source
     * @param hours
     * @param minutes
     * @param second
     * @return
     */
    public static Date addTime(Date source, int hours, int minutes, int second) {
        if (source == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(source);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        cal.add(Calendar.MINUTE, minutes);
        cal.add(Calendar.MILLISECOND, second);
        return cal.getTime();
    }

    /**
     * 增加 amount 天
     * @param date
     * @param amount
     * @return
     */
    public static Date addDays(Date date, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, amount);
        return cal.getTime();
    }

    public static void main(String[] args) {
        Date today = new Date();
        Date yesterday = new Date(today.getTime() - 1000L * 3600 * 24);
        System.out.println(getDaysFromStartTime(yesterday.getTime(), today.getTime()));
        System.out.println(getDaysFromStartTime(today.getTime(), today.getTime()));
        // System.out.println( getDaysFromStartTime(today.getTime(),
        // yesterday.getTime()));
    }
}
