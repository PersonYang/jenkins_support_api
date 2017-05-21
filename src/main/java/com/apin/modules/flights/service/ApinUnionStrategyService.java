package com.apin.modules.flights.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
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
 * 联程航班发布相关情况
 * @author Young
 * @date 2017年3月13日 下午1:26:01
 * @version 1.0 
 */
@Service
public class ApinUnionStrategyService {
	@Autowired
	AirLineFlightVersionService flightVersionService;
	@Autowired
	AirLineFlightService lineFlightService;
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
	 * 多程航班单个添加
	 * 多程航班信息添加
	 * @param paramMap
	 * @param flightMapList
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> addUnionFlight(Map<String, String> paramMap, List<Map<String, String>> flightMapList,
			String userId) throws Exception {
		// 聚合查询参数
		Map<String, Object> flightParamMap = new HashMap<>();
		Map<String, Object> returnResultMap =new HashMap<>();
		Byte flightType = 0;
		// 终结整个行程的
		for (Map<String, String> map : flightMapList) {
			flightParamMap.put("dtype", "");
			flightParamMap.put("name", map.get("flightNo"));
			flightParamMap.put("key", juheAppKey);
			flightParamMap.put("date", DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
			// 获取每一程的航班信息
			Map<String, String> flightMap = lineFlightService.getEachFlightInfo(flightParamMap);
			if(flightMap == null ){
				returnResultMap.put("status", false);
				returnResultMap.put("message", "未查询到该航班");
				return returnResultMap;
			}
			flightType = PublishFlightUtils.returnFlightType(flightMap.get("departAirportCode"), flightMap.get("arriveAirportCode"));
			if(flightType ==1 ){
				break;
			}
		}
		// 生成行程ID
		ApinJourneyIdSequence journeyIdSequence = new ApinJourneyIdSequence();
		journeyIdDao.insertJourneyIdSequence(journeyIdSequence);
		// 航班主体信息
		ApinMerchantFlightInfo mainFlight = PublishFlightUtils.packMain(paramMap, userId);
		mainFlight.setJourneyId(journeyIdSequence.getJourneyId());
		mainFlight.setGroupId(UUID.randomUUID().toString());
		mainFlight.setFlightType(flightType);
		mainFlightInfoDao.insert(mainFlight);
		// 添加所有的航班信息到数据库
		Integer parentId = 0;
		Byte tripNumber = 0;
		for (Map<String, String> map : flightMapList) {
			String timeSpeace = map.get("timeSpace").replace("[", "").replace("]", "");
			String [] firstTimeSpeace = timeSpeace.split(",");
			if(StringUtils.isBlank(firstTimeSpeace[0])){
				returnResultMap.put("status", false);
				returnResultMap.put("message", "时间有误");
				return returnResultMap;
			}
			String [] firstTime = firstTimeSpeace[0].replace("\"", "").split("/");
			if(StringUtils.isBlank(firstTime[0])){
				returnResultMap.put("status", false);
				returnResultMap.put("message", "时间有误");
				return returnResultMap;
			}
			flightParamMap.put("dtype", "");
			flightParamMap.put("name", map.get("flightNo"));
			flightParamMap.put("key", juheAppKey);
			flightParamMap.put("date", DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
			// 获取每一程的航班信息
			Map<String, String> flightMap = lineFlightService.getEachFlightInfo(flightParamMap);
			if(flightMap == null ){
				returnResultMap.put("status", false);
				returnResultMap.put("message", "未查询到该航班");
				return returnResultMap;
			}
			// 无转机时 获取去程的航班信息
			ApinMerchantFlightDetailInfo firstDetailInfo = PublishFlightUtils.packDetail(flightMap);
			firstDetailInfo.setJourneyId(mainFlight.getJourneyId());
			firstDetailInfo.setParentId(parentId);
			firstDetailInfo.setTripNumber(tripNumber);
			firstDetailInfo.setDepartDate(DateUtil.toFormatDate(firstTime[0], "yyyy-MM-dd"));
			firstDetailInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstTime[0], flightMap.get("departTime"), flightMap.get("arriveTime")));
			// 去程有经停的航班信息 需要将经停信息放入 转机表中
			if("1".equals(flightMap.get("isMiddleStop"))){
				firstDetailInfo.setHasTurn((byte)2);
				detailInfoDao.insert(firstDetailInfo);
				// 经停信息
				ApinMerchantFlightTurnInfo firstMiddleInfo = PublishFlightUtils.middleStopInfo(flightMap);
				firstMiddleInfo.setFlightId(firstDetailInfo.getId());
				firstMiddleInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstTime[0], flightMap.get("departTime"), flightMap.get("middleArriveTime")));
				firstMiddleInfo.setDepartDate(PublishFlightUtils.getArriveDate(DateUtil.dateToStr(firstMiddleInfo.getArriveDate(), "yyyy-MM-dd"),
						flightMap.get("middleArriveTime"), flightMap.get("middleDepartTime")));
				turnInfoDao.insert(firstMiddleInfo);
			}else{
				firstDetailInfo.setHasTurn((byte)0);
				detailInfoDao.insert(firstDetailInfo);
			}
			parentId = firstDetailInfo.getId();
			tripNumber = (byte)(tripNumber+(byte)1);
		}
		returnResultMap.put("status", true);
		returnResultMap.put("message", "添加成功");
		return returnResultMap;
	}

	/**
	 * 改版后供应商添加发布航班信息的情况。
	 * @param paramMap
	 * @param flightMapList
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> addUnionFlightVersion(Map<String, String> paramMap, List<Map<String, String>> flightMapList,
			String userId) throws Exception {
		// 聚合查询参数
		Map<String, Object> flightParamMap = new HashMap<>();
		Map<String, Object> returnResultMap =new HashMap<>();
		Byte flightType = 0;
		// 终结整个行程的
		for (Map<String, String> map : flightMapList) {
			flightParamMap.put("dtype", "");
			flightParamMap.put("departPlaceCode", map.get("departPlaceCode"));
			flightParamMap.put("destPlaceCode", map.get("destPlaceCode"));
			flightParamMap.put("name", map.get("flightNo"));
			flightParamMap.put("key", juheAppKey);
			flightParamMap.put("date", DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
			// 获取每一程的航班信息
			Map<String, String> flightMap = flightVersionService.getEachFlightInfo(flightParamMap);
			if(flightMap == null ){
				returnResultMap.put("status", false);
				returnResultMap.put("message", "未查询到该航班");
				return returnResultMap;
			}
			flightType = PublishFlightUtils.returnFlightType(flightMap.get("departAirportCode"), flightMap.get("arriveAirportCode"));
			if(flightType ==1 ){
				break;
			}
		}
		// 生成行程ID
		ApinJourneyIdSequence journeyIdSequence = new ApinJourneyIdSequence();
		journeyIdDao.insertJourneyIdSequence(journeyIdSequence);
		// 航班主体信息
		ApinMerchantFlightInfo mainFlight = PublishFlightUtils.packMain(paramMap, userId);
		mainFlight.setJourneyId(journeyIdSequence.getJourneyId());
		mainFlight.setGroupId(UUID.randomUUID().toString());
		mainFlight.setFlightType(flightType);
		mainFlightInfoDao.insert(mainFlight);
		// 添加所有的航班信息到数据库
		Integer parentId = 0;
		Byte tripNumber = 0;
		for (Map<String, String> map : flightMapList) {
			String timeSpeace = map.get("timeSpace").replace("[", "").replace("]", "");
			String [] firstTimeSpeace = timeSpeace.split(",");
			if(StringUtils.isBlank(firstTimeSpeace[0])){
				returnResultMap.put("status", false);
				returnResultMap.put("message", "时间有误");
				return returnResultMap;
			}
			String [] firstTime = firstTimeSpeace[0].replace("\"", "").split("/");
			if(StringUtils.isBlank(firstTime[0])){
				returnResultMap.put("status", false);
				returnResultMap.put("message", "时间有误");
				return returnResultMap;
			}
			flightParamMap.put("dtype", "");
			flightParamMap.put("name", map.get("flightNo"));
			flightParamMap.put("departPlaceCode", map.get("departPlaceCode"));
			flightParamMap.put("destPlaceCode", map.get("destPlaceCode"));
			flightParamMap.put("key", juheAppKey);
			flightParamMap.put("date", DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
			// 获取每一程的航班信息
			Map<String, String> flightMap = flightVersionService.getEachFlightInfo(flightParamMap);
			if(flightMap == null ){
				returnResultMap.put("status", false);
				returnResultMap.put("message", "未查询到该航班");
				return returnResultMap;
			}
			// 无转机时 获取去程的航班信息
			ApinMerchantFlightDetailInfo firstDetailInfo = PublishFlightUtils.packDetail(flightMap);
			firstDetailInfo.setJourneyId(mainFlight.getJourneyId());
			firstDetailInfo.setParentId(parentId);
			firstDetailInfo.setTripNumber(tripNumber);
			firstDetailInfo.setDepartDate(DateUtil.toFormatDate(firstTime[0], "yyyy-MM-dd"));
			firstDetailInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstTime[0], flightMap.get("departTime"), flightMap.get("arriveTime")));
			// 去程有经停的航班信息 需要将经停信息放入 转机表中
			if("1".equals(flightMap.get("isMiddleStop"))){
				firstDetailInfo.setHasTurn((byte)2);
				detailInfoDao.insert(firstDetailInfo);
				// 经停信息
				ApinMerchantFlightTurnInfo firstMiddleInfo = PublishFlightUtils.middleStopInfo(flightMap);
				firstMiddleInfo.setFlightId(firstDetailInfo.getId());
				firstMiddleInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstTime[0], flightMap.get("departTime"), flightMap.get("middleArriveTime")));
				firstMiddleInfo.setDepartDate(PublishFlightUtils.getArriveDate(DateUtil.dateToStr(firstMiddleInfo.getArriveDate(), "yyyy-MM-dd"),
						flightMap.get("middleArriveTime"), flightMap.get("middleDepartTime")));
				turnInfoDao.insert(firstMiddleInfo);
			}else{
				firstDetailInfo.setHasTurn((byte)0);
				detailInfoDao.insert(firstDetailInfo);
			}
			parentId = firstDetailInfo.getId();
			tripNumber = (byte)(tripNumber+(byte)1);
		}
		returnResultMap.put("status", true);
		returnResultMap.put("message", "添加成功");
		return returnResultMap;
	}
	
}
