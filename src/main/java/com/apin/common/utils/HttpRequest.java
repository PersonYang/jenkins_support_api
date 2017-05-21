package com.apin.common.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * Created by Administrator on 2016/1/30.
 */
public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    public static String post(String url, List<NameValuePair> formParams, String charset) throws HttpException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(RequestConfig.DEFAULT);
        String result = null;
        try {
        	httpPost.setHeader("isWeb", "true");
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
    

    
    
    public static String postJson(String url, JsonObject jsonObject,String charset) throws HttpException {
    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    	HttpPost method = new HttpPost(url);
    	method.setConfig(RequestConfig.DEFAULT);
    	String result = null;
    	try {
    		StringEntity  entity = new StringEntity(jsonObject.toString(), charset);
    		entity.setContentEncoding("UTF-8");    
    		entity.setContentType("application/json");
    		method.setHeader("isWeb", "true");
    		method.setEntity(entity);   
    		HttpResponse e = httpClient.execute(method);
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
    public static String postJson(String url, JsonObject jsonObject,String charset,Map<String, String> headerMap) throws HttpException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost method = new HttpPost(url);
        method.setConfig(RequestConfig.DEFAULT);
        String result = null;
        try {
            StringEntity  entity = new StringEntity(jsonObject.toString(), charset);
            entity.setContentEncoding("UTF-8");    
            entity.setContentType("application/json");    
            method.setEntity(entity);   
            method.setHeader("isWeb", "true");
            for (String key : headerMap.keySet()) {  
    		    method.addHeader(key, headerMap.get(key));
    		} 
            HttpResponse e = httpClient.execute(method);
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
    /**
     * @param url
     * @param jsonObject
     * @param charset
     * @param headerMap
     * @return
     * @throws HttpException
     */
    public static String postJson(String url, JSONObject jsonObject,String charset,Map<String, String> headerMap) throws HttpException {
    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    	HttpPost method = new HttpPost(url);
    	method.setConfig(RequestConfig.DEFAULT);
    	String result = null;
    	try {
    		StringEntity  entity = new StringEntity(jsonObject.toString(), charset);
    		entity.setContentEncoding("UTF-8");    
    		entity.setContentType("application/json");    
    		method.setEntity(entity);
    		method.setHeader("isWeb", "true");
    		for (String key : headerMap.keySet()) {  
    		    method.addHeader(key, headerMap.get(key));
    		} 
    		HttpResponse e = httpClient.execute(method);
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
    public static String postJson(String url, JSONObject jsonObject,String charset) throws HttpException {
    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    	HttpPost method = new HttpPost(url);
    	method.setConfig(RequestConfig.DEFAULT);
    	String result = null;
    	try {
    		StringEntity  entity = new StringEntity(jsonObject.toString(), charset);
    		entity.setContentEncoding("UTF-8");    
    		entity.setContentType("application/json");
    		method.setHeader("isWeb", "true");
    		method.setEntity(entity);   
    		HttpResponse e = httpClient.execute(method);
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
    public static String httpGet(String url, String parame,String charset) throws HttpException{
    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        //创建Get请求  
        HttpGet httpGet = new HttpGet(url+"?"+parame);  
        httpGet.setConfig(RequestConfig.DEFAULT);
    	String result = null;
    	try {
    		httpGet.setHeader("isWeb", "true");
    		HttpResponse e = httpClient.execute(httpGet);
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
