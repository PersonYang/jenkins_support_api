package com.apin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.apin.common.utils.DateUtil;
import com.apin.common.utils.HttpRequest;
import com.apin.common.utils.JsonUtil;
import com.google.gson.JsonObject;

public class AppContent {

	
	public static void main(String[] args) {
		
	  Map<String, Object> hash = new  ConcurrentHashMap<String, Object>();

	  
	  Map<String, Object>  linkedMap = new  LinkedHashMap<String, Object>();
	  Runtime.getRuntime().maxMemory(); 
	  System.out.println("===="+Runtime.getRuntime().maxMemory());
	}
	
	
	
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    public static String post(String url, List<NameValuePair> formParams, String charset) throws HttpException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(RequestConfig.DEFAULT);
        String result = null;

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, charset));
            CloseableHttpResponse e = httpClient.execute(httpPost);
            int statusCode = e.getStatusLine().getStatusCode();
            if (e.getStatusLine().getStatusCode() != 200) {
                throw new HttpException(String.format("HTTP error.Wrong statusCode:%s.", new Object[]{Integer.valueOf(statusCode)}));
            }
            result = EntityUtils.toString(e.getEntity(), charset);
        } catch (UnsupportedEncodingException var17) {
            log.error(var17.getMessage(), var17);
            throw new HttpException("HTTP error", var17);
        } catch (IOException var18) {
            log.error(var18.getMessage(), var18);
            throw new HttpException("HTTP error", var18);
        } catch (Exception var19) {
            log.error(var19.getMessage(), var19);
            throw new HttpException("HTTP error", var19);
        } finally {
            try {
                httpClient.close();
            } catch (IOException var16) {
                log.error(var16.getMessage(), var16);
            }
        }
        return result;
    }
}
