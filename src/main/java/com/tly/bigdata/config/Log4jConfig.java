package com.tly.bigdata.config;

import java.net.URL;

import org.apache.log4j.PropertyConfigurator;

public class Log4jConfig {
    private static final String PATH_LOG4J = "common/log4j.properties";
    
    /**
     * 初始化 Log4j
     */
    public synchronized static void init () {
        URL url = Thread.currentThread().getContextClassLoader().getResource(PATH_LOG4J);
        PropertyConfigurator.configure(url);
    }
    
    public static void main(String[] args) {
        init();
        System.out.println( "Log4jConfig be ok" );
    }
}
