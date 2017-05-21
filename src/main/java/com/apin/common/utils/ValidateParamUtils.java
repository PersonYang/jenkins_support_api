package com.apin.common.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 验证参数
 * @author Young
 * @date 2017年1月17日 下午8:41:31
 * @version 1.0 
 */
public class ValidateParamUtils {

	
	
	/**
	 * 发布航班验证参数情况
	 * @param param
	 * @return
	 */
	public static boolean validateFlightParam(String param){
		Map<String, String> paramMap = JsonUtil.parseToMap(param);
		if(StringUtils.isBlank(paramMap.get("flightType")) 
				|| StringUtils.isBlank(paramMap.get("userId"))
				|| StringUtils.isBlank(paramMap.get("price"))
				|| StringUtils.isBlank(paramMap.get("ticketNum"))
				|| StringUtils.isBlank(paramMap.get("flightDetail"))){
			return false;
		}
		List<Map<String, String>> flightMapList = JsonUtil.parseToMapInList(paramMap.get("flightDetail"));
		for (Map<String, String> map : flightMapList) {
			if(StringUtils.isBlank(map.get("flightNo")) || StringUtils.isBlank(map.get("timeSpace"))){
				return false;
			}
		}
		// 见证时间段间隔天数时候相同是否
		if("2".equals(paramMap.get("flightType"))){
			if(flightMapList.size() != 2){
				return false;
			}
			Map<String, String> firstMap = flightMapList.get(0);
			Map<String, String> secondtMap = flightMapList.get(1);
			String firstTimeSpeace = firstMap.get("timeSpace").replace("[", "").replace("]", "");
			String secondTimeSpeace = secondtMap.get("timeSpace").replace("[", "").replace("]", "");
			String [] firstTimeSpeaceArray = firstTimeSpeace.split(",");
			String [] secondTimeSpeaceArray = secondTimeSpeace.split(",");
			if(firstTimeSpeaceArray.length != secondTimeSpeaceArray.length){
				return false;
			}
			for (int i = 0; i < firstTimeSpeaceArray.length; i++) {
				String [] firstStrArray = firstTimeSpeaceArray[i].split("/");
				String [] secondStrArray = secondTimeSpeaceArray[i].split("/");
				if(firstStrArray.length != secondStrArray.length){
					return false;
				}
			}
			
		}
		return true;
	}
	
	/**
	 * 校验分页参数是否为空
	 * @param pageMap
	 * @return true 不为空 false 为空
	 */
	public static boolean validatePageParam(Map<String, String> pageMap){
		if(pageMap==null){
			return false;
		}
		if(StringUtils.isNumeric(pageMap.get("pageSize")) && StringUtils.isNumeric(pageMap.get("pageIndex"))){
			return true;
		}
		return false;
	}

	/**
	 * 商户出价验证提交信息
	 * @param routeNo
	 * @param depositRatio
	 * @param restLastTicketTime
	 * @param ticketRate
	 * @param submitParam
	 * @return
	 */
	public static boolean validateOfferPrice(String params) {
		if(!JsonUtil.validateIsKeyValue(params)){
			return false;
		}
		Map<String, String> paramsMap = JsonUtil.parseToMap(params);
		if (StringUtils.isNoneBlank(paramsMap.get("routeNo")) &&isDouble(paramsMap.get("depositRatio"))
				&& isDouble(paramsMap.get("ticketRate")) && StringUtils.isNoneBlank(paramsMap.get("submitParam"))) {
			if(Double.parseDouble(paramsMap.get("depositRatio"))>1 || Double.parseDouble(paramsMap.get("depositRatio"))<0 
					|| Double.parseDouble(paramsMap.get("ticketRate"))<0 || Double.parseDouble(paramsMap.get("ticketRate"))>1){
				return false;
			}
			List<Map<String, String>> listMap;
			try {
				listMap = JsonUtil.parseToMapInList(paramsMap.get("submitParam"));
				for (int i = 0; i < listMap.size(); i++) {
					Map<String, String> map = listMap.get(i);
					if(map != null){
						if(StringUtils.isBlank(map.get("journeyId")) || StringUtils.isBlank(map.get("price")) ){
							return false;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	public static boolean isDouble(String value) {
		  try {
		   Double.parseDouble(value);
		  } catch (NumberFormatException e) {
		     return false;
		  }
		return true;
	}

	/**
	 * 校验用户提交的出票参数
	 * @param ticketParam
	 * @return
	 */
	public static boolean validatePushParam(String params) {
		if(!JsonUtil.validateIsKeyValue(params)){
			return false;
		}
		Map<String, String> paramsMap = JsonUtil.parseToMap(params);
		List<Map<String, String>> ticketMapList = JsonUtil.parseToMapInList(paramsMap.get("ticketParam"));
		if(ticketMapList == null || ticketMapList.size() == 0){
			return false;
		}
		for (Map<String, String> map : ticketMapList) {
			if(StringUtils.isBlank(map.get("passengerId"))){
				return false;
			}
			if(StringUtils.isBlank(map.get("ticketNo"))){
				return false;
			}
		}
		return true;
	}

	/**
	 * 后台提交的
	 * 航班参数验证
	 * @param param
	 * @return
	 */
	public static boolean validateFlightParamFromAdmin(String param) {
		Map<String, String> paramMap = JsonUtil.parseToMap(param);
		if(StringUtils.isBlank(paramMap.get("flightType"))
				|| StringUtils.isBlank(paramMap.get("flightDetail"))){
			return false;
		}
		List<Map<String, String>> flightMapList = JsonUtil.parseToMapInList(paramMap.get("flightDetail"));
		for (Map<String, String> map : flightMapList) {
			if(StringUtils.isBlank(map.get("flightNo")) || StringUtils.isBlank(map.get("departDate"))){
				return false;
			}
			if(StringUtils.isNoneBlank(paramMap.get("hasTurn")) && "1".equals(paramMap.get("hasTurn"))){
				if(StringUtils.isBlank(map.get("turnFlightNo"))){
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 验证参数是否符合格式
	 * @param params
	 * @return
	 */
	public static boolean validateRegisterParam(String params) {
		if(!JsonUtil.validateIsKeyValue(params)){
			return false;
		}
		Map<String, String> paramMap = JsonUtil.parseToMap(params);
		if(StringUtils.isBlank(paramMap.get("business_phone"))){
			return false;
		}
		if(StringUtils.isBlank(paramMap.get("nick_name"))){
			return false;
		}
		if(StringUtils.isBlank(paramMap.get("business_address"))){
			return false;
		}
		if(StringUtils.isBlank(paramMap.get("major_principal"))){
			return false;
		}
		if(StringUtils.isBlank(paramMap.get("contact_info"))){
			return false;
		}
		if(StringUtils.isBlank(paramMap.get("contact_person"))){
			return false;
		}
		if(StringUtils.isBlank(paramMap.get("user_id"))){
			return false;
		}
		return true;
	}
}
