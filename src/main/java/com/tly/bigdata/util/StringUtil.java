package com.tly.bigdata.util;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class StringUtil {
    public final static String UNICODE_CN_ZH = "\\u4e00-\\u9fa5";      // 中文 - 简体    
    public final static String REGEX_NUMBER = "^(-?\\d+)(\\.\\d+)?$";           // 正则 -- 数字
    public final static String REGEX_PERCENTAGE = "^(-?\\d+)(\\.\\d+)?\\%$";    // 正则 -- 百分比
    
    /**
     * 如果 str 为null或空字符串，则返回 defValue
     * @param str
     * @param defValue
     * @return
     */
    public static String getString (String str, String defValue) {
        if (isNullOrEmpty(str, true)) {
            return defValue;
        }
        return str;
    }
    
    /**
     * 是否数字
     * @param value
     * @return
     */
    public static boolean isNumber (String value) {
        return value.matches(REGEX_NUMBER);
    }
    
    /**
     * 是否百分比
     * @param value
     * @return
     */
    public static boolean isPercentage (String value) {
        return value.matches(REGEX_PERCENTAGE);
    }
    
    /**
     * 字符串是否为 null or empty
     *      1) 不会对 str 做 trim
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty (String str) {
        return str == null || str.isEmpty();
    }
    
    /**
     * 字符串是否为 null or empty
     * @param str
     * @param strict
     *      true:   会对 str 做 trim
     *      false:  不会对 str 做 trim
     * @return
     */
    public static boolean isNullOrEmpty (String str, boolean strict) {
        if (str == null) {
            return true;
        }
        
        if (strict) {
            str = str.trim();
        }
        
        return str.isEmpty();
    }
    
    /**
     * 首字母大写
     * @param clazz
     * @return
     */
    public static String upperCaseFirstLetter (String name) {
        if (name.length() == 1) {
            return name.toUpperCase();
        }
        
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
    
    /**
     * 首字母小写
     * @param name
     * @return
     */
    public static String lowerCaseFirstLetter (String name) {
        if (name.length() == 1) {
            return name.toLowerCase();
        }
        
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
    
    /**
     * 是否包含中文简体字符
     * @param text
     * @return
     */
    public static boolean hasCnZHWords (String text) {
        Pattern p = Pattern.compile("[" + UNICODE_CN_ZH + "]+");
        Matcher m = p.matcher(text);  
        return m.find();
    }
    
    /**
     * 读取字符串里面的中文单词
     * @param text
     * @return
     */
    public static List<String> readCnWords (String text) {
        List<String> list = Lists.newArrayList();
        if (text == null) {
            return list;
        }        
        
        List<String> unholyRegexList = Lists.newArrayList("<color=#[\\d%s]{2,6}>.*?</color>", "<a href=\\{%s\\}>.*?</a>"); // 修饰性词组(变态的策划或者技术定义的规则)
        final String additionRegex = "\\(\\)\\d，。；：！“”‘’:,%\\w";                       // 如果和中文词组在一起，就保留; 否则就不保留        
        final String UNICODE_CN_ZH = "\\u4e00-\\u9fa5";       // 中文词组，必须保留的
        
        // 处理 修饰性词组 -- 提取
        Map<String, String> unholyMap = Maps.newHashMap();
        int unholyIndex = 0;
        for (String unholyRegex : unholyRegexList) {
            Pattern p = Pattern.compile(unholyRegex);
            Matcher m = p.matcher(text);  
            while (m.find()) {
                unholyMap.put("(((" + (++unholyIndex) + ")))", m.group(0));
            }
        }
        // 处理 修饰性词组 -- 用特殊词组替换 (最后还要替换回来的)
        for (Map.Entry<String, String> entry : unholyMap.entrySet()) {
            text = text.replace(entry.getValue(), entry.getKey());
        }
        
        // 提取中文词组
        Pattern p = Pattern.compile("[" + UNICODE_CN_ZH + additionRegex + "]+");
        Matcher m = p.matcher(text);  
        while (m.find()) {
            list.add( m.group(0) );
        }
        
        // 剔除 单独存在的 additionRegex
        p = Pattern.compile("^[" + additionRegex + "]+$");
        for (int i = list.size() - 1; i >= 0; i--) {
            String item = list.get(i);
            if (p.matcher(item).find()) {
                list.remove(i);
            }
        }
        
        // 替换回 修饰性词组
        for (int i = 0; i < list.size(); i++) {
            String item = list.get(i);
            for (Map.Entry<String, String> entry : unholyMap.entrySet()) {
                item = item.replace(entry.getKey(), entry.getValue());
            }
            list.set(i, item);
        }
        
        return list;
    }
    
    public static void main(String[] args) {
        System.out.println( hasCnZHWords("2哈2") );
        System.out.println( hasCnZHWords("123") );
        
        System.out.println( "Done.." );
    }
}
