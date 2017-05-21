package com.apin.modules.flights.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apin.base.service.AirLineFlightService;
import com.apin.base.service.AirLineFlightVersionService;
import com.apin.common.utils.DateUtil;
import com.apin.modules.flights.bean.ApinJourneyIdSequence;
import com.apin.modules.flights.bean.ApinMerchantFlightDetailInfo;
import com.apin.modules.flights.bean.ApinMerchantFlightInfo;
import com.apin.modules.flights.bean.ApinMerchantFlightTurnInfo;
import com.apin.modules.flights.dao.ApinJourneyIdSequenceDao;
import com.apin.modules.flights.dao.ApinMerchantFlightDetailInfoDao;
import com.apin.modules.flights.dao.ApinMerchantFlightInfoDao;
import com.apin.modules.flights.dao.ApinMerchantFlightTurnInfoDao;

/**
 * 单程添加航班相关
 * @author Young
 * @date 2017年3月13日 下午1:26:01
 * @version 1.0 
 */
@Service
public class ApinSingleStrategyService {
	@Autowired
	AirLineFlightVersionService flightVersionService;
	@Autowired
	AirLineFlightService airLineFlightService;
	@Value("${juhe.appkey}")
	private String juheAppKey;
	@Autowired
	ApinJourneyIdSequenceDao journeyIdDao;
	@Autowired
	ApinMerchantFlightInfoDao mainFlightInfoDao;
	@Autowired
	ApinMerchantFlightDetailInfoDao detailInfoDao;
	@Autowired
	ApinMerchantFlightTurnInfoDao turnInfoDao;
	/**
	 * 早期版本 before 2017-03-13
	 * 添加单程航班
	 * @param paramMap
	 * @param flightMapList
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Exception.class)
	public Map<String, Object> addSingleFlight(Map<String, String> paramMap, List<Map<String, String>> flightMapList,
			String userId) throws Exception{
		Map<String, Object> returnResultMap =new HashMap<>();
		Map<String, String> firstMap = flightMapList.get(0);
		String flightNo = firstMap.get("flightNo");
		Map<String, Object> map = new HashMap<>();
		map.put("dtype","");
		map.put("name",flightNo);
		map.put("key",juheAppKey);
		map.put("date",DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
		Map<String, String> flightMap = airLineFlightService.getEachFlightInfo(map);
		Map<String, String> turnFlightMap =null;
		if(flightMap == null){
			map.put("date",DateUtil.changeDay(new Date(), 1, true));
			flightMap = airLineFlightService.getEachFlightInfo(map);
			if(flightMap ==null ){
				returnResultMap.put("status", false);
				returnResultMap.put("message", "未查询到该航班");
				return returnResultMap;
			}
		}
		if("1".equals(paramMap.get("isTurn"))){
			map.put("name",firstMap.get("turnFlightNo"));
			map.put("key",juheAppKey);
			map.put("date",DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
			turnFlightMap= airLineFlightService.getEachFlightInfo(map);
			if(turnFlightMap == null){
				map.put("date",DateUtil.changeDay(new Date(), 1, true));
				turnFlightMap = airLineFlightService.getEachFlightInfo(map);
				if(turnFlightMap ==null ){
					returnResultMap.put("status", false);
					returnResultMap.put("message", "未查询到转机航班");
					return returnResultMap;
				}
			}
		}
		String firstTimeSpeace = firstMap.get("timeSpace").replace("[", "").replace("]", "");
		String [] firstTimeSpeaceArray = firstTimeSpeace.split(",");
		for (int i = 0; i < firstTimeSpeaceArray.length; i++) {
			String groupId = UUID.randomUUID().toString();
			paramMap.put("groupId", groupId);
			String [] firstStrArray = firstTimeSpeaceArray[i].replace("\"", "").split("/");
			for (int j = 0; j < firstStrArray.length; j++) {
				System.out.println("===="+firstStrArray[j]);
				ApinJourneyIdSequence journeyIdSequence = new ApinJourneyIdSequence();
				journeyIdDao.insertJourneyIdSequence(journeyIdSequence);
				// 航班主体信息
				ApinMerchantFlightInfo mainFlight = PublishFlightUtils.packMain(paramMap, userId);
				mainFlight.setJourneyId(journeyIdSequence.getJourneyId());
				// 判断当前航班属于国内还是国际
				if(PublishFlightUtils.returnFlightType(flightMap.get("departAirportCode"), flightMap.get("arriveAirportCode")) == 1 || 
						(turnFlightMap != null &&PublishFlightUtils. returnFlightType(turnFlightMap.get("departAirportCode"), turnFlightMap.get("arriveAirportCode")) == 1)){
					mainFlight.setFlightType((byte)1);
				}else{
					mainFlight.setFlightType((byte)0);
				}
				mainFlightInfoDao.insert(mainFlight);
				if("1".equals(paramMap.get("isTurn"))){
					// 得出航班详情
					ApinMerchantFlightDetailInfo detailInfo = PublishFlightUtils.packDetailWhenTurn(flightMap, turnFlightMap);
					detailInfo.setJourneyId(mainFlight.getJourneyId());
					detailInfo.setParentId(0);
					detailInfo.setTripNumber((byte)0);
					detailInfo.setDepartDate(DateUtil.toFormatDate(firstStrArray[j], "yyyy-MM-dd"));
					detailInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], flightMap.get("departTime"), turnFlightMap.get("arriveTime")));
					detailInfoDao.insert(detailInfo);
					ApinMerchantFlightTurnInfo turnInfo = PublishFlightUtils.packageTurnInfo(flightMap, turnFlightMap);
					turnInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], flightMap.get("departTime"), flightMap.get("arriveTime")));
					turnInfo.setDepartDate(PublishFlightUtils.getArriveDate(DateUtil.dateToStr(turnInfo.getArriveDate(), "yyyy-MM-dd"),
							flightMap.get("arriveTime"), turnFlightMap.get("departTime")));
					turnInfo.setFlightId(detailInfo.getId());
					turnInfoDao.insert(turnInfo);
				}else {
					ApinMerchantFlightDetailInfo detailInfo = PublishFlightUtils.packDetail(flightMap);
					detailInfo.setJourneyId(mainFlight.getJourneyId());
					detailInfo.setParentId(0);
					detailInfo.setTripNumber((byte)0);
					detailInfo.setDepartDate(DateUtil.toFormatDate(firstStrArray[j], "yyyy-MM-dd"));
					detailInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], flightMap.get("departTime"), flightMap.get("arriveTime")));
					// 有经停的航班信息
					if("1".equals(flightMap.get("isMiddleStop"))){
						detailInfo.setHasTurn((byte)2);
						detailInfoDao.insert(detailInfo);
						// 经停信息
						ApinMerchantFlightTurnInfo middleInfo = PublishFlightUtils.middleStopInfo(flightMap);
						middleInfo.setFlightId(detailInfo.getId());
						middleInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], flightMap.get("departTime"), flightMap.get("middleArriveTime")));
						middleInfo.setDepartDate(PublishFlightUtils.getArriveDate(DateUtil.dateToStr(middleInfo.getArriveDate(), "yyyy-MM-dd"),
								flightMap.get("middleArriveTime"), flightMap.get("middleDepartTime")));
						turnInfoDao.insert(middleInfo);
					}else{
						detailInfo.setHasTurn((byte)0);
						detailInfoDao.insert(detailInfo);
					}
				}
			}
		}
		returnResultMap.put("status", true);
		returnResultMap.put("message", "操作成功");
		return returnResultMap;
	}
	
	
	
	
	/**
	 * 新版本航班 信息发布
	 * @param paramMap
	 * @param flightMapList
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Exception.class)
	public Map<String, Object> addSingleFlightVersion(Map<String, String> paramMap, List<Map<String, String>> flightMapList,
			String userId) throws Exception{
		Map<String, Object> returnResultMap =new HashMap<>();
		Map<String, String> firstMap = flightMapList.get(0);
		String flightNo = firstMap.get("flightNo");
		Map<String, Object> map = new HashMap<>();
		Map<String, String> turnFlightMap =null;
		Map<String, String> flightMap = null;
		if("1".equals(paramMap.get("isTurn"))){
			map.put("dtype","");
			map.put("name",flightNo);
			map.put("key",juheAppKey);
			map.put("departPlaceCode",firstMap.get("departPlaceCode"));
			map.put("destPlaceCode",firstMap.get("turnCityCode"));
			map.put("date",DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
			flightMap = flightVersionService.getEachFlightInfo(map);
			if(flightMap == null){
				map.put("date",DateUtil.changeDay(new Date(), 1, true));
				flightMap = flightVersionService.getEachFlightInfo(map);
				if(flightMap ==null ){
					returnResultMap.put("status", false);
					returnResultMap.put("message", "未查询到该航班");
					return returnResultMap;
				}
			}
			
			map.put("departPlaceCode", firstMap.get("turnCityCode"));
			map.put("destPlaceCode", firstMap.get("destPlaceCode"));
			map.put("name",firstMap.get("turnFlightNo"));
			turnFlightMap= flightVersionService.getEachFlightInfo(map);
			if(turnFlightMap == null){
				map.put("date",DateUtil.changeDay(new Date(), 1, true));
				turnFlightMap = flightVersionService.getEachFlightInfo(map);
				if(turnFlightMap ==null ){
					returnResultMap.put("status", false);
					returnResultMap.put("message", "未查询到转机航班");
					return returnResultMap;
				}
			}
		 // 非转机
		}else{
			map.put("dtype","");
			map.put("name",flightNo);
			map.put("key",juheAppKey);
			map.put("departPlaceCode",firstMap.get("departPlaceCode"));
			map.put("destPlaceCode",firstMap.get("destPlaceCode"));
			map.put("date",DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
			flightMap = flightVersionService.getEachFlightInfo(map);
			if(flightMap == null){
				map.put("date",DateUtil.changeDay(new Date(), 1, true));
				flightMap = flightVersionService.getEachFlightInfo(map);
				if(flightMap ==null ){
					returnResultMap.put("status", false);
					returnResultMap.put("message", "未查询到该航班");
					return returnResultMap;
				}
			}
		}
		String firstTimeSpeace = firstMap.get("timeSpace").replace("[", "").replace("]", "");
		String [] firstTimeSpeaceArray = firstTimeSpeace.split(",");
		for (int i = 0; i < firstTimeSpeaceArray.length; i++) {
			String groupId = UUID.randomUUID().toString();
			paramMap.put("groupId", groupId);
			String [] firstStrArray = firstTimeSpeaceArray[i].replace("\"", "").split("/");
			for (int j = 0; j < firstStrArray.length; j++) {
				ApinJourneyIdSequence journeyIdSequence = new ApinJourneyIdSequence();
				journeyIdDao.insertJourneyIdSequence(journeyIdSequence);
				// 航班主体信息
				ApinMerchantFlightInfo mainFlight = PublishFlightUtils.packMain(paramMap, userId);
				mainFlight.setJourneyId(journeyIdSequence.getJourneyId());
				// 判断当前航班属于国内还是国际
				if(PublishFlightUtils.returnFlightType(flightMap.get("departAirportCode"), flightMap.get("arriveAirportCode")) == 1 || 
						(turnFlightMap != null &&PublishFlightUtils. returnFlightType(turnFlightMap.get("departAirportCode"), turnFlightMap.get("arriveAirportCode")) == 1)){
					mainFlight.setFlightType((byte)1);
				}else{
					mainFlight.setFlightType((byte)0);
				}
				mainFlightInfoDao.insert(mainFlight);
				if("1".equals(paramMap.get("isTurn"))){
					// 得出航班详情
					ApinMerchantFlightDetailInfo detailInfo = PublishFlightUtils.packDetailWhenTurn(flightMap, turnFlightMap);
					detailInfo.setJourneyId(mainFlight.getJourneyId());
					detailInfo.setParentId(0);
					detailInfo.setTripNumber((byte)0);
					detailInfo.setDepartDate(DateUtil.toFormatDate(firstStrArray[j], "yyyy-MM-dd"));
					detailInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], flightMap.get("departTime"), turnFlightMap.get("arriveTime")));
					detailInfoDao.insert(detailInfo);
					ApinMerchantFlightTurnInfo turnInfo = PublishFlightUtils.packageTurnInfo(flightMap, turnFlightMap);
					turnInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], flightMap.get("departTime"), flightMap.get("arriveTime")));
					turnInfo.setDepartDate(PublishFlightUtils.getArriveDate(DateUtil.dateToStr(turnInfo.getArriveDate(), "yyyy-MM-dd"),
							flightMap.get("arriveTime"), turnFlightMap.get("departTime")));
					turnInfo.setFlightId(detailInfo.getId());
					turnInfoDao.insert(turnInfo);
				}else {
					ApinMerchantFlightDetailInfo detailInfo = PublishFlightUtils.packDetail(flightMap);
					detailInfo.setJourneyId(mainFlight.getJourneyId());
					detailInfo.setParentId(0);
					detailInfo.setTripNumber((byte)0);
					detailInfo.setDepartDate(DateUtil.toFormatDate(firstStrArray[j], "yyyy-MM-dd"));
					detailInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], flightMap.get("departTime"), flightMap.get("arriveTime")));
					// 有经停的航班信息
					if("1".equals(flightMap.get("isMiddleStop"))){
						detailInfo.setHasTurn((byte)2);
						detailInfoDao.insert(detailInfo);
						// 经停信息
						ApinMerchantFlightTurnInfo middleInfo = PublishFlightUtils.middleStopInfo(flightMap);
						middleInfo.setFlightId(detailInfo.getId());
						middleInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], flightMap.get("departTime"), flightMap.get("middleArriveTime")));
						middleInfo.setDepartDate(PublishFlightUtils.getArriveDate(DateUtil.dateToStr(middleInfo.getArriveDate(), "yyyy-MM-dd"),
								flightMap.get("middleArriveTime"), flightMap.get("middleDepartTime")));
						turnInfoDao.insert(middleInfo);
					}else{
						detailInfo.setHasTurn((byte)0);
						detailInfoDao.insert(detailInfo);
					}
				}
			}
		}
		returnResultMap.put("status", true);
		returnResultMap.put("message", "操作成功");
		return returnResultMap;
	}
	
}
