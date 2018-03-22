package com.tly.bigdata.util;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 *
 * <pre>
 * 检查辅助工具。
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public class CheckUtil {
    /**
     * 检查 SQL 注入问题
     * @param sql
     */
    public static void checkSQLInject (String sql) {
        // TODO 检查 SQL 注入问题
    }
    
    /**
     * 检查一组开始/结束日期是否合法
     * @param startDate
     * @param endDate
     */
    public static void checkStartAndEndDate (Date startDate, Date endDate) {
        checkNotNull("startDate", startDate);
        checkNotNull("endDate", endDate);
        
        if (startDate.getTime() > endDate.getTime()) {
            String msg = String.format("startDate[%s] must be <= endDate[%s]", 
                    DateTimeUtil.formatDateTime(startDate),
                    DateTimeUtil.formatDateTime(endDate));
            throw new IllegalArgumentException(msg);
        }
    }
    
    /**
     * 检查 Collection 是否为 null/空Collection
     * @param name
     * @param c
     */
    public static <T> void checkNotNullAndEmpty(String name, Collection<T> c) {
        if (c == null) {
            throw new NullPointerException(name + " can not be null");
        }
        if (c.isEmpty()) {
            throw new IllegalArgumentException(name + " can not be empty");
        }
    }
    
    /**
     * 检查 String数组 是否为 null/空数组
     * @param name
     * @param arr
     */
    public static <T> void checkNotNullAndEmpty(String name, T[] arr) {
        if (arr == null) {
            throw new NullPointerException(name + " can not be null");
        }
        if (arr.length == 0) {
            throw new IllegalArgumentException(name + " can not be empty");
        }
    }
    
    /**
     * 检查 int数组 是否为 null/空数组
     * @param name
     * @param arr
     */
    public static void checkNotNullAndEmpty(String name, int[] arr) {
        if (arr == null) {
            throw new NullPointerException(name + " can not be null");
        }
        if (arr.length == 0) {
            throw new IllegalArgumentException(name + " can not be empty");
        }
    }
    
    /**
     * 检查 Map 是否为 null/空Map
     * @param name
     * @param map
     */
    public static <K,V > void checkNotNullAndEmpty(String name, Map<K, V> map) {
        if (map == null) {
            throw new NullPointerException(name + " can not be null");
        }
        if (map.isEmpty()) {
            throw new IllegalArgumentException(name + " can not be empty");
        }
    }

    /**
     * 检查是否为空
     * 
     * @param name
     * @param value
     */
    public static <T> void checkNotNull(String name, T value) {
        if (value == null) {
            throw new NullPointerException(name + " can not be null");
        }
    }
    
    /**
     * 是否为正数(>0)
     * @param name
     * @param num
     */
    public static void checkPositiveNumber (String name, int num) {
        if (num <= 0) {
            throw new RuntimeException(name + " 必须为 > 0"); 
        }
    }
    
    /**
     * 是否为非负数(>=0)
     * @param name
     * @param num
     */
    public static void checkNotNegativeNumber (String name, int num) {
        if (num < 0) {
            throw new RuntimeException(name + " 必须为 >= 0"); 
        }
    }

    public static void main(String[] args) {
        checkNotNullAndEmpty("CheckUtil.main.testValue", new String[]{});
        checkNotNullAndEmpty("CheckUtil.main.testValue", new Object[]{});
    }
}
