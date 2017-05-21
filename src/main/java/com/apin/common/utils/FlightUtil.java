package com.apin.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;

import com.apin.base.bean.ApinAirComp;
import com.apin.base.bean.ApinCity;
import com.apin.base.bean.DictAirport;
import com.apin.common.global.DictSource;



/**
 * 
 * 航班相关工具类
 * @author Young
 * @date 2016年10月18日
 */
public class FlightUtil {
	
	/**
	 * 返回航空公司名称
	 * @param cellValue
	 * @return
	 */
	public static String returnAirComp(String cellValue) {
		if(!StringUtils.isBlank(cellValue)){
			ApinAirComp airComp = DictSource.APIN_AIR_COMP_MAP.get(cellValue.substring(0, 2));
			if(airComp != null){
				return airComp.getName();
			}
		}
		return "";
	}
	/**
	 * 返回机场信息
	 * @param airportCode
	 * @return
	 */
	public static DictAirport returnDictAirport(String airportCode){
		return DictSource.AIRPORT_CODE_MAP.get(airportCode.toUpperCase());
	}
	
	/**
	 * 城市信息
	 * @param cityCode
	 * @return
	 */
	public static ApinCity returnCity(String cityCode){
		return DictSource.APIN_CITY_MAP.get(cityCode.toUpperCase());
	}
	
	
	/**
	 * 是否在同一个时区
	 * @param departCode 机场代码
	 * @param destCode 机场代码
	 * @return true 同一时区,false 不同时区
	 */
	public static boolean isDifferentTimeZone(String departCode,String destCode){
		String departTimeZone = returnTimeZone(departCode.toUpperCase());
		String destTimeZone = returnTimeZone(destCode.toUpperCase());
		if(departTimeZone.equals(destTimeZone)){
			return true;
		}return false;
	}
	/**
	 * 是否在同一个时区
	 * @param departCode 城市代码
	 * @param destCode 城市代码
	 * @return true 同一时区,false 不同时区
	 */
	public static boolean isDifferentTimeZoneByCityCode(String departCode,String destCode){
		String departTimeZone = returnTimeZoneByCityCode(departCode.toUpperCase());
		String destTimeZone = returnTimeZoneByCityCode(destCode.toUpperCase());
		if(departTimeZone.equals(destTimeZone)){
			return true;
		}return false;
	}
	
	/**
	 * @param airCode 机场代码
	 * @return 例如：Asia/Shanghai 
	 */
	public static String returnTimeZone(String airCode){
		DictAirport airPort = DictSource.AIRPORT_CODE_MAP.get(airCode);
		if(airPort != null ){
			if(StringUtils.isNoneBlank(airPort.getTimeZone())){
				return airPort.getTimeZone();
			}
			return "Asia/Shanghai";
		}
		return "Asia/Shanghai";
	}
	/**
	 * @param cityCode 城市代码
	 * @return 例如：Asia/Shanghai 
	 */
	public static String returnTimeZoneByCityCode(String cityCode){
		DictAirport airPort = DictSource.AIRPORT_CITY_CODE_MAP.get(cityCode);
		if(airPort != null ){
			if(StringUtils.isNoneBlank(airPort.getTimeZone())){
				return airPort.getTimeZone();
			}
			return "Asia/Shanghai";
		}
		return "Asia/Shanghai";
	}
	
	/**
	 * 是否有转机
	 * @param cellValue
	 * @return 0否，1是
	 */
	public static Byte returnHasTurn(String cellValue) {
		if("是".equals(cellValue)){
			return 1;
		}else if("否".equals(cellValue)){
			return 0;
		}
		return 0;
	}
	
	
	
	/**
	 * 验证 excel文件 必填项是否为空
	 * 
	 * @return
	 */
	public static boolean validateSingleRound(Sheet sheet){
		
		
		return false;
	}
	
}
