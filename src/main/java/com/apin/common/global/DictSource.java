package com.apin.common.global;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.apin.base.bean.ApinAirComp;
import com.apin.base.bean.ApinCity;
import com.apin.base.bean.DictAirport;

/**
 * 全局资源
 * @author Young
 * @date 2017年1月11日 下午4:47:10
 * @version 1.0 
 */
public class DictSource {
	
	public static List<DictAirport> DICT_AIRPORT;
	public static List<ApinAirComp> DICT_AIR_COMP;
	public static List<ApinCity> DICT_CITY;
	
	/**
	 *KAY 机场iata代码，value DictAirport
	 */
	public static final Map<String, DictAirport> AIRPORT_CODE_MAP = Collections.synchronizedMap(new HashMap<String, DictAirport>()); 
	
	/**
	 *KAY 城市代码，value DictAirport
	 */
	public static final Map<String, DictAirport> AIRPORT_CITY_CODE_MAP = new ConcurrentHashMap<String, DictAirport>();
	
	public static final Map<String, ApinAirComp> APIN_AIR_COMP_MAP = new ConcurrentHashMap<String, ApinAirComp>();
	
	public static final Map<String, ApinCity> APIN_CITY_MAP = new ConcurrentHashMap<String, ApinCity>();
}
