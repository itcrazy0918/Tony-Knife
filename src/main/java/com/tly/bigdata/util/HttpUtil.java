package com.tly.bigdata.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
    static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    
    /**
     * HTTP Post 提交 JSON 格式请求
     *      1) request/response 均是 UTF-8编码
     *      2) 默认 6000毫秒超时( 连接 1500毫秒 + 读取数据 4500毫秒)
     * @param url
     * @param json
     * @return
     *      null 标志出错了
     */
    public static String httpPostJSON(final String url, String json) {
        return httpPostJSON(url, json, 1500, 4500);
    }
    
    /**
     * HTTP Post 提交 JSON 格式请求
     *      1) request/response 均是 UTF-8编码
     * @param url
     * @param json
     * @param connectTimeout    
     *      设置连接超时时间，单位毫秒
     * @param socketTimeout
     *      请求获取数据的超时时间，单位毫秒
     * @return
     *      null 标志出错了
     */
    public static String httpPostJSON(final String url, String json, int connectTimeout, int socketTimeout) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // json UTF-8
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            // 配置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(socketTimeout)        // 请求获取数据的超时时间，单位毫秒
                    .setConnectTimeout(connectTimeout)      // 设置连接超时时间，单位毫秒
                    .setConnectionRequestTimeout(2000)      // 设置从connect Manager获取Connection 超时时间，单位毫秒
                    .build();
            
            ResponseHandler<String> handler = new SimpleResponseHandler(url);
            
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);
            httpPost.setConfig(requestConfig);
            return httpclient.execute(httpPost, handler);
        }
        catch (Exception e) {
            String msg = String.format("httpPostJSON Exception: url=%s, msg=%s", url, e.getMessage());
            logger.error(msg, e);
            return null;
        }
        finally {
            FileUtil.close(httpclient);
        }
    }
    
    /**
     * HTTP Post 提交 Form表单 格式请求
     *      1) request/response 均是 UTF-8编码
     *      2) 默认 6000毫秒超时( 连接 1500毫秒 + 读取数据 4500毫秒)
     * @param url
     * @param paramMap
     * @return
     *      null 标志出错了
     */
    public static String httpPostForm(final String url, Map<String, String> paramMap) {
        return httpPostForm(url, paramMap, 1500, 4500);
    }
    
    /**
     * HTTP Post 提交 Form表单 格式请求
     *      1) request/response 均是 UTF-8编码
     * @param url
     * @param paramMap
     * @param connectTimeout    
     *      设置连接超时时间，单位毫秒
     * @param socketTimeout
     *      请求获取数据的超时时间，单位毫秒
     * @return
     *      null 标志出错了
     */
    public static String httpPostForm(final String url, Map<String, String> paramMap, int connectTimeout, int socketTimeout) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // form UTF-8
            List<NameValuePair> requestParams = new ArrayList <NameValuePair>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                requestParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));    
            }            
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(requestParams, Consts.UTF_8);
            // 配置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(socketTimeout)        // 请求获取数据的超时时间，单位毫秒
                    .setConnectTimeout(connectTimeout)      // 设置连接超时时间，单位毫秒
                    .setConnectionRequestTimeout(2000)      // 设置从connect Manager获取Connection 超时时间，单位毫秒
                    .build();
            
            ResponseHandler<String> handler = new SimpleResponseHandler(url);
            
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);
            httpPost.setConfig(requestConfig);
            return httpclient.execute(httpPost, handler);
        }
        catch (Exception e) {
            String msg = String.format("httpPostForm Exception: url=%s, msg=%s", url, e.getMessage());
            logger.error(msg, e);
            return null;
        }
        finally {
            FileUtil.close(httpclient);
        }
    }
    
    public static void main(String[] args) {
        String url = "http://10.21.210.192:8081/game/my.jsp";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("a", "101");
        paramMap.put("b", "222");
        System.out.println( httpPostJSON(url, GsonUtil.toJson(paramMap)) );
        System.out.println( httpPostForm(url, paramMap) );
        System.out.println( "=============================" );
    }
}

/**
 * 只是 HTTP状态码为200的时候才会生成返回文本
 * @author Administrator
 *
 */
class SimpleResponseHandler implements ResponseHandler<String> {
    private final String url;
    
    SimpleResponseHandler(String url) {
        this.url = url;
    }
    
    @Override
    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, Consts.UTF_8);
        }
        else {
            HttpUtil.logger.debug("SimpleResponseHandler: url={}, statusCode={}", this.url, statusCode);
            return null;
        }
    }
}