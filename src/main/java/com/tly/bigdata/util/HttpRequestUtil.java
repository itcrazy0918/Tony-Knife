package com.tly.bigdata.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.tly.bigdata.component.Pair;

public class HttpRequestUtil {
    /**
     * 获取Ip
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("http_client_ip");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        // 如果是多级代理，那么取第一个ip为客户ip
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
        }
        return ip;
    }
    
    /**
     * 跟踪request相关操作
     * Copyright (c) 2011-2013 by 广州游爱 Inc.
     * @Author Create by 李兴
     * @Date 2014年10月22日 下午4:10:42
     * @Description
     */
    public static class TraceRequest {
        public static final String KEY_ORIGINAL_URL = ".KEY_ORIGINAL_URL";                      // 请求的原始url
        public static final String KEY_ORIGINAL_QUERY_STRING = ".KEY_ORIGINAL_QUERY_STRING";    // 请求的 原始URL 的请求参数
        public static final String KEY_URIs = ".KEY_URIs";                                      // 请求过程中 forward 过的 uri
        
        /**
         * 获取请求的 原始URI的 ServletPath
         * @param request
         * @return
         */
        public static String getServletPathOfOriginalURI (HttpServletRequest request) {
            String originalURI = getOriginalURI(request);
            if (originalURI == null) {
                return null;
            }
            
            return originalURI.substring(request.getContextPath().length());
        }
        
        /**
         * 获取请求的 原始URI
         * @param request
         * @return
         */
        public static String getOriginalURI (HttpServletRequest request) {
            List<String> uriList = getURIs(request);
            if (uriList.isEmpty()) {
                return null;
            }
            
            return uriList.get(0);
        }
        
        /**
         * 获取请求的 原始URL
         * @param request
         * @return
         */
        public static String getOriginalURL (HttpServletRequest request) {
            return (String) request.getAttribute(KEY_ORIGINAL_URL);
        }
        
        /**
         * 获取请求的 原始URL 的请求参数
         * @param request
         * @return
         */
        public static String getOriginalQueryString (HttpServletRequest request) {
            return (String) request.getAttribute(KEY_ORIGINAL_QUERY_STRING);
        }
        
        /**
         * 获取 request URLs
         *      URLs包括: 原始请求URI, 第1次forward后的URI, 第2次forward后的URI..
         * @param request
         * @return
         */
        public static List<String> getURIs (HttpServletRequest request) {
            @SuppressWarnings("unchecked")
            List<String> uriList = (List<String>) request.getAttribute(KEY_URIs);
            if (uriList == null) {
                uriList = new ArrayList<String> ();
                request.setAttribute(KEY_URIs, uriList);
            }
            
            return uriList;
        }
        
        /**
         * 跟踪 Forward 请求
         * @param request
         * @return
         *      Pair<是否 本次请求链中的首次请求, List<本次请求链中的路径>>
         */
        public static Pair<Boolean, List<String>> traceForwardRequest(HttpServletRequest request) {
            List<String> uriList = getURIs(request);
            
            boolean firstTrace = false; // 是否 本次请求链中的首次请求
            if (uriList.isEmpty()) {
                String currentURL = request.getRequestURL().toString();
                request.setAttribute(KEY_ORIGINAL_URL, currentURL);
                request.setAttribute(KEY_ORIGINAL_QUERY_STRING, request.getQueryString());
                firstTrace = true;
            }
            
            String currentURI = request.getRequestURI();
            uriList.add(currentURI);
            
            return Pair.makePair(firstTrace, uriList);
        }
    }
}
