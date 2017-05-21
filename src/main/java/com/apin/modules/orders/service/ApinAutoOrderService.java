package com.apin.modules.orders.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apin.common.response.ApinResponse;
import com.apin.common.response.ApinResponseUtil;
import com.apin.common.response.ErrorEnum;
import com.apin.common.utils.DateUtil;
import com.apin.common.utils.JsonUtil;
import com.apin.common.utils.ValidateParamUtils;
import com.apin.modules.orders.bean.ApinOrder;
import com.apin.modules.orders.dao.ApinOrderDao;
import com.apin.modules.passengers.bean.ApinPassengerTicket;
import com.apin.modules.passengers.dao.ApinPassengerTicketDao;
import com.apin.modules.routes.bean.ApinMerchantRoutePriceInfo;
import com.apin.modules.routes.bean.ApinRoute;
import com.apin.modules.routes.bean.ApinRouteDistributeMerchantInfo;
import com.apin.modules.routes.dao.ApinMerchantRoutePriceInfoDao;
import com.apin.modules.routes.dao.ApinRouteDao;
import com.apin.modules.routes.dao.ApinRouteDetailInfoDao;
import com.apin.modules.routes.dao.ApinRouteDistributeMerchantInfoDao;

/**
 * 
 * auto订单相关
 * @author Young
 * @date 2017年2月28日 下午5:01:23
 * @version 1.0 
 */
@Service
public class ApinAutoOrderService {
	@Autowired
	ApinOrderDao orderDao;
	@Autowired
	ApinPassengerTicketDao ticketDao;
	@Autowired
	ApinMerchantRoutePriceInfoDao merchantPriceDao;
	@Autowired
	ApinRouteDistributeMerchantInfoDao distributeMerchantInfoDao;
	@Autowired
	ApinRouteDao routeDao;
	@Value("${offered.price.validate.time}")
	private String priceValidateTime;
	@Autowired
	ApinRouteDetailInfoDao routeDetailDao;
	
	
	private final Logger logger = LoggerFactory.getLogger(ApinOrderService.class);
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> pushTicket(String params) {
		long requestTime = System.currentTimeMillis();
		ApinResponse<Map<String, Object>> response = null;
		Map<String, Object> resultMap = new HashMap<>();
		try {
			if(!ValidateParamUtils.validatePushParam(params)){
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("=================some param is blank in pushTicket=======================");
				return response;
			}
			Map<String, String> paramsMap = JsonUtil.parseToMap(params);
			ApinOrder apinOrder = orderDao.getByProperty(ApinOrder.class, "orderNo",paramsMap.get("orderNo"));
			if(apinOrder == null){
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("==============the orderNo is not null,but the order is null ==============");
				return response;
			}
			boolean result = pushTicketAffair(apinOrder,paramsMap.get("ticketParam")) ;
			response = ApinResponseUtil.good(requestTime);
			if(result){
				resultMap.put("status", true);
				resultMap.put("message", "出票成功");
			}else{
				resultMap.put("status", false);
				resultMap.put("message", "出票失败");
			}
			response.setBody(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			logger.info("=======================");
			return response;
		}
		return response;
	}
	
	
	/**
	 * 出票 改变订单状态
	 * 添加乘机人票号 保障事务一致性
	 * @param apinOrder
	 * @param ticketParam
	 * @return
	 */
	@Transactional
	private boolean pushTicketAffair(ApinOrder apinOrder, String ticketParam) throws Exception{
		apinOrder.setStatus((byte) 4);
		ApinOrder updateOrder = new ApinOrder();
		updateOrder.setId(apinOrder.getId());
		updateOrder.setStatus((byte) 4);
		if (orderDao.updateNotNull(updateOrder) > 0) {
			List<Map<String, String>> paramMapList = JsonUtil.parseToMapInList(ticketParam);
			for (Map<String, String> map : paramMapList) {
				ApinPassengerTicket ticket = new ApinPassengerTicket();
				ticket.setPassengerId(Integer.valueOf(map.get("passengerId")));
				ticket.setTicketNo(map.get("ticketNo"));
				ticketDao.insert(ticket);
			}
		} else {
			return false;
		}
		return true;
	}


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
			result = offerPrice(paramsMap,paramMapList,resultMap);
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
	
	
	
	/**
	 * 供应商出价
	 * @param userId
	 * @param routeNo
	 * @param depositRatio
	 * @param restLastTicketTime
	 * @param ticketRate
	 * @param paramMapList
	 * @param resultMap
	 * @return
	 */
	@Transactional
	public boolean offerPrice(Map<String, String> paramsMap,List<Map<String, String>> paramMapList, Map<String, Object> resultMap) throws Exception{
		Map<String, String> searchMap =new HashMap<>();
		searchMap.put("userId",  paramsMap.get("userId"));
		searchMap.put("routeNo", paramsMap.get("routeNo"));
		ApinRouteDistributeMerchantInfo distributeInfo = distributeMerchantInfoDao.findOneBySearchMap(searchMap);
		if(distributeInfo == null ){
			return false;
		}
		if(paramMapList != null && paramMapList.size()>0){
			ApinRoute apinRoute = routeDao.getByProperty(ApinRoute.class, "routeNo",  paramsMap.get("routeNo"));
			Map<String, String> containMap =new HashMap<>();
			for (int i = 0; i < paramMapList.size(); i++) {
				Map<String, String> paramMap = paramMapList.get(i);
				ApinMerchantRoutePriceInfo routePrice = new ApinMerchantRoutePriceInfo();
				routePrice.setRouteNo(Long.valueOf( paramsMap.get("routeNo")));
				routePrice.setJourneyId(Long.valueOf(paramMap.get("journeyId")));
				routePrice.setPrice(new BigDecimal(paramMap.get("price")));
				routePrice.setTicketRate(new BigDecimal( paramsMap.get("ticketRate")));
				routePrice.setDepositRatio(new BigDecimal( paramsMap.get("depositRatio")));
				routePrice.setRestLastPayTime(DateUtil.toFormatDate(paramsMap.get("restLastTicketTime")+" 23:59", "yyyy-MM-dd HH:mm"));
				Date validateDate = new Date(new Date().getTime()+Long.valueOf(priceValidateTime)*60*1000);
				routePrice.setValidTime(validateDate);
				routePrice.setSupplierId(paramsMap.get("userId"));
			    int isInsert = merchantPriceDao.insert(routePrice);
			    if(isInsert ==0){
			    	logger.info("when insert offerprice get something wrong");
			    	throw new Exception("=============when insert offerprice get something wrong==================");
			    }
			}
			if(containMap.size() == paramMapList.size()){
				return false;
			}else if(containMap.size()==0){
				resultMap.put("message", "出价成功");
			}
			// 将行程行程的状态改为已经出价
			distributeInfo.setHandleStatus((byte)1);
			// 改变状态为 跟进中========= 
			distributeMerchantInfoDao.update(distributeInfo);
			ApinRoute  updateRoute = new ApinRoute();
			updateRoute.setId(apinRoute.getId());
			updateRoute.setRouteStatus((byte)2);
			if(routeDao.updateNotNull(updateRoute) > 0){
				return true;
			}
			return false;
		}
		return false;
	}
	
	

}
