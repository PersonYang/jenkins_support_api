package com.apin.modules.flights.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.apin.common.response.ApinResponse;
import com.apin.common.response.ApinResponseUtil;
import com.apin.common.response.ErrorEnum;
import com.apin.common.utils.JsonUtil;
import com.apin.common.utils.PageCommon;
import com.apin.common.utils.ValidateParamUtils;
import com.apin.modules.flights.bean.ApinMerchantFlightDetailInfo;
import com.apin.modules.flights.bean.ApinMerchantFlightInfo;
import com.apin.modules.flights.bean.ApinMerchantFlightTurnInfo;
import com.apin.modules.flights.dao.ApinMerchantFlightDetailInfoDao;
import com.apin.modules.flights.dao.ApinMerchantFlightInfoDao;
import com.apin.modules.flights.dao.ApinMerchantFlightTurnInfoDao;

/**
 * 供应商航班相关
 * @author Young
 * @date 2017年1月16日 上午9:31:36
 * @version 1.0 
 */
@Service
public class ApinFlightService {

	private Logger logger = LoggerFactory.getLogger(ApinFlightService.class);

	@Autowired
	ApinMerchantFlightInfoDao flightInfoDao;
	@Autowired
	ApinMerchantFlightDetailInfoDao flightDetailDao;
	@Autowired
	ApinMerchantFlightTurnInfoDao flightTurnInfoDao;
	@Autowired
	ApinPublishFlightService publishService;
	/**
	 * 发布航班情况
	 * @param userId
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> publishFlight(String param) {
		long requestTime =System.currentTimeMillis();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ApinResponse<Map<String, Object>> apinResponse =null;
		try {
			logger.info("======="+param);
			Map<String, String> paramMap = JsonUtil.parseToMap(param);
			if(!ValidateParamUtils.validateFlightParam(param)){
				logger.info("==========some param is blank ========");
				apinResponse = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				resultMap.put("status", false);
				apinResponse.setBody(resultMap);
				return apinResponse;
			}
			resultMap = publishService.resultMapFlightInfo(paramMap);
			apinResponse = ApinResponseUtil.good(requestTime);
			apinResponse.setBody(resultMap);
			return apinResponse;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("==========the method publishFlight get somthing wrong ========");
			apinResponse = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			resultMap.put("status", false);
			apinResponse.setBody(resultMap);
			return apinResponse;
		}
	}
	

	
	
	
	/**
	 * 统一获取航班详情
	 * @param journeyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Cacheable(value="myCache",key="#journeyId")
	public ApinResponse<List<Map<String, Object>>> getDetailInfo(String journeyId) {
		long requestTime =System.currentTimeMillis();
		List<Map<String, Object>> resultMapList = new ArrayList<Map<String, Object>>();
		ApinResponse<List<Map<String, Object>>> apinResponse =null;
		try {
			if(StringUtils.isBlank(journeyId)){
				logger.info("==========getDetailInfo some param is blank ========");
				apinResponse = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				return apinResponse;
			}
			apinResponse = ApinResponseUtil.good(requestTime);
			Map<String, String> searchMap = new HashMap<>();
			searchMap.put("journeyId", journeyId);
			ApinMerchantFlightInfo flightInfo = flightInfoDao.getByProperty(ApinMerchantFlightInfo.class, "journeyId", journeyId);
			List<ApinMerchantFlightDetailInfo> flightDetailList = flightDetailDao.getFlightBySearchMap(searchMap);
			for (ApinMerchantFlightDetailInfo flightDetail : flightDetailList) {
				Map<String, Object> resultMap =new HashMap<>();
				resultMap.put("airComp", flightDetail.getAirComp());
				resultMap.put("arriveAirport", flightDetail.getArriveAirport());
				resultMap.put("arriveDate", flightDetail.getArriveDate().getTime());
				resultMap.put("arriveTime", flightDetail.getArriveTime());
				resultMap.put("departAirport", flightDetail.getDepartAirport());
				resultMap.put("departDate", flightDetail.getDepartDate().getTime());
				resultMap.put("departPlace", flightDetail.getDepartPlace());
				resultMap.put("departTime", flightDetail.getDepartTime());
				resultMap.put("destPlace", flightDetail.getDestPlace());
				resultMap.put("flightNo", flightDetail.getFlightNo());
				resultMap.put("hasTurn", flightDetail.getHasTurn());
				resultMap.put("id", flightDetail.getId());
				resultMap.put("parentId", flightDetail.getParentId());
				if(flightInfo != null){
					resultMap.put("isSpecialTicket", flightInfo.getIsSpecialTicket());
					resultMap.put("baggageRules", flightInfo.getBaggageRules());
					resultMap.put("routeType", flightInfo.getRouteType());
				}
				if(flightDetail.getHasTurn() != (byte)0){
					searchMap.put("flightId", String.valueOf(flightDetail.getId()));
					List<ApinMerchantFlightTurnInfo> flightTurnList = flightTurnInfoDao.findListBySearchMap(searchMap);
					List<Map<String, Object>> turnMapList =new ArrayList<>();
					for (ApinMerchantFlightTurnInfo turnInfo : flightTurnList) {
						Map<String, Object> turnInfoMap = new HashMap<>();
						turnInfoMap.put("turnCity", turnInfo.getTurnCity());
						turnInfoMap.put("arriveAirport", turnInfo.getArriveAirport());
						turnInfoMap.put("arriveDate", turnInfo.getArriveDate().getTime());
						turnInfoMap.put("arriveTime", turnInfo.getArriveTime());
						turnInfoMap.put("departAirComp", turnInfo.getDepartAirComp());
						turnInfoMap.put("departAirport", turnInfo.getDepartAirport());
						turnInfoMap.put("departDate", turnInfo.getDepartDate().getTime());
						turnInfoMap.put("departTime", turnInfo.getDepartTime());
						turnInfoMap.put("flightId", turnInfo.getFlightId());
						turnInfoMap.put("flightNo", turnInfo.getDepartFlightNo());
						turnInfoMap.put("id", turnInfo.getId());
						turnInfoMap.put("stayTime", turnInfo.getStayTime());
						turnMapList.add(turnInfoMap);
					}
					resultMap.put("turnList", turnMapList);
				}
				resultMapList.add(resultMap);
			}
			apinResponse.setBody(resultMapList);
			return apinResponse;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("==========the method getDetailInfo get somthing wrong ========");
			apinResponse = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			return apinResponse;
		}
	}

	/**
	 * 获取当前供应商
	 * 的全部航班情况
	 * @param pageParam
	 * @param searchParam
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> getWholeList(String pageParam, String searchParam) {
		long requestTime = System.currentTimeMillis();
		ApinResponse<Map<String, Object>> response = null;
		try {
			Map<String, String> searchMap = JsonUtil.parseToMap(searchParam);
			Map<String, String> pageMap = JsonUtil.parseToMap(pageParam);
			Map<String, Object> resultMap = new HashMap<>();
			if (!ValidateParamUtils.validatePageParam(pageMap) || StringUtils.isBlank(searchMap.get("userId"))) {
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("=================some param is blank in getWholeList =======================");
				return response;
			}
			searchMap.put("isShelves", "1");
			searchMap.put("isFareTicket", "0");
			response = ApinResponseUtil.good(requestTime);
			long recordCount = flightInfoDao.getCountBySearchMap(searchMap);
			if (recordCount > 0) {
				PageCommon page = new PageCommon(Long.valueOf(pageMap.get("pageSize")), recordCount,
						Long.valueOf(pageMap.get("pageIndex")), pageMap.get("sortField"), pageMap.get("sortOrder"));
				List<Map<String, Object>> flightMapList = flightInfoDao.getMapListBySearchMap(page, searchMap);
				List<Map<String, Object>> newflightMapList = new ArrayList<>();
				for (Map<String, Object> map : flightMapList) {
					if("2".equals(map.get("routeType").toString())){
						Map<String, Object> backDateMap = flightDetailDao.getBackDepartDate(map.get("groupId"));
						if(backDateMap != null ){
							map.put("backDepartDateStart", backDateMap.get("backDepartDateStart"));
							map.put("backDepartDateEnd", backDateMap.get("backDepartDateEnd"));
						}
					}else if("3".equals(map.get("routeType").toString())){
						List<Map<String, Object>>  unionDepartDateList = flightDetailDao.getUnionDepartDate(map.get("journeyId"));
							map.put("unionDepartDate", unionDepartDateList);
					}
					newflightMapList.add(map);
				}
				resultMap.put("flightList", newflightMapList);
				resultMap.put("pageCount", page.getPageCount());
				resultMap.put("pageCurrent", page.getCurrentPage());
				response.setBody(resultMap);
			}
		} catch (Exception e) {
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
			logger.info(
					"=================getWholeList get someting wrong or sql wrong =======================");
			e.printStackTrace();
			return response;
		}
		return response;
	}

	/**
	 * 假删除航线
	 * @param flightId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> removeFlight(String groupId) {
		long requestTime = System.currentTimeMillis();
		ApinResponse<Map<String, Object>> response = null;
		Map<String, Object> resultMap =new HashMap<>();
		try {
			if (StringUtils.isBlank(groupId)) {
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("=================some param is blank in removeFlight =======================");
				return response;
			}
			response = ApinResponseUtil.good(requestTime);
			Map<String, Object> updateMap =new HashMap<>();
			ApinMerchantFlightInfo flightInfo = flightInfoDao.getOneByGroupId(groupId);
			if(flightInfo != null ){
				updateMap.put("version", flightInfo.getVersion());
			}
			updateMap.put("groupId", groupId);
			updateMap.put("isShelves", (byte)0);
			if(flightInfoDao.updateByMap(updateMap) >0){
				resultMap.put("status", true);
				resultMap.put("message", "操作成功");
			}else{
				resultMap.put("status", false);
				resultMap.put("message", "操作失败");
			}
			response.setBody(resultMap);
		} catch (Exception e) {
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
			logger.info("=====removeFlight ====");
			e.printStackTrace();
			return response;
		}
		return response;
	}

	/**
	 * 编辑航线信息
	 * @param groupId
	 * @param ticketNum
	 * @param price
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> motifyFlightInfo(String flightParam) {
		long requestTime = System.currentTimeMillis();
		ApinResponse<Map<String, Object>> response = null;
		Map<String, Object> resultMap =new HashMap<>();
		try {
			if (StringUtils.isBlank(flightParam) || !JsonUtil.validateIsKeyValue(flightParam)) {
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("=================some param is blank in motifyFlightInfo =======================");
				return response;
			}
			Map<String, String> flightParamMap = JsonUtil.parseToMap(flightParam);
			if (StringUtils.isBlank(flightParamMap.get("groupId")) || StringUtils.isBlank(flightParamMap.get("ticketNum"))
					|| StringUtils.isBlank(flightParamMap.get("price")) ) {
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("=================some param is blank in motifyFlightInfo =======================");
				return response;
			}
			response = ApinResponseUtil.good(requestTime);
			Map<String, Object> updateMap =new HashMap<>();
			ApinMerchantFlightInfo flightInfo = flightInfoDao.getOneByGroupId(flightParamMap.get("groupId"));
			if(flightInfo != null ){
				updateMap.put("version", flightInfo.getVersion());
			}
			updateMap.put("groupId",  flightParamMap.get("groupId"));
			updateMap.put("currentTicketNum", Integer.valueOf(flightParamMap.get("ticketNum")));
			updateMap.put("totalPriceInctax", new BigDecimal(flightParamMap.get("price")));
			if(flightInfoDao.updateByMap(updateMap) >0){
				resultMap.put("status", true);
				resultMap.put("message", "操作成功");
			}else{
				resultMap.put("status", false);
				resultMap.put("message", "操作失败");
			}
			response.setBody(resultMap);
		} catch (Exception e) {
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
			logger.info("=====motifyFlightInfo ====");
			e.printStackTrace();
			return response;
		}
		return response;
	}



	/**
	 * app 航班详情中获取头部数据
	 * @param groupId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> getHeadDetailInfo(String groupId) {
		long requestTime =System.currentTimeMillis();
		ApinResponse<Map<String, Object>> apinResponse =null;
		try {
			if(StringUtils.isBlank(groupId)){
				logger.info("========== getHeadDetailInfo some param is blank ========");
				apinResponse = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				return apinResponse;
			}
			apinResponse = ApinResponseUtil.good(requestTime);
			Map<String, Object> resultMap = flightDetailDao.getHeadDetailInfo(groupId);
			apinResponse.setBody(resultMap);
			return apinResponse;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("==========the method getDetailInfo get somthing wrong ========");
			apinResponse = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			return apinResponse;
		}
	}
	
	
	
	
	
	
	
	
}
