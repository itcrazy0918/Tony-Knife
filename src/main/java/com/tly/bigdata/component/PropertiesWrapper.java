package com.tly.bigdata.component;

import com.tly.bigdata.util.DateTimeUtil;

import java.util.Date;
import java.util.Properties;


/**
 *
 * <pre>
 * 属性文件包装工具。
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public class PropertiesWrapper {
    protected Properties properties;

    public PropertiesWrapper(Properties properties) {
        if (properties == null) {
            throw new NullPointerException("PropertiesWrapper.properties must not be null");
        }
        else {
            this.properties = properties;
            return;
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    public String getProperty(String name, String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }
    
    public String getRequiredProperty(String name) {
        String value = properties.getProperty(name);
        if (value == null) {
            throw new IllegalArgumentException("The " + name + " property must be specified");
        }
        
        return value;
    }

    public boolean getBooleanProperty(String name, boolean defaultValue) {
        String value = properties.getProperty(name);
        return value == null ? defaultValue : Boolean.valueOf(value.trim());
    }

    public int getIntProperty(String name, int defaultValue) {
        String value = properties.getProperty(name);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Integer.parseInt(value.trim());
        }
        catch (NumberFormatException e) {
            throw new NumberFormatException("The value of the " + name + " property must be a valid " + "int: \"" + value + "\"");
        }
    }

    public int getRequiredIntProperty(String name) {
        String value = properties.getProperty(name);
        if (value == null) {
            throw new IllegalArgumentException("The " + name + " property must be specified");
        }
        
        try {
            return Integer.parseInt(value.trim());
        }
        catch (NumberFormatException e) {
            throw new NumberFormatException("The value of the " + name + " property must be a valid " + "int: \"" + value + "\"");
        }
    }
    
    /**
     * 读取一个必须存在的 日期属性
     * @param name
     * @param pattern
     * @return
     *      格式, 参考 yyyy-MM-dd HH:mm:ss
     */
    public Date getRequiredDateProperty (String name, String pattern) {
        String value = properties.getProperty(name);
        if (value == null) {
            throw new IllegalArgumentException("The " + name + " property must be specified");
        }
        
        return DateTimeUtil.parseDateTime(value, pattern);
    }
    
    /**
     * 读取一个必须存在的 字符串路径
     *      -- 删除字符串末尾的"文件系统分隔符", 如果存在 
     * @param name
     * @return
     */
    public String getRequiredStringPathProperty (String name) {
        String value = properties.getProperty(name);
        if (value == null) {
            throw new IllegalArgumentException("The " + name + " property must be specified");
        }
        
        while (value.endsWith("\\") || value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);           
        }
        return value;
    }
}
