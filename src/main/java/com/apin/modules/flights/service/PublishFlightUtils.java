package com.apin.modules.flights.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.apin.base.bean.DictAirport;
import com.apin.common.global.DictSource;
import com.apin.common.utils.DateUtil;
import com.apin.common.utils.JsonUtil;
import com.apin.modules.flights.bean.ApinMerchantFlightDetailInfo;
import com.apin.modules.flights.bean.ApinMerchantFlightInfo;
import com.apin.modules.flights.bean.ApinMerchantFlightTurnInfo;

/**
 * 
 * 添加航班相关utils
 * @author Young
 * @date 2017年3月13日 下午1:26:44
 * @version 1.0 
 */
public class PublishFlightUtils {
	/**
	 * 发布航班验证参数情况
	 * @param param
	 * @return 为空返回false，全部通过返回true.
	 */
	public static boolean valiParams(String param){
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
			if(StringUtils.isBlank(map.get("flightNo")) 
			  || StringUtils.isBlank(map.get("timeSpace"))
			  || StringUtils.isBlank(map.get("departPlace"))
			  || StringUtils.isBlank(map.get("departPlaceCode"))
			  || StringUtils.isBlank(map.get("destPlace"))
			  || StringUtils.isBlank(map.get("destPlaceCode"))
			  ){
				return false;
			}
		}// 如果是转机 判断转机的参数是否为空
		if("1".equals(paramMap.get("isTurn"))){
			for (Map<String, String> map : flightMapList) {
				if(StringUtils.isBlank(map.get("turnCityCode")) 
				  || StringUtils.isBlank(map.get("turnFlightNo"))
				  || StringUtils.isBlank(map.get("turnCity"))
				  ){
					return false;
				}
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
	 * 封装经停信息
	 * @param flightMap
	 * @return
	 */
	public static ApinMerchantFlightTurnInfo middleStopInfo(Map<String, String> flightMap) {
		ApinMerchantFlightTurnInfo turnInfo = new ApinMerchantFlightTurnInfo();
		// 抵达转机城市的情况
		turnInfo.setArriveAirport(flightMap.get("arriveAirport"));
		turnInfo.setArriveAirComp(flightMap.get("airComp"));
		if(StringUtils.isNoneBlank(flightMap.get("flightNo"))){
			turnInfo.setArriveFlightNo(flightMap.get("flightNo").toUpperCase());
			turnInfo.setDepartFlightNo(flightMap.get("flightNo").toUpperCase());
		}
		turnInfo.setArriveTime(flightMap.get("arriveTime"));
		//从转机城市出发的情况
		turnInfo.setDepartAirComp(flightMap.get("airComp"));
		turnInfo.setDepartAirport(flightMap.get("middleAirport"));
		turnInfo.setTurnCity(flightMap.get("middleCity"));
		turnInfo.setDepartTime(flightMap.get("middleDepartTime"));
		turnInfo.setStayTime(DateUtil.redurnStayTime(flightMap.get("middleArriveTime"), flightMap.get("middleDepartTime"), "HH:mm", "hm"));
		return turnInfo;
	}
	
	/**
	 * turnInfo
	 * @param turnFlighttMap
	 * @return
	 */
	public static ApinMerchantFlightTurnInfo packageTurnInfo(Map<String, String> flightMap,
			Map<String, String> turnFlighttMap) {
		ApinMerchantFlightTurnInfo turnInfo = new ApinMerchantFlightTurnInfo();
		// 抵达转机城市的情况
		turnInfo.setArriveAirport(flightMap.get("arriveAirport"));
		turnInfo.setArriveAirComp(flightMap.get("airComp"));
		if(StringUtils.isNoneBlank(flightMap.get("flightNo"))){
			turnInfo.setArriveFlightNo(flightMap.get("flightNo").toUpperCase());
		}
		turnInfo.setArriveTime(flightMap.get("arriveTime"));
		//从转机城市出发的情况
		turnInfo.setDepartAirComp(turnFlighttMap.get("airComp"));
		turnInfo.setDepartAirport(turnFlighttMap.get("departAirport"));
		turnInfo.setDepartFlightNo(turnFlighttMap.get("flightNo"));
		turnInfo.setDepartTime(turnFlighttMap.get("departTime"));
		turnInfo.setTurnCity(turnFlighttMap.get("departPlace"));
		turnInfo.setStayTime(DateUtil.redurnStayTime(flightMap.get("arriveTime"), flightMap.get("departTime"), "HH:mm", "hm"));
		return turnInfo;
	}
	
	
	public static Date getArriveDate(String departDate,String departTime,String arriveTime){
		int with24 = DateUtil.retrurnBetween(departDate+" "+departTime, departDate+" 23:59", "yyyy-MM-dd HH:mm", "h");
		int withArrive = DateUtil.retrurnBetween(departDate+" "+departTime, departDate+" "+arriveTime, "yyyy-MM-dd HH:mm", "h");
		if(with24 >= withArrive){
			return DateUtil.toFormatDate(departDate, "yyyy-MM-dd");
		}else{
			return DateUtil.changeDay(DateUtil.toFormatDate(departDate, "yyyy-MM-dd"), 1, true);
		}
	}

	/**
	 * detailInfo  when turn 
	 * 中转 时 中装 封装航班
	 * 转机 封装航班详情信息
	 * @param flightMap
	 * @return
	 */
	public static ApinMerchantFlightDetailInfo packDetailWhenTurn(Map<String, String> flightMap,
			Map<String, String> turnFlighttMap) {
		ApinMerchantFlightDetailInfo detailInfo = new ApinMerchantFlightDetailInfo();
		detailInfo.setAirComp(flightMap.get("airComp"));
		if(StringUtils.isNoneBlank(flightMap.get("flightNo"))){
			detailInfo.setFlightNo(flightMap.get("flightNo").toUpperCase());
		}
		detailInfo.setDepartPlace(flightMap.get("departPlace"));
		detailInfo.setDepartPlaceCode(flightMap.get("departPlaceCode"));
		detailInfo.setDepartTime(flightMap.get("departTime"));
		detailInfo.setDepartAirport(flightMap.get("departAirport"));
		
		detailInfo.setDestPlace(turnFlighttMap.get("destPlace"));
		detailInfo.setDestPlaceCode(turnFlighttMap.get("destPlaceCode"));
		detailInfo.setArriveAirport(turnFlighttMap.get("arriveAirport"));
		detailInfo.setArriveTime(turnFlighttMap.get("arriveTime"));
		detailInfo.setHasTurn((byte)1);
		return detailInfo;
	}
	
	/**
	 * 封装航班详情信息  没有转机的时候信息
	 * @param flightMap
	 * @return
	 */
	public  static ApinMerchantFlightDetailInfo packDetail(Map<String, String> flightMap) {
		ApinMerchantFlightDetailInfo detailInfo = new ApinMerchantFlightDetailInfo();
		detailInfo.setAirComp(flightMap.get("airComp"));
		if(StringUtils.isNoneBlank(flightMap.get("flightNo"))){
			detailInfo.setFlightNo(flightMap.get("flightNo").toUpperCase());
		}
		detailInfo.setDepartPlace(flightMap.get("departPlace"));
		detailInfo.setDepartPlaceCode(flightMap.get("departPlaceCode"));
		detailInfo.setDepartTime(flightMap.get("departTime"));
		detailInfo.setDepartAirport(flightMap.get("departAirport"));
		detailInfo.setDestPlace(flightMap.get("destPlace"));
		detailInfo.setDestPlaceCode(flightMap.get("destPlaceCode"));
		detailInfo.setArriveAirport(flightMap.get("arriveAirport"));
		detailInfo.setArriveTime(flightMap.get("arriveTime"));
		detailInfo.setHasTurn((byte)0);
		return detailInfo;
	}
	/**
	 * 封装航班主体信息
	 * @param paramMap
	 * @param userId
	 * @param flightMap
	 * @return
	 */
	public static ApinMerchantFlightInfo packMain(Map<String, String> paramMap,String userId){
		ApinMerchantFlightInfo mainFlightInfo = new ApinMerchantFlightInfo();
		mainFlightInfo.setGroupId(paramMap.get("groupId"));
		mainFlightInfo.setIsAudit((byte)1);
		mainFlightInfo.setCurrentTicketNum(Integer.valueOf(paramMap.get("ticketNum")));
		mainFlightInfo.setTotalPriceInctax(new BigDecimal(paramMap.get("price")));
		mainFlightInfo.setIsSpecialTicket(Byte.valueOf(paramMap.get("isSpecialTicket")));
		mainFlightInfo.setIsFareTicket((byte)0);
		mainFlightInfo.setSupplierId(userId);
		mainFlightInfo.setBaggageRules(paramMap.get("baggageRules"));
		mainFlightInfo.setCreateTime(new Date());
		//mainFlightInfo.setFlightType(returnFlightType(flightMap.get("departAirportCode"), flightMap.get("arriveAirportCode")));
		mainFlightInfo.setRouteType(Byte.valueOf(paramMap.get("flightType")));
		mainFlightInfo.setIsShelves((byte)1);
		mainFlightInfo.setVersion(1);
		return mainFlightInfo;
	}
	/**
	 * 
	 * 根据机场代码 返回 航班属于国内 还是国际线路
	 * @param departCode
	 * @param destCode
	 * @return 国内航班返回0，国际航班返回1.
	 */
	public static Byte returnFlightType(String departCode,String destCode){
		Map<Byte, Byte> resultMap = new HashMap<>();
		DictAirport departAirport = DictSource.AIRPORT_CODE_MAP.get(departCode);
		DictAirport destAirport = DictSource.AIRPORT_CODE_MAP.get(destCode);
		if(departAirport != null ){
			resultMap.put(departAirport.getAbroad(), departAirport.getAbroad());
		}
		if(destAirport != null ){
			resultMap.put(destAirport.getAbroad(), destAirport.getAbroad());
		}
		if(resultMap.size()==2){
			return 1;
		}else if(resultMap.size()==1){
			if(resultMap.containsKey((byte)0)){
				return 0;
			}
			return 1;
		}
		return 0;
	}
	
	
	
}
