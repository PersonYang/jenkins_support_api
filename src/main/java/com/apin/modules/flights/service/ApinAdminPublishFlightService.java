package com.apin.modules.flights.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.apin.base.bean.DictAirport;
import com.apin.base.service.AirLineFlightService;
import com.apin.common.global.DictSource;
import com.apin.common.response.ApinResponse;
import com.apin.common.response.ApinResponseUtil;
import com.apin.common.response.ErrorEnum;
import com.apin.common.utils.DateUtil;
import com.apin.common.utils.JsonUtil;
import com.apin.common.utils.ValidateParamUtils;
import com.apin.modules.flights.bean.ApinJourneyIdSequence;
import com.apin.modules.flights.bean.ApinMerchantFlightDetailInfo;
import com.apin.modules.flights.bean.ApinMerchantFlightInfo;
import com.apin.modules.flights.bean.ApinMerchantFlightTurnInfo;
import com.apin.modules.flights.dao.ApinJourneyIdSequenceDao;
import com.apin.modules.flights.dao.ApinMerchantFlightDetailInfoDao;
import com.apin.modules.flights.dao.ApinMerchantFlightInfoDao;
import com.apin.modules.flights.dao.ApinMerchantFlightTurnInfoDao;

/**
 * 
 * 用于支持后台系统的
 * 航班添加服务
 * @author Young
 * @date 2017年1月25日
 */
@Service
public class ApinAdminPublishFlightService {
	
	private static final Logger logger = LoggerFactory.getLogger(ApinAdminPublishFlightService.class);

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
	 * 后台系统添加航班信息
	 * @param userId
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> publishFlightFromAdmin(String param) {
		long requestTime = System.currentTimeMillis();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ApinResponse<Map<String, Object>> apinResponse = null;
		try {
			Map<String, String> paramMap = JsonUtil.parseToMap(param);
			if (!ValidateParamUtils.validateFlightParamFromAdmin(param)
					|| StringUtils.isBlank(paramMap.get("userId"))) {
				logger.info("==========some param is blank in publishFlightFromAdmin========");
				apinResponse = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				resultMap.put("status", false);
				apinResponse.setBody(resultMap);
				return apinResponse;
			}
			apinResponse = ApinResponseUtil.good(requestTime);
			List<Map<String, String>> flightMapList = JsonUtil.parseToMapInList(paramMap.get("flightDetail"));
			Map<String, Object> flightParamMap = new HashMap<>();
			// 航班类型 国内航班 国际航班
			Byte flightType = 0;
			Byte tripNumber = 0;
			Integer parentId = 0;
			// 航班信息
			for (Map<String, String> map : flightMapList) {
				flightParamMap.put("dtype", "");
				flightParamMap.put("name", map.get("flightNo"));
				flightParamMap.put("key", juheAppKey);
				flightParamMap.put("date", DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
				// 获取每一程的航班信息
				Map<String, String> flightMap = lineFlightService.getEachFlightInfo(flightParamMap);
				if (flightMap == null) {
					resultMap.put("status", false);
					resultMap.put("message", "未查询到该航班");
					apinResponse.setBody(resultMap);
					return apinResponse;
				}
				flightType = returnFlightType(flightMap.get("departAirportCode"), flightMap.get("arriveAirportCode"));
				if (flightType == 1) {
					break;
				}
			}
			// 航班主题信息 需要标识出这是临时团的航班信息
			ApinJourneyIdSequence journeyIdSequence = new ApinJourneyIdSequence();
			journeyIdDao.insertJourneyIdSequence(journeyIdSequence);
			String groupId = UUID.randomUUID().toString();
			ApinMerchantFlightInfo mainFlightInfo = packMain(paramMap, paramMap.get("userId"));
			mainFlightInfo.setJourneyId(journeyIdSequence.getJourneyId());
			mainFlightInfo.setGroupId(groupId);
			mainFlightInfo.setIsFareTicket((byte) 1);
			mainFlightInfo.setFlightType(flightType);
			mainFlightInfoDao.insert(mainFlightInfo);
			List<ApinMerchantFlightDetailInfo> detailList = new ArrayList<>();
			for (Map<String, String> map : flightMapList) {
				String departDate = map.get("departDate");
				flightParamMap.put("dtype", "");
				flightParamMap.put("name", map.get("flightNo"));
				flightParamMap.put("key", juheAppKey);
				flightParamMap.put("date", DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
				// 获取每一程的航班信息
				Map<String, String> flightMap = lineFlightService.getEachFlightInfo(flightParamMap);
				if (flightMap == null) {
					resultMap.put("status", false);
					resultMap.put("message", "未查询到该航班");
					apinResponse.setBody(resultMap);
					return apinResponse;
				}
				// 无转机时 获取去程的航班信息
				ApinMerchantFlightDetailInfo firstDetailInfo = packDetail(flightMap);
				firstDetailInfo.setJourneyId(mainFlightInfo.getJourneyId());
				firstDetailInfo.setParentId(parentId);
				firstDetailInfo.setTripNumber(tripNumber);
				firstDetailInfo.setDepartDate(DateUtil.toFormatDate(departDate, "yyyy-MM-dd"));
				firstDetailInfo.setArriveDate(
						getArriveDate(departDate, flightMap.get("departTime"), flightMap.get("arriveTime")));
				// 去程有经停的航班信息 需要将经停信息放入 转机表中
				if ("1".equals(flightMap.get("isMiddleStop"))) {
					firstDetailInfo.setHasTurn((byte) 2);
					detailInfoDao.insert(firstDetailInfo);
					// 经停信息
					ApinMerchantFlightTurnInfo firstMiddleInfo = middleStopInfo(flightMap);
					firstMiddleInfo.setFlightId(firstDetailInfo.getId());
					firstMiddleInfo.setArriveDate(
							getArriveDate(departDate, flightMap.get("departTime"), flightMap.get("middleArriveTime")));
					firstMiddleInfo.setDepartDate(
							getArriveDate(DateUtil.dateToStr(firstMiddleInfo.getArriveDate(), "yyyy-MM-dd"),
									flightMap.get("middleArriveTime"), flightMap.get("middleDepartTime")));
					turnInfoDao.insert(firstMiddleInfo);
				} else {
					firstDetailInfo.setHasTurn((byte) 0);
					detailInfoDao.insert(firstDetailInfo);
				}
				detailList.add(firstDetailInfo);
				parentId = firstDetailInfo.getId();
				tripNumber = (byte) (tripNumber + (byte) 1);
			}
			if (detailList != null && detailList.size() > 0) {
				Map<String, Object> adminReturnMap = returnAdminFlightInfo(detailList.get(0));
				resultMap.put("flightInfo", adminReturnMap);
			}
			apinResponse = ApinResponseUtil.good(requestTime);
			apinResponse.setBody(resultMap);
			return apinResponse;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("==========the method publishFlightFromAdmin get somthing wrong ========");
			apinResponse = ApinResponseUtil.good(requestTime);
			resultMap.put("status", false);
			resultMap.put("message", "网络异常，请稍后再试！");
			apinResponse.setBody(resultMap);
			return apinResponse;
		}
	}
	
	public Map<String, Object> returnAdminFlightInfo(ApinMerchantFlightDetailInfo detailInfo){
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("journeyId", detailInfo.getJourneyId());
		resultMap.put("departPlace", detailInfo.getDepartPlace());
		resultMap.put("departTime", detailInfo.getDepartTime());
		resultMap.put("departAirport", detailInfo.getDepartAirport());
		resultMap.put("destPlace", detailInfo.getDestPlace());
		resultMap.put("arriveTime", detailInfo.getArriveTime());
		resultMap.put("arriveAirport", detailInfo.getArriveAirport());
		resultMap.put("hasTurn", detailInfo.getHasTurn());
		resultMap.put("airComp", detailInfo.getAirComp());
		resultMap.put("flightNo", detailInfo.getFlightNo());
		return resultMap;
	}
	
	
	
	
	/**
	 * 封装航班主体信息
	 * 满足后台系统的调用
	 * @param paramMap
	 * @param userId
	 * @param flightMap
	 * @return
	 */
	public ApinMerchantFlightInfo packMain(Map<String, String> paramMap,String userId){
		ApinMerchantFlightInfo mainFlightInfo = new ApinMerchantFlightInfo();
		mainFlightInfo.setGroupId(paramMap.get("groupId"));
		mainFlightInfo.setIsAudit((byte)1);
		//mainFlightInfo.setCurrentTicketNum(Integer.valueOf(paramMap.get("ticketNum")));
		//mainFlightInfo.setTotalPriceInctax(new BigDecimal(paramMap.get("price")));
		mainFlightInfo.setIsSpecialTicket(Byte.valueOf(paramMap.get("isSpecialTicket")));
		mainFlightInfo.setIsFareTicket((byte)0);
		mainFlightInfo.setSupplierId(userId);
		mainFlightInfo.setBaggageRules(paramMap.get("baggageRules"));
		mainFlightInfo.setCreateTime(new Date());
		//mainFlightInfo.setFlightType(returnFlightType(flightMap.get("departAirportCode"), flightMap.get("arriveAirportCode")));
		mainFlightInfo.setRouteType(Byte.valueOf(paramMap.get("flightType")));
		mainFlightInfo.setIsShelves((byte)1);
		mainFlightInfo.setIsFareTicket((byte)1);
		mainFlightInfo.setVersion(1);
		return mainFlightInfo;
	}
	

	/**
	 * 用户后台系统的调用情况
	 * 封装航班详情信息  
	 * 没有转机的时候信息
	 * @param flightMap
	 * @return
	 */
	public ApinMerchantFlightDetailInfo packDetail(Map<String, String> flightMap) {
		ApinMerchantFlightDetailInfo detailInfo = new ApinMerchantFlightDetailInfo();
		detailInfo.setAirComp(flightMap.get("airComp"));
		detailInfo.setFlightNo(flightMap.get("flightNo"));
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
	 * 用于后台系统的调用
	 * detailInfo  when turn 
	 * 中转 时 中装 封装航班
	 * 转机 封装航班详情信息
	 * @param flightMap
	 * @return
	 */
	public ApinMerchantFlightDetailInfo packDetailWhenTurn(Map<String, String> flightMap,
			Map<String, String> turnFlighttMap) {
		ApinMerchantFlightDetailInfo detailInfo = new ApinMerchantFlightDetailInfo();
		detailInfo.setAirComp(flightMap.get("airComp"));
		detailInfo.setFlightNo(flightMap.get("flightNo").toUpperCase());
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
	 * 封装经停信息
	 * @param flightMap
	 * @return
	 */
	public ApinMerchantFlightTurnInfo middleStopInfo(Map<String, String> flightMap) {
		ApinMerchantFlightTurnInfo turnInfo = new ApinMerchantFlightTurnInfo();
		// 抵达转机城市的情况
		turnInfo.setArriveAirport(flightMap.get("arriveAirport"));
		turnInfo.setArriveAirComp(flightMap.get("airComp"));
		turnInfo.setArriveFlightNo(flightMap.get("flightNo").toUpperCase());
		turnInfo.setArriveTime(flightMap.get("arriveTime"));
		//从转机城市出发的情况
		turnInfo.setDepartAirComp(flightMap.get("airComp"));
		turnInfo.setDepartAirport(flightMap.get("middleAirport"));
		turnInfo.setTurnCity(flightMap.get("middleCity"));
		turnInfo.setDepartFlightNo(flightMap.get("flightNo").toUpperCase());
		turnInfo.setDepartTime(flightMap.get("middleDepartTime"));
		turnInfo.setStayTime(DateUtil.redurnStayTime(flightMap.get("middleArriveTime"), flightMap.get("middleDepartTime"), "HH:mm", "hm"));
		return turnInfo;
	}
	
	/**
	 * turnInfo
	 * @param turnFlighttMap
	 * @return
	 */
	public ApinMerchantFlightTurnInfo packageTurnInfo(Map<String, String> flightMap,
			Map<String, String> turnFlighttMap) {
		ApinMerchantFlightTurnInfo turnInfo = new ApinMerchantFlightTurnInfo();
		// 抵达转机城市的情况
		turnInfo.setArriveAirport(flightMap.get("arriveAirport"));
		turnInfo.setArriveAirComp(flightMap.get("airComp"));
		turnInfo.setArriveFlightNo(flightMap.get("flightNo").toUpperCase());
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
	
	
	/**
	 * 获取抵达日期
	 * @param departDate 出发日期
	 * @param departTime 出发时间
	 * @param arriveTime 抵达时间
	 * @return
	 */
	public Date getArriveDate(String departDate,String departTime,String arriveTime){
		int with24 = DateUtil.retrurnBetween(departDate+" "+departTime, departDate+" 23:59", "yyyy-MM-dd HH:mm", "h");
		int withArrive = DateUtil.retrurnBetween(departDate+" "+departTime, departDate+" "+arriveTime, "yyyy-MM-dd HH:mm", "h");
		if(with24 >= withArrive){
			return DateUtil.toFormatDate(departDate, "yyyy-MM-dd");
		}else{
			return DateUtil.changeDay(DateUtil.toFormatDate(departDate, "yyyy-MM-dd"), 1, true);
		}
	}
	
	/**
	 * 航班属于国内 还是国际线路
	 * @param departCode 出发机场代码
	 * @param destCode 目的机场代码
	 * @return 国内航班返回0，国际航班返回1.
	 */
	public Byte returnFlightType(String departCode,String destCode){
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
