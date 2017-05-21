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
 * 往返发布航班相关策略service
 * @author Young
 * @date 2017年3月13日 下午1:26:01
 * @version 1.0 
 */
@Service
public class ApinRoundStrategyService {
	@Autowired
	AirLineFlightService lineFlightService;
	@Autowired
	AirLineFlightVersionService lineFlightVersionService;
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
	 * 发布往返航班信息
	 * @param paramMap
	 * @param flightMapList
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	@Transactional(rollbackFor=Exception.class)
	public Map<String, Object> addRoundFlight(Map<String, String> paramMap, List<Map<String, String>> flightMapList,
			String userId) throws Exception {
		Map<String, Object> returnResultMap =new HashMap<>();
		if(flightMapList.size() != 2){
			returnResultMap.put("status", false);
			returnResultMap.put("message", "参数错误");
			return returnResultMap;
		}
	    // 聚合查询参数
		Map<String, Object> flightParamMap = new HashMap<>();
		// 提交的去程参数信息
		Map<String,String> firstFlightParamMap = flightMapList.get(0);
		// 访问接口 提交的 回城航班信息
		Map<String, String> secondFlightParamMap = flightMapList.get(1);
		flightParamMap.put("dtype","");
		flightParamMap.put("name",firstFlightParamMap.get("flightNo"));
		flightParamMap.put("key",juheAppKey);
		flightParamMap.put("date",DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
		// 获取的的去程航班返回信息
		Map<String, String> firstFlightMap = lineFlightService.getEachFlightInfo(flightParamMap);
		// 去程转机信息
		Map<String, String> firstTurnFlightMap =new HashMap<>();
		Map<String, String> secondTurnFlightMap =null;
		if(firstFlightMap == null){
			flightParamMap.put("date",DateUtil.changeDay(new Date(), 1, true));
			firstFlightMap = lineFlightService.getEachFlightInfo(flightParamMap);
			if(firstFlightMap == null ){
				returnResultMap.put("status", false);
				returnResultMap.put("message", "未查询到该航班");
				return returnResultMap;
			}
		}
		flightParamMap.put("name",secondFlightParamMap.get("flightNo"));
		// 获取的的去程航班返回信息
		Map<String, String> secondFlightMap = lineFlightService.getEachFlightInfo(flightParamMap);
		if(secondFlightMap == null){
			flightParamMap.put("date",DateUtil.changeDay(new Date(), 1, true));
			secondFlightMap = lineFlightService.getEachFlightInfo(flightParamMap);
			if(secondFlightMap ==null ){
				returnResultMap.put("status", false);
				returnResultMap.put("message", "未查询到转机航班");
				return returnResultMap;
			}
		}
		if("1".equals(paramMap.get("isTurn"))){
			flightParamMap.put("name",firstFlightParamMap.get("turnFlightNo"));
			flightParamMap.put("key",juheAppKey);
			flightParamMap.put("date",DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
			// 获取去程的转机信息情况
			firstTurnFlightMap= lineFlightService.getEachFlightInfo(flightParamMap);
			if(firstTurnFlightMap == null){
				flightParamMap.put("date",DateUtil.changeDay(new Date(), 1, true));
				firstTurnFlightMap = lineFlightService.getEachFlightInfo(flightParamMap);
				if(firstTurnFlightMap ==null ){
					returnResultMap.put("status", false);
					returnResultMap.put("message", "未查询到转机航班");
					return returnResultMap;
				}
			}
			// 返程转机航班号
			flightParamMap.put("name",secondFlightParamMap.get("turnFlightNo")); 
			// 获取返程的转机信息情况
			secondTurnFlightMap = lineFlightService.getEachFlightInfo(flightParamMap);
			if(secondTurnFlightMap == null){
				flightParamMap.put("date",DateUtil.changeDay(new Date(), 1, true));
				secondTurnFlightMap = lineFlightService.getEachFlightInfo(flightParamMap);
				if(secondTurnFlightMap == null ){
					returnResultMap.put("status", false);
					returnResultMap.put("message", "未查询到转机航班");
					return returnResultMap;
				}
			}
		}
		// 去程航班时间段
		String firstTimeSpeace = firstFlightParamMap.get("timeSpace").replace("[", "").replace("]", "");
		String [] firstTimeSpeaceArray = firstTimeSpeace.split(",");
		// 返程航班时间段
		String secondTimeSpeace = secondFlightParamMap.get("timeSpace").replace("[", "").replace("]", "");
		String [] secondTimeSpeaceArray = secondTimeSpeace.split(",");
		for (int i = 0; i < firstTimeSpeaceArray.length; i++) {
			String groupId = UUID.randomUUID().toString();
			paramMap.put("groupId", groupId);
			String [] firstStrArray = firstTimeSpeaceArray[i].replace("\"", "").split("/");
			String [] secondStrArray = secondTimeSpeaceArray[i].replace("\"", "").split("/");
			for (int j = 0; j < firstStrArray.length; j++) {
				System.out.println("===="+firstStrArray[j]);
				ApinJourneyIdSequence journeyIdSequence = new ApinJourneyIdSequence();
				journeyIdDao.insertJourneyIdSequence(journeyIdSequence);
				// 航班主体信息
				ApinMerchantFlightInfo mainFlight = PublishFlightUtils.packMain(paramMap, userId);
				mainFlight.setJourneyId(journeyIdSequence.getJourneyId());
				// 判断当前航班属于国内还是国际
				if(PublishFlightUtils.returnFlightType(firstFlightMap.get("departAirportCode"), firstFlightMap.get("arriveAirportCode")) == 1 || 
						(firstTurnFlightMap!=null && PublishFlightUtils.returnFlightType(firstTurnFlightMap.get("departAirportCode"), firstTurnFlightMap.get("arriveAirportCode")) == 1)){
					mainFlight.setFlightType((byte)1);
				}else{
					mainFlight.setFlightType((byte)0);
				}
				mainFlightInfoDao.insert(mainFlight);
				if("1".equals(paramMap.get("isTurn"))){
					// 得出航班详情 关于去程的相关信息   包括去程的转机
					ApinMerchantFlightDetailInfo firstDetailInfo = PublishFlightUtils.packDetailWhenTurn(firstFlightMap, firstTurnFlightMap);
					firstDetailInfo.setJourneyId(mainFlight.getJourneyId());
					firstDetailInfo.setParentId(0);
					firstDetailInfo.setTripNumber((byte)0);
					firstDetailInfo.setDepartDate(DateUtil.toFormatDate(firstStrArray[j], "yyyy-MM-dd"));
					firstDetailInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], firstFlightMap.get("departTime"), firstTurnFlightMap.get("arriveTime")));
					detailInfoDao.insert(firstDetailInfo);
					ApinMerchantFlightTurnInfo firstTurnInfo = PublishFlightUtils.packageTurnInfo(firstFlightMap, firstTurnFlightMap);
					firstTurnInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], firstFlightMap.get("departTime"), firstFlightMap.get("arriveTime")));
					firstTurnInfo.setDepartDate(PublishFlightUtils.getArriveDate(DateUtil.dateToStr(firstTurnInfo.getArriveDate(), "yyyy-MM-dd"),
							firstFlightMap.get("arriveTime"), firstTurnFlightMap.get("departTime")));
					firstTurnInfo.setFlightId(firstDetailInfo.getId());
					turnInfoDao.insert(firstTurnInfo);
					// 得出关于回城的相关航班信息包括回城的转机信息
					ApinMerchantFlightDetailInfo secondDetailInfo = PublishFlightUtils.packDetailWhenTurn(secondFlightMap, secondTurnFlightMap);
					secondDetailInfo.setJourneyId(mainFlight.getJourneyId());
					secondDetailInfo.setParentId(firstDetailInfo.getId());
					secondDetailInfo.setTripNumber((byte)1);
					secondDetailInfo.setDepartDate(DateUtil.toFormatDate(secondStrArray[j], "yyyy-MM-dd"));
					secondDetailInfo.setArriveDate(PublishFlightUtils.getArriveDate(secondStrArray[j], secondFlightMap.get("departTime"), secondTurnFlightMap.get("arriveTime")));
					detailInfoDao.insert(secondDetailInfo);
					ApinMerchantFlightTurnInfo secondTurnInfo = PublishFlightUtils.packageTurnInfo(secondFlightMap, secondTurnFlightMap);
					secondTurnInfo.setArriveDate(PublishFlightUtils.getArriveDate(secondStrArray[j], secondFlightMap.get("departTime"), secondTurnFlightMap.get("arriveTime")));
					secondTurnInfo.setDepartDate(PublishFlightUtils.getArriveDate(DateUtil.dateToStr(secondTurnInfo.getArriveDate(), "yyyy-MM-dd"),
							secondFlightMap.get("arriveTime"), secondTurnFlightMap.get("departTime")));
					secondTurnInfo.setFlightId(secondDetailInfo.getId());
					turnInfoDao.insert(secondTurnInfo);
				}else {
					// 无转机时 获取去程的航班信息
					ApinMerchantFlightDetailInfo firstDetailInfo = PublishFlightUtils.packDetail(firstFlightMap);
					firstDetailInfo.setJourneyId(mainFlight.getJourneyId());
					firstDetailInfo.setParentId(0);
					firstDetailInfo.setTripNumber((byte)0);
					firstDetailInfo.setDepartDate(DateUtil.toFormatDate(firstStrArray[j], "yyyy-MM-dd"));
					firstDetailInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], firstFlightMap.get("departTime"), firstFlightMap.get("arriveTime")));
					// 去程有经停的航班信息 需要将经停信息放入 转机表中
					if("1".equals(firstFlightMap.get("isMiddleStop"))){
						firstDetailInfo.setHasTurn((byte)2);
						detailInfoDao.insert(firstDetailInfo);
						// 经停信息
						ApinMerchantFlightTurnInfo firstMiddleInfo = PublishFlightUtils.middleStopInfo(firstFlightMap);
						firstMiddleInfo.setFlightId(firstDetailInfo.getId());
						firstMiddleInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], firstFlightMap.get("departTime"), firstFlightMap.get("middleArriveTime")));
						firstMiddleInfo.setDepartDate(PublishFlightUtils.getArriveDate(DateUtil.dateToStr(firstMiddleInfo.getArriveDate(), "yyyy-MM-dd"),
								firstFlightMap.get("middleArriveTime"), firstFlightMap.get("middleDepartTime")));
						turnInfoDao.insert(firstMiddleInfo);
					}else{
						firstDetailInfo.setHasTurn((byte)0);
						detailInfoDao.insert(firstDetailInfo);
					}
					
					// 无转机时 返程的航班信息
					ApinMerchantFlightDetailInfo secondDetailInfo = PublishFlightUtils.packDetail(secondFlightMap);
					secondDetailInfo.setJourneyId(mainFlight.getJourneyId());
					secondDetailInfo.setParentId(firstDetailInfo.getId());
					secondDetailInfo.setTripNumber((byte)1);
					secondDetailInfo.setDepartDate(DateUtil.toFormatDate(secondStrArray[j], "yyyy-MM-dd"));
					secondDetailInfo.setArriveDate(PublishFlightUtils.getArriveDate(secondStrArray[j], secondFlightMap.get("departTime"), secondFlightMap.get("arriveTime")));
					// 返程时有经停的信息需要将经停的信息 放入转机表中
					if("1".equals(secondFlightMap.get("isMiddleStop"))){
						secondDetailInfo.setHasTurn((byte)2);
						detailInfoDao.insert(secondDetailInfo);
						// 经停信息
						ApinMerchantFlightTurnInfo secondMiddleInfo =PublishFlightUtils.middleStopInfo(secondFlightMap);
						secondMiddleInfo.setFlightId(secondDetailInfo.getId());
						secondMiddleInfo.setArriveDate(PublishFlightUtils.getArriveDate(secondStrArray[j], secondFlightMap.get("departTime"), secondFlightMap.get("middleArriveTime")));
						secondMiddleInfo.setDepartDate(PublishFlightUtils.getArriveDate(DateUtil.dateToStr(secondMiddleInfo.getArriveDate(), "yyyy-MM-dd"),
								secondFlightMap.get("middleArriveTime"), secondFlightMap.get("middleDepartTime")));
						turnInfoDao.insert(secondMiddleInfo);
					}else{
						secondDetailInfo.setHasTurn((byte)0);
						detailInfoDao.insert(secondDetailInfo);
					}
				}
			}
		}
		returnResultMap.put("status", true);
		returnResultMap.put("message", "操作成功");
		return returnResultMap;
	}
	
	
	
	/**
	 * 改版后的供应商发布航线
	 * @param paramMap
	 * @param flightMapList
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Exception.class)
	public Map<String, Object> addRoundFlightVersion(Map<String, String> paramMap, List<Map<String, String>> flightMapList,
			String userId) throws Exception {
		Map<String, Object> returnResultMap =new HashMap<>();
		if(flightMapList.size() != 2){
			returnResultMap.put("status", false);
			returnResultMap.put("message", "参数错误");
			return returnResultMap;
		}
	    // 聚合查询参数
		Map<String, Object> flightParamMap = new HashMap<>();
		// 提交的去程参数信息
		Map<String,String> firstFlightParamMap = flightMapList.get(0);
		// 访问接口 提交的 回城航班信息
		Map<String, String> secondFlightParamMap = flightMapList.get(1);
		// 去程信息
		Map<String, String> firstFlightMap = null;
		// 去程转机信息
		Map<String, String> firstTurnFlightMap = null;
		// 返程信息
		Map<String, String> secondFlightMap =null;
		// 返程转机信息
		Map<String, String> secondTurnFlightMap = null;
		if("1".equals(paramMap.get("isTurn"))){
			flightParamMap.put("dtype","");
			flightParamMap.put("name",firstFlightParamMap.get("flightNo"));
			flightParamMap.put("departPlaceCode", firstFlightParamMap.get("departPlaceCode"));
			flightParamMap.put("destPlaceCode", firstFlightParamMap.get("turnCityCode"));
			flightParamMap.put("key",juheAppKey);
			flightParamMap.put("date",DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
			// 获取去程航班返回信息
			firstFlightMap = lineFlightVersionService.getEachFlightInfo(flightParamMap);
			if(firstFlightMap == null){
				flightParamMap.put("date",DateUtil.changeDay(new Date(), 1, true));
				firstFlightMap = lineFlightVersionService.getEachFlightInfo(flightParamMap);
				if(firstFlightMap == null ){
					returnResultMap.put("status", false);
					returnResultMap.put("message", "未查询到该航班");
					return returnResultMap;
				}
			}
			// 查询返程航班信息所需参数
			flightParamMap.put("name",secondFlightParamMap.get("flightNo"));
			flightParamMap.put("departPlaceCode", secondFlightParamMap.get("departPlaceCode"));
			flightParamMap.put("destPlaceCode", secondFlightParamMap.get("turnCityCode"));
			// 获取的的去程航班返回信息
		    secondFlightMap = lineFlightVersionService.getEachFlightInfo(flightParamMap);
			if(secondFlightMap == null){
				flightParamMap.put("date",DateUtil.changeDay(new Date(), 1, true));
				secondFlightMap = lineFlightVersionService.getEachFlightInfo(flightParamMap);
				if(secondFlightMap ==null ){
					returnResultMap.put("status", false);
					returnResultMap.put("message", "未查询到转机航班");
					return returnResultMap;
				}
			}
			// 查询去程转机 所需参数
			flightParamMap.put("name",firstFlightParamMap.get("turnFlightNo"));
			flightParamMap.put("departPlaceCode", firstFlightParamMap.get("turnCityCode"));
			flightParamMap.put("destPlaceCode", firstFlightParamMap.get("destPlaceCode"));
			// 获取去程的转机信息情况
			firstTurnFlightMap= lineFlightVersionService.getEachFlightInfo(flightParamMap);
			if(firstTurnFlightMap == null){
				flightParamMap.put("date",DateUtil.changeDay(new Date(), 1, true));
				firstTurnFlightMap = lineFlightVersionService.getEachFlightInfo(flightParamMap);
				if(firstTurnFlightMap ==null ){
					returnResultMap.put("status", false);
					returnResultMap.put("message", "未查询到转机航班");
					return returnResultMap;
				}
			}
			// 查询返程转机 所需参数
			flightParamMap.put("name",secondFlightParamMap.get("turnFlightNo")); 
			flightParamMap.put("departPlaceCode", secondFlightParamMap.get("turnCityCode"));
			flightParamMap.put("destPlaceCode", secondFlightParamMap.get("destPlaceCode"));
			// 获取返程的转机信息情况
			secondTurnFlightMap = lineFlightVersionService.getEachFlightInfo(flightParamMap);
			if(secondTurnFlightMap == null){
				flightParamMap.put("date",DateUtil.changeDay(new Date(), 1, true));
				secondTurnFlightMap = lineFlightVersionService.getEachFlightInfo(flightParamMap);
				if(secondTurnFlightMap == null ){
					returnResultMap.put("status", false);
					returnResultMap.put("message", "未查询到转机航班");
					return returnResultMap;
				}
			}
			
		}else{// 非转机情况
			flightParamMap.put("dtype","");
			flightParamMap.put("name",firstFlightParamMap.get("flightNo"));
			flightParamMap.put("departPlaceCode", firstFlightParamMap.get("departPlaceCode"));
			flightParamMap.put("destPlaceCode", firstFlightParamMap.get("destPlaceCode"));
			flightParamMap.put("key",juheAppKey);
			flightParamMap.put("date",DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
			// 获取去程航班返回信息
			firstFlightMap = lineFlightVersionService.getEachFlightInfo(flightParamMap);
			if(firstFlightMap == null){
				flightParamMap.put("date",DateUtil.changeDay(new Date(), 1, true));
				firstFlightMap = lineFlightVersionService.getEachFlightInfo(flightParamMap);
				if(firstFlightMap == null ){
					returnResultMap.put("status", false);
					returnResultMap.put("message", "未查询到该航班");
					return returnResultMap;
				}
			}
			// 查询返程航班信息所需参数
			flightParamMap.put("name",secondFlightParamMap.get("flightNo"));
			flightParamMap.put("departPlaceCode", secondFlightParamMap.get("departPlaceCode"));
			flightParamMap.put("destPlaceCode", secondFlightParamMap.get("destPlaceCode"));
			// 获取的的去程航班返回信息
		    secondFlightMap = lineFlightVersionService.getEachFlightInfo(flightParamMap);
			if(secondFlightMap == null){
				flightParamMap.put("date",DateUtil.changeDay(new Date(), 1, true));
				secondFlightMap = lineFlightVersionService.getEachFlightInfo(flightParamMap);
				if(secondFlightMap ==null ){
					returnResultMap.put("status", false);
					returnResultMap.put("message", "未查询到转机航班");
					return returnResultMap;
				}
			}
		}
		// 去程航班时间段
		String firstTimeSpeace = firstFlightParamMap.get("timeSpace").replace("[", "").replace("]", "");
		String [] firstTimeSpeaceArray = firstTimeSpeace.split(",");
		// 返程航班时间段
		String secondTimeSpeace = secondFlightParamMap.get("timeSpace").replace("[", "").replace("]", "");
		String [] secondTimeSpeaceArray = secondTimeSpeace.split(",");
		for (int i = 0; i < firstTimeSpeaceArray.length; i++) {
			String groupId = UUID.randomUUID().toString();
			paramMap.put("groupId", groupId);
			String [] firstStrArray = firstTimeSpeaceArray[i].replace("\"", "").split("/");
			String [] secondStrArray = secondTimeSpeaceArray[i].replace("\"", "").split("/");
			for (int j = 0; j < firstStrArray.length; j++) {
				ApinJourneyIdSequence journeyIdSequence = new ApinJourneyIdSequence();
				journeyIdDao.insertJourneyIdSequence(journeyIdSequence);
				// 航班主体信息
				ApinMerchantFlightInfo mainFlight = PublishFlightUtils.packMain(paramMap, userId);
				mainFlight.setJourneyId(journeyIdSequence.getJourneyId());
				// 判断当前航班属于国内还是国际
				if(PublishFlightUtils.returnFlightType(firstFlightMap.get("departAirportCode"), firstFlightMap.get("arriveAirportCode")) == 1 || 
						(firstTurnFlightMap!=null && PublishFlightUtils.returnFlightType(firstTurnFlightMap.get("departAirportCode"), firstTurnFlightMap.get("arriveAirportCode")) == 1)){
					mainFlight.setFlightType((byte)1);
				}else{
					mainFlight.setFlightType((byte)0);
				}
				mainFlightInfoDao.insert(mainFlight);
				if("1".equals(paramMap.get("isTurn"))){
					// 得出航班详情 关于去程的相关信息   包括去程的转机
					ApinMerchantFlightDetailInfo firstDetailInfo = PublishFlightUtils.packDetailWhenTurn(firstFlightMap, firstTurnFlightMap);
					firstDetailInfo.setJourneyId(mainFlight.getJourneyId());
					firstDetailInfo.setParentId(0);
					firstDetailInfo.setTripNumber((byte)0);
					firstDetailInfo.setDepartDate(DateUtil.toFormatDate(firstStrArray[j], "yyyy-MM-dd"));
					firstDetailInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], firstFlightMap.get("departTime"), firstTurnFlightMap.get("arriveTime")));
					detailInfoDao.insert(firstDetailInfo);
					ApinMerchantFlightTurnInfo firstTurnInfo = PublishFlightUtils.packageTurnInfo(firstFlightMap, firstTurnFlightMap);
					firstTurnInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], firstFlightMap.get("departTime"), firstFlightMap.get("arriveTime")));
					firstTurnInfo.setDepartDate(PublishFlightUtils.getArriveDate(DateUtil.dateToStr(firstTurnInfo.getArriveDate(), "yyyy-MM-dd"),
							firstFlightMap.get("arriveTime"), firstTurnFlightMap.get("departTime")));
					firstTurnInfo.setFlightId(firstDetailInfo.getId());
					turnInfoDao.insert(firstTurnInfo);
					// 得出关于回城的相关航班信息包括回城的转机信息
					ApinMerchantFlightDetailInfo secondDetailInfo = PublishFlightUtils.packDetailWhenTurn(secondFlightMap, secondTurnFlightMap);
					secondDetailInfo.setJourneyId(mainFlight.getJourneyId());
					secondDetailInfo.setParentId(firstDetailInfo.getId());
					secondDetailInfo.setTripNumber((byte)1);
					secondDetailInfo.setDepartDate(DateUtil.toFormatDate(secondStrArray[j], "yyyy-MM-dd"));
					secondDetailInfo.setArriveDate(PublishFlightUtils.getArriveDate(secondStrArray[j], secondFlightMap.get("departTime"), secondTurnFlightMap.get("arriveTime")));
					detailInfoDao.insert(secondDetailInfo);
					ApinMerchantFlightTurnInfo secondTurnInfo = PublishFlightUtils.packageTurnInfo(secondFlightMap, secondTurnFlightMap);
					secondTurnInfo.setArriveDate(PublishFlightUtils.getArriveDate(secondStrArray[j], secondFlightMap.get("departTime"), secondTurnFlightMap.get("arriveTime")));
					secondTurnInfo.setDepartDate(PublishFlightUtils.getArriveDate(DateUtil.dateToStr(secondTurnInfo.getArriveDate(), "yyyy-MM-dd"),
							secondFlightMap.get("arriveTime"), secondTurnFlightMap.get("departTime")));
					secondTurnInfo.setFlightId(secondDetailInfo.getId());
					turnInfoDao.insert(secondTurnInfo);
				}else {
					// 无转机时 获取去程的航班信息
					ApinMerchantFlightDetailInfo firstDetailInfo = PublishFlightUtils.packDetail(firstFlightMap);
					firstDetailInfo.setJourneyId(mainFlight.getJourneyId());
					firstDetailInfo.setParentId(0);
					firstDetailInfo.setTripNumber((byte)0);
					firstDetailInfo.setDepartDate(DateUtil.toFormatDate(firstStrArray[j], "yyyy-MM-dd"));
					firstDetailInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], firstFlightMap.get("departTime"), firstFlightMap.get("arriveTime")));
					// 去程有经停的航班信息 需要将经停信息放入 转机表中
					if("1".equals(firstFlightMap.get("isMiddleStop"))){
						firstDetailInfo.setHasTurn((byte)2);
						detailInfoDao.insert(firstDetailInfo);
						// 经停信息
						ApinMerchantFlightTurnInfo firstMiddleInfo = PublishFlightUtils.middleStopInfo(firstFlightMap);
						firstMiddleInfo.setFlightId(firstDetailInfo.getId());
						firstMiddleInfo.setArriveDate(PublishFlightUtils.getArriveDate(firstStrArray[j], firstFlightMap.get("departTime"), firstFlightMap.get("middleArriveTime")));
						firstMiddleInfo.setDepartDate(PublishFlightUtils.getArriveDate(DateUtil.dateToStr(firstMiddleInfo.getArriveDate(), "yyyy-MM-dd"),
								firstFlightMap.get("middleArriveTime"), firstFlightMap.get("middleDepartTime")));
						turnInfoDao.insert(firstMiddleInfo);
					}else{
						firstDetailInfo.setHasTurn((byte)0);
						detailInfoDao.insert(firstDetailInfo);
					}
					// 无转机时 返程的航班信息
					ApinMerchantFlightDetailInfo secondDetailInfo = PublishFlightUtils.packDetail(secondFlightMap);
					secondDetailInfo.setJourneyId(mainFlight.getJourneyId());
					secondDetailInfo.setParentId(firstDetailInfo.getId());
					secondDetailInfo.setTripNumber((byte)1);
					secondDetailInfo.setDepartDate(DateUtil.toFormatDate(secondStrArray[j], "yyyy-MM-dd"));
					secondDetailInfo.setArriveDate(PublishFlightUtils.getArriveDate(secondStrArray[j], secondFlightMap.get("departTime"), secondFlightMap.get("arriveTime")));
					// 返程时有经停的信息需要将经停的信息 放入转机表中
					if("1".equals(secondFlightMap.get("isMiddleStop"))){
						secondDetailInfo.setHasTurn((byte)2);
						detailInfoDao.insert(secondDetailInfo);
						// 经停信息
						ApinMerchantFlightTurnInfo secondMiddleInfo =PublishFlightUtils.middleStopInfo(secondFlightMap);
						secondMiddleInfo.setFlightId(secondDetailInfo.getId());
						secondMiddleInfo.setArriveDate(PublishFlightUtils.getArriveDate(secondStrArray[j], secondFlightMap.get("departTime"), secondFlightMap.get("middleArriveTime")));
						secondMiddleInfo.setDepartDate(PublishFlightUtils.getArriveDate(DateUtil.dateToStr(secondMiddleInfo.getArriveDate(), "yyyy-MM-dd"),
								secondFlightMap.get("middleArriveTime"), secondFlightMap.get("middleDepartTime")));
						turnInfoDao.insert(secondMiddleInfo);
					}else{
						secondDetailInfo.setHasTurn((byte)0);
						detailInfoDao.insert(secondDetailInfo);
					}
				}
			}
		}
		returnResultMap.put("status", true);
		returnResultMap.put("message", "操作成功");
		return returnResultMap;
	}
	
}
