package com.apin.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import net.sf.json.JSONArray;

/**
 * @author Young
 * json 处理的方法
 * @version 0.0.1
 * @date
 */
public class JsonUtil {

	/**
	 * @param param  json中的key
	 * @param paramJson json字符串
	 * @return 取得key对应的value
	 */
	public static String getJsonStr(String param,String paramJson){
		try {
			if(paramJson.indexOf(param) != -1){
				JSONObject jsonObject = new JSONObject(paramJson);
				return jsonObject.get(param).toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return paramJson;
	}
	/**
	 * @param paramJson json字符串
	 * @return json字符串
	 */
	public static String getJsonStr(String paramJson){
		try {
			JSONObject jsonObject = new JSONObject(paramJson);
			return jsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return paramJson;
	}
	
	
	/**
	 * 
	 * 将返回的jsonStr parseToMap 
	 * @author Young
	 * @param flightInfo 返回的jsonStr
	 * @return map
	 */
	public static Map<String, String> parseToMap(String paramJson){
		Map<String, String> map = new HashMap<String, String>();
		if(!StringUtils.isEmpty(paramJson)){
			net.sf.json.JSONObject jsonMap = net.sf.json.JSONObject.fromObject(paramJson);
			parseToMap(jsonMap, map);
		}
		return map;  
	}
	
	/**
	 * 符合json字符串 返回true
	 * 不符合false 
	 */
	public static boolean validateIsKeyValue(String paramJson){
		if(!StringUtils.isEmpty(paramJson)){
			try {
				net.sf.json.JSONObject.fromObject(paramJson);
			} catch (Exception e) {
				return false;
			}
		}
		return true;  
	}
	
	/**
	 * 处理信息共用部分
	 * @param paramJson map 格式的 json字符串
	 * @param map 将处理好的信息放进传入的map中
	 */
	public static void parseToMap(net.sf.json.JSONObject jsonMap,Map<String, String> map){
		 Iterator keys=jsonMap.keys();  
		    while(keys.hasNext()){  
		        String key=(String) keys.next();  
		        String value=jsonMap.getString(key);
		        	map.put(key, value);
		    }
	}
	
	
	
	/**
	 * 将list的json字符串分割存储到map中
	 * @param paramJson list形式的json字符串 
	 * @return List
	 */
	public static  List<Map<String, String>>   parseToMapInList(String paramJson){
		JSONArray jsonArray = net.sf.json.JSONArray.fromObject(paramJson);
		List<Map<String, String>> resusltList = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			Map<String, String> map = new HashMap<>();
			net.sf.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
			parseToMap(jsonObject, map);
			resusltList.add(map);
		}
		return resusltList;
	}
	
	
	
	
	
}
