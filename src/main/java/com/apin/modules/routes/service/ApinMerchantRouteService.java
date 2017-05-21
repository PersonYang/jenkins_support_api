package com.apin.modules.routes.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apin.common.response.ApinResponse;
import com.apin.common.response.ApinResponseUtil;
import com.apin.common.response.ErrorEnum;
import com.apin.common.utils.JsonUtil;
import com.apin.common.utils.PageCommon;
import com.apin.common.utils.Redis;
import com.apin.common.utils.RedisType;
import com.apin.common.utils.ValidateParamUtils;
import com.apin.modules.flights.bean.ApinMerchantFlightDetailInfo;
import com.apin.modules.flights.bean.ApinMerchantFlightInfo;
import com.apin.modules.flights.dao.ApinMerchantFlightDetailInfoDao;
import com.apin.modules.flights.dao.ApinMerchantFlightInfoDao;
import com.apin.modules.orders.bean.ApinOrder;
import com.apin.modules.orders.dao.ApinOrderDao;
import com.apin.modules.orders.service.ApinCommonOrdersService;
import com.apin.modules.routes.bean.ApinMerchantRoutePriceInfo;
import com.apin.modules.routes.bean.ApinRoute;
import com.apin.modules.routes.bean.ApinRouteDetailInfo;
import com.apin.modules.routes.dao.ApinMerchantRoutePriceInfoDao;
import com.apin.modules.routes.dao.ApinRouteDao;
import com.apin.modules.routes.dao.ApinRouteDetailInfoDao;
import com.apin.modules.routes.dao.ApinRouteDistributeMerchantInfoDao;

/**
 * 
 * 用户行程相关service
 * @author Young
 * @date 2017年1月18日 上午9:41:36
 * @version 1.0 
 */
@Service
public class ApinMerchantRouteService {

	private static Logger logger =LoggerFactory.getLogger(ApinMerchantRouteService.class);
	
	@Autowired
	ApinMerchantRoutePriceService merchantPriceService;
	@Autowired
	ApinRouteDao routeDao;
	@Autowired
	ApinRouteDetailInfoDao routeDetailDao;
	@Autowired
	ApinMerchantRoutePriceInfoDao priceDao;
	@Autowired
	ApinOrderDao orderDao;
	@Autowired
	ApinMerchantFlightDetailInfoDao flightDetailInfoDao;
	@Autowired
	ApinRouteDistributeMerchantInfoDao distributeInfoDao;
	@Autowired
	ApinMerchantRoutePriceInfoDao routePriceInfoDao;
	@Autowired
	Redis redis;
	@Autowired
	ApinMerchantFlightInfoDao mainFlightInfoDao;
	@Autowired
	ApinCommonOrdersService commonOrderService;
	/**
	 * 行程出价
	 * @param routeNo 
	 * @param depositRatio 定金预付比例
	 * @param restLastTicketTime 最晚出票时间
	 * @param ticketRate 出票率
	 * @param submitParam 提交的价格参数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> putOfferPrice(String params) {
		long requestTime = System.currentTimeMillis();
		boolean result =false;
		Map<String, Object> resultMap = new HashMap<>();
		ApinResponse<Map<String, Object>> response =null;
		// 验证提交的参数是否为空
		if(!ValidateParamUtils.validateOfferPrice(params)){
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
			return response;
		}
		Map<String, String> paramsMap = JsonUtil.parseToMap(params);
		List<Map<String, String>> paramMapList = JsonUtil.parseToMapInList(paramsMap.get("submitParam"));
		// 航班出价
		try {
			ApinRoute apinRoute = routeDao.getByProperty(ApinRoute.class, "routeNo",paramsMap.get("routeNo"));
			if(apinRoute !=null &&  (apinRoute.getRouteStatus() == (byte)3 || apinRoute.getRouteStatus() == (byte)4)){
				resultMap.put("status",false);
				resultMap.put("message","出价失败，该行程已取消，或已下单");
				response = ApinResponseUtil.good(requestTime, resultMap);
				return response;
			}
			if(requestTime > apinRoute.getExpireTime().getTime()){
				resultMap.put("status",false);
				resultMap.put("message","该行程拼团日期已截止");
				response = ApinResponseUtil.good(requestTime, resultMap);
				return response;
			}
			result = merchantPriceService.offerPrice(paramsMap,paramMapList,resultMap);
			if(result){
				resultMap.put("status",true);
			}else{
				resultMap.put("status",false);
				resultMap.put("message","出价失败");
			}
			response = ApinResponseUtil.good(requestTime, resultMap);
		} catch (Exception e) {
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
			logger.info("there is a question in querying flightInfo ");
			return response;
		}
		return response;
	}
	
	/** 支持web调用
	 *  我的订单中 status
	 *  不传 查询全部订单
	 *  app调用 传入如下状态 
	 * 1，待支付。2，待出票。3，已出票，4，已失效 ,等订单
	 * @param pageParam
	 * @param searchParam
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> handledList(String pageParam, String searchParam) {
		long requestTime = System.currentTimeMillis();
		ApinResponse<Map<String, Object>> response =null;
		Map<String, Object> resultMap = new HashMap<>(); 
		try {
			Map<String, String> pageMap = JsonUtil.parseToMap(pageParam);
			Map<String, String> searchMap = JsonUtil.parseToMap(searchParam);
			if (!ValidateParamUtils.validatePageParam(pageMap) || StringUtils.isBlank(searchMap.get("userId"))) {
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("======allInfoList some param is blank =========");
				return response;
			}
			String orderStatus = searchMap.get("status");
			if(StringUtils.isBlank(searchMap.get("status"))){
				searchMap.put("isNotStatus", "yes");
			}
			// 取消或者订单失效
			if(searchMap.containsKey("status") && "4".equals(searchMap.get("status"))){
				searchMap.remove("status");
				searchMap.put("isCancle", "yes");
			}else {
				searchMap.put("orderStatus", orderStatus);
				searchMap.remove("status");
			}
			//List<String> userIdList = commonService.getSupportIdList(searchMap.get("userId"));
			response = ApinResponseUtil.good(requestTime);
			long recordCount = routeDao.getAllCountBySearchMap(searchMap);
			if (recordCount > 0) {
				PageCommon page = new PageCommon(Long.valueOf(pageMap.get("pageSize")), recordCount,
						Long.valueOf(pageMap.get("pageIndex")), pageMap.get("sortField"), pageMap.get("sortOrder"));
				List<ApinRoute> routeList = routeDao.getAllListBySearchMap(page, searchMap);
				packageAllRoute(resultMap, routeList);
				resultMap.put("pageCount", page.getPageCount());
				resultMap.put("pageCurrent", page.getCurrentPage());
			}
			response.setBody(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("=========allInfoList get something wrong ===========");
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			return response;
		}
		return response;
	}

	/**
	 * 将需要的信息封装
	 * @param resultMap
	 * @param routeList
	 */
	public void packageAllRoute(Map<String, Object> resultMap,List<ApinRoute> routeList){
		List<Map<String, Object>> routeMapList = new ArrayList<>(); 
		Map<String, String> searchMap = new HashMap<>();
		for (ApinRoute apinRoute : routeList) {
			searchMap.put("routeNo", String.valueOf(apinRoute.getRouteNo()));
			Map<String, Object> routeMap = new HashMap<>();
			List<ApinRouteDetailInfo> routeDetailList = routeDetailDao.getListByProperty(ApinRouteDetailInfo.class, 
					"routeNo", apinRoute.getRouteNo());
			List<Map<String, Object>> routeDetailMapList =new ArrayList<>();
			for (ApinRouteDetailInfo apinRouteDetailInfo : routeDetailList) {
				Map<String, Object> routeDetailMap = new HashMap<>();
				routeDetailMap.put("departPlace", apinRouteDetailInfo.getDepartPlace());
				routeDetailMap.put("departDate", apinRouteDetailInfo.getDepartDate().getTime());
				routeDetailMap.put("destPlace", apinRouteDetailInfo.getDestPlace());
				routeDetailMapList.add(routeDetailMap);
			}
			routeMap.put("id", apinRoute.getId());
			routeMap.put("routeType", apinRoute.getRouteType());
			routeMap.put("passengerNum", apinRoute.getPassengerNum());
			routeMap.put("createTime", apinRoute.getCreateTime().getTime());
			routeMap.put("routeNo", apinRoute.getRouteNo());
			routeMap.put("status", apinRoute.getRouteStatus());
			routeMap.put("orderNo", null);
			routeMap.put("journeyId", null);
			routeMap.put("cancleType", "0");
			routeMap.put("currentTime", System.currentTimeMillis());
			routeMap.put("routeDetail", routeDetailMapList);
			routeMap.put("description", apinRoute.getDescription());
			packageOrder(apinRoute, routeMap, searchMap);
			routeMapList.add(routeMap);
		}
		resultMap.put("routeList", routeMapList);
	}
	
	public  void packageOrder(ApinRoute route,Map<String, Object> routeMap,Map<String, String> searchMap){
		if(route.getRouteStatus() == (byte)2){
			routeMap.put("status", route.getRouteStatus());
		}else if(route.getRouteStatus() == (byte)3 || route.getRouteStatus() == (byte)4){
				// 根据行程号 查询订单情况
				ApinOrder order = orderDao.getByProperty(ApinOrder.class, "routeNo", route.getRouteNo());
				if(order != null ){
					routeMap.put("passengerNum", order.getActualPassengerNum());
					routeMap.put("totalPrice", order.getActualPayCash());
					ApinMerchantRoutePriceInfo routePriceInfo = priceDao.getByID(ApinMerchantRoutePriceInfo.class, order.getPriceId());
					if(routePriceInfo != null ){
						routeMap.put("restValidateTime", routePriceInfo.getRestLastPayTime().getTime());
						routeMap.put("advancePrice", getTwoPoint(order.getTotalPrice().multiply(routePriceInfo.getDepositRatio())));
						routeMap.put("restPrice", getTwoPoint(order.getRestPrice()));
						routeMap.put("price", routePriceInfo.getPrice());
					}
					routeMap.put("advanceRemainTime", commonOrderService.getRemainPayTime(order.getOrderNo(), order.getTravelAgencyId()));
					searchMap.put("journeyId", String.valueOf(order.getJourneyId()));
					List<ApinMerchantFlightDetailInfo> flightDetailList = flightDetailInfoDao.getFlightBySearchMap(searchMap);
					if(flightDetailList != null && flightDetailList.size()>0){
						ApinMerchantFlightDetailInfo flightDetailInfo = flightDetailList.get(0);
						routeMap.put("airComp", flightDetailInfo.getAirComp());
						routeMap.put("flightNo", flightDetailInfo.getFlightNo());
						routeMap.put("departPlace", flightDetailInfo.getDepartPlace());
						routeMap.put("destPlace", flightDetailInfo.getDestPlace());
						routeMap.put("departDate", flightDetailInfo.getDepartDate().getTime());
						routeMap.put("arriveDate", flightDetailInfo.getArriveDate().getTime());
						routeMap.put("departTime", flightDetailInfo.getDepartTime());
						routeMap.put("arriveTime", flightDetailInfo.getArriveTime());
						routeMap.put("departAirPort", flightDetailInfo.getDepartAirport());
						routeMap.put("arriveAirPort", flightDetailInfo.getArriveAirport());
						routeMap.put("hasTurn", flightDetailInfo.getHasTurn());
					}
					routeMap.put("journeyId", order.getJourneyId());
					routeMap.put("orderNo", order.getOrderNo());
					getCommonStatus(order.getStatus(),routeMap,order.getActualPassengerNum());
				}
		}else{
			routeMap.put("status", route.getRouteStatus());
		}
	}
	
	public BigDecimal getTwoPoint(BigDecimal price){
		if(price != null ){
			DecimalFormat myformat=new DecimalFormat("0.00");
			 String str = myformat.format(price);
			 return new BigDecimal(str);
		}return null;
	}
	/**
	 * 根据订单状态返回状态 
	 * @param status
	 * @return   0：没有商户出价。 1：正常。 2：已出价 。4：订单失效。5：订金待支付。6：定金支付待审核。7：尾款待支付。8：尾款支付待审核。9：已支付未添加乘机人。10：出票中，11：已出票
	 */
	private void getCommonStatus(Byte status,Map<String, Object> resultMap,Integer actualPassengerNum) {
		// 订单失效
		if(status == (byte)0){
			resultMap.put("status", (byte)4);
			resultMap.put("cancleType", "1");
			// 订单待支付 
		}else if(status == (byte)1){
			resultMap.put("status", (byte)5);
			// 已支付未添加乘机人
		}else if(status == (byte)2){
			resultMap.put("passengerNum", actualPassengerNum);
			resultMap.put("status", (byte)9);
			// 出票中
		}else if(status == (byte)3){
			resultMap.put("passengerNum", actualPassengerNum);
			resultMap.put("status", (byte)10);
			// 已出票
		}else if(status == (byte)4){
			resultMap.put("passengerNum", actualPassengerNum);
			resultMap.put("status", (byte)11);
		  // 定金支付等待审核
		}else if(status == (byte)5){
			resultMap.put("status", (byte)6);
			// 定金已经支付
		}else if(status == (byte)6){
			resultMap.put("status", (byte)7);
		   // 尾款支付待审核
		}else if(status == (byte)7){
			resultMap.put("passengerNum", actualPassengerNum);
			resultMap.put("status", (byte)8);
		}
	}

	/**
	 * 获取当前供应商 用户的 等待出价的行程或已出价的行程信息
	 * @param pageParam
	 * @param searchParam
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> getWaitHandle(String pageParam, String searchParam) {
		long requestTime = System.currentTimeMillis();
		ApinResponse<Map<String, Object>> response =null;
		Map<String, Object> resultMap = new HashMap<>(); 
		try {
			Map<String, String> pageMap = JsonUtil.parseToMap(pageParam);
			Map<String, String> searchMap = JsonUtil.parseToMap(searchParam);
			if (!ValidateParamUtils.validatePageParam(pageMap) || StringUtils.isBlank(searchMap.get("userId"))) {
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("======getWaitHandle some param is blank =========");
				return response;
			}
			response = ApinResponseUtil.good(requestTime);
			List<Integer> routeNoList = distributeInfoDao.getRouteNoListByMap(searchMap);
			if(routeNoList != null && routeNoList.size()>0){
				searchMap.put("support_select", "yes");
				long recordCount = routeDao.getCountBySearchMap(searchMap,routeNoList);
				if(recordCount > 0){
					PageCommon page = new PageCommon(Long.valueOf(pageMap.get("pageSize")), recordCount,
							Long.valueOf(pageMap.get("pageIndex")), pageMap.get("sortField"), pageMap.get("sortOrder"));
					List<ApinRoute> routeList = routeDao.getListBySearchMap(page,searchMap,routeNoList);
					packageWaitHandleRoute(resultMap, routeList,searchMap.get("userId"));
					resultMap.put("pageCount", page.getPageCount());
					resultMap.put("pageCurrent", page.getCurrentPage());
				}
			}
			response.setBody(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("=========getWaitHandle get something wrong ===========");
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			return response;
		}
		return response;
	}

	/**
	 * 封装等待出价 或者已经出价的行程的信息
	 * @param resultMap
	 * @param routeList
	 */
	private void packageWaitHandleRoute(Map<String, Object> resultMap, List<ApinRoute> routeList,String userId) {
		List<Map<String, Object>> routeMapList = new ArrayList<>(); 
		Map<String, String> searchMap = new HashMap<>();
		for (ApinRoute apinRoute : routeList) {
			searchMap.put("routeNo", String.valueOf(apinRoute.getRouteNo()));
			searchMap.put("userId", userId);
			Map<String, Object> routeMap = new HashMap<>();
			List<ApinRouteDetailInfo> routeDetailList = routeDetailDao.getListByProperty(ApinRouteDetailInfo.class, 
					"routeNo", apinRoute.getRouteNo());
			List<Map<String, Object>> routeDetailMapList =new ArrayList<>();
			if(routeDetailList != null && routeDetailList.size()>0){
				ApinRouteDetailInfo firstrouteInfo = routeDetailList.get(0);
				routeMap.put("departPlace", firstrouteInfo.getDepartPlace());
				routeMap.put("departDate", firstrouteInfo.getDepartDate().getTime());
				routeMap.put("destPlace", firstrouteInfo.getDestPlace());
			}
			for (ApinRouteDetailInfo apinRouteDetailInfo : routeDetailList) {
				Map<String, Object> routeDetailMap = new HashMap<>();
				routeDetailMap.put("departPlace", apinRouteDetailInfo.getDepartPlace());
				routeDetailMap.put("departDate", apinRouteDetailInfo.getDepartDate().getTime());
				routeDetailMap.put("destPlace", apinRouteDetailInfo.getDestPlace());
				routeDetailMapList.add(routeDetailMap);
			}
			if(apinRoute.getRouteStatus() == (byte)2){
				ApinMerchantRoutePriceInfo routePriceInfo = routePriceInfoDao.getOneRoutePrice(searchMap);
				if(routePriceInfo != null ){
					routeMap.put("priceId", routePriceInfo.getId());
					List<ApinMerchantFlightDetailInfo> flightDetailInfoList = 
							flightDetailInfoDao.getListByProperty(ApinMerchantFlightDetailInfo.class, "journeyId", routePriceInfo.getJourneyId());
					if(flightDetailInfoList != null && flightDetailInfoList.size()>0 ){
						ApinMerchantFlightDetailInfo flightDetailInfo = flightDetailInfoList.get(0);
						routeMap.put("airComp", flightDetailInfo.getAirComp());
						routeMap.put("arriveAirport", flightDetailInfo.getArriveAirport());
						routeMap.put("arriveDate", flightDetailInfo.getArriveDate().getTime());
						routeMap.put("arriveTime", flightDetailInfo.getArriveTime());
						routeMap.put("departAirport", flightDetailInfo.getDepartAirport());
						routeMap.put("departDate", flightDetailInfo.getDepartDate().getTime());
						routeMap.put("departTime", flightDetailInfo.getDepartTime());
						routeMap.put("flightNo", flightDetailInfo.getFlightNo());
						routeMap.put("hasTurn", flightDetailInfo.getHasTurn());
						routeMap.put("price",getTwoPoint(routePriceInfo.getPrice()));
						routeMap.put("journeyId",flightDetailInfo.getJourneyId());
					}
				}
			}
			routeMap.put("agencyId", apinRoute.getTravelAgencyId());
			routeMap.put("id", apinRoute.getId());
			routeMap.put("status", apinRoute.getRouteStatus());
			routeMap.put("description", apinRoute.getDescription());
			routeMap.put("routeType", apinRoute.getRouteType());
			routeMap.put("passengerNum", apinRoute.getPassengerNum());
			routeMap.put("createTime", apinRoute.getCreateTime().getTime());
			routeMap.put("routeNo", apinRoute.getRouteNo());
			routeMap.put("showNo", apinRoute.getShowNo());
			routeMap.put("detailList", routeDetailMapList);
			routeMapList.add(routeMap);
		}
		resultMap.put("routeList", routeMapList);
		
	}

	/**
	 * 出价时获取符合该行程订单
	 * 的航班信息情况
	 * @param routeNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<List<Map<String, Object>>> getEnquiryFlightInfo(String routeNo,String userId) {
		long requestTime = System.currentTimeMillis();
		ApinResponse<List<Map<String, Object>>> response =null;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		try {
			if (StringUtils.isBlank(routeNo) || StringUtils.isBlank(userId)) {
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("======getEnquiryFlightInfo some param is blank =========");
				return response;
			}
			ApinRoute route = routeDao.getByProperty(ApinRoute.class, "routeNo", routeNo);
			if(route == null ){
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("======getEnquiryFlightInfo some param is blank =========");
				return response;
			}
			response = ApinResponseUtil.good(requestTime);
			List<String> jourIdList =  redis.lrange(RedisType.matchedSupplierIdList, String.valueOf(route.getRouteNo()));
			if(jourIdList != null && jourIdList.size()>0){
				Map<String, String> searchMap = new HashMap<>();
				searchMap.put("parentId", "0");
				// 取出所有符合行程订单 parentId为0的 行程
				List<ApinMerchantFlightDetailInfo> flightDetailList = flightDetailInfoDao.newGetOfferedFlight(jourIdList, searchMap);
				for (ApinMerchantFlightDetailInfo flightDetai : flightDetailList) {
					Map<String, Object> resultMap = new HashMap<>();
					ApinMerchantFlightInfo mainFlightInfo = 
							mainFlightInfoDao.getByProperty(ApinMerchantFlightInfo.class, "journeyId", flightDetai.getJourneyId());
					if(mainFlightInfo != null ){
						if(!userId.equals(mainFlightInfo.getSupplierId())){
							continue;
						}
						resultMap.put("routeType", mainFlightInfo.getRouteType());
						resultMap.put("totalPriceInctax", mainFlightInfo.getTotalPriceInctax());
					}
					resultMap.put("personNum", route.getPassengerNum());
					resultMap.put("airComp", flightDetai.getAirComp());
					resultMap.put("arriveAirport", flightDetai.getArriveAirport());
					resultMap.put("arriveDate", flightDetai.getArriveDate().getTime());
					resultMap.put("arriveTime", flightDetai.getArriveTime());
					resultMap.put("departAirport", flightDetai.getDepartAirport());
					resultMap.put("departDate", flightDetai.getDepartDate());
					resultMap.put("departPlace", flightDetai.getDepartPlace());
					resultMap.put("departTime", flightDetai.getDepartTime());
					resultMap.put("destPlace", flightDetai.getDestPlace());
					resultMap.put("flightNo", flightDetai.getFlightNo());
					resultMap.put("hasTurn", flightDetai.getHasTurn());
					resultMap.put("id", flightDetai.getId());
					resultMap.put("journeyId", flightDetai.getJourneyId());
					resultList.add(resultMap);
				}
				response.setBody(resultList);
			}else{
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("============redis get something wrong ===============");
				return response;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.info("=========getEnquiryFlightInfo get something wrong ===========");
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			return response;
		}
		return response;
	}
	
}
