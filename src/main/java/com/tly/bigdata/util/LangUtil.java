package com.tly.bigdata.util;

import java.util.Collection;


public class LangUtil {
    /**
     * 关闭 一组 AutoCloseable
     * @param objs
     */
    public static void close (AutoCloseable... objs) {
        if (objs.length == 0) {
            return;
        }
        
        for (AutoCloseable obj : objs) {
            if (obj != null) {
                try {
                    obj.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 关闭 一组 AutoCloseable
     * @param c
     */
    public static void close (Collection<? extends AutoCloseable> c) {
        if (c == null || c.isEmpty()) {
            return;
        }
        
        for (AutoCloseable obj : c) {
            if (obj != null) {
                try {
                    obj.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
