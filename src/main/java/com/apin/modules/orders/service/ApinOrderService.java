package com.apin.modules.orders.service;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apin.common.response.ApinResponse;
import com.apin.common.response.ApinResponseUtil;
import com.apin.common.response.ErrorEnum;
import com.apin.common.thread.ApinSendMessageThread;
import com.apin.common.utils.DateUtil;
import com.apin.common.utils.JsonUtil;
import com.apin.common.utils.PageCommon;
import com.apin.common.utils.ValidateParamUtils;
import com.apin.modules.flights.bean.ApinMerchantFlightDetailInfo;
import com.apin.modules.flights.bean.ApinMerchantFlightInfo;
import com.apin.modules.flights.dao.ApinMerchantFlightDetailInfoDao;
import com.apin.modules.flights.dao.ApinMerchantFlightInfoDao;
import com.apin.modules.orders.bean.ApinOrder;
import com.apin.modules.orders.dao.ApinOrderDao;
import com.apin.modules.passengers.bean.ApinPassengerInfo;
import com.apin.modules.passengers.bean.ApinPassengerTicket;
import com.apin.modules.passengers.dao.ApinPassengerInfoDao;
import com.apin.modules.passengers.dao.ApinPassengerTicketDao;
import com.apin.modules.routes.bean.ApinMerchantRoutePriceInfo;
import com.apin.modules.routes.bean.ApinRoute;
import com.apin.modules.routes.bean.ApinRouteDetailInfo;
import com.apin.modules.routes.dao.ApinMerchantRoutePriceInfoDao;
import com.apin.modules.routes.dao.ApinRouteDao;
import com.apin.modules.routes.dao.ApinRouteDetailInfoDao;
import com.apin.modules.users.bean.ApinAccount;
import com.apin.modules.users.dao.ApinAccountDao;

/**
 * 供应商订单
 * @author Young
 * @date 2017年1月19日 上午11:05:04
 * @version 1.0 
 */
@Service
public class ApinOrderService {

	
	@Autowired
	ApinOrderDao orderDao;
	@Autowired
	ApinMerchantFlightDetailInfoDao flightDetailDao;
	@Autowired
	ApinRouteDao routeDao;
	@Autowired
	ApinMerchantRoutePriceInfoDao priceInfoDao;
	@Autowired
	ApinCommonOrdersService commonOrderService;
	@Autowired
	ApinMerchantFlightInfoDao flightInfoDao;
	@Autowired
	ApinPassengerInfoDao passengerInfoDao;
	@Autowired
	ApinPassengerTicketDao ticketDao;
	@Value("${apin.customServicePhone}")
	private String customServicePhone;
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	ApinSendMessageThread sendMessageThread;
	@Autowired
	ApinAccountDao accountDao;
	@Autowired
	ApinRouteDetailInfoDao routeDetailDao;
	@Autowired
	ApinMerchantRoutePriceInfoDao priceDao;
	
	private final Logger logger = LoggerFactory.getLogger(ApinOrderService.class);

	/**
	 * for web
	 * 获取 等待支付 等待出票 订单信息
	 * @param pageParam
	 * @param searchParam
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> getOrderInfo(String pageParam, String searchParam) {
		long requestTime = System.currentTimeMillis();
		ApinResponse<Map<String, Object>> response = null;
		try {
			Map<String, String> searchMap = JsonUtil.parseToMap(searchParam);
			Map<String, String> pageMap = JsonUtil.parseToMap(pageParam);
			Map<String, Object> resultMap = new HashMap<>();
			if (!ValidateParamUtils.validatePageParam(pageMap) || StringUtils.isBlank(searchMap.get("userId")) 
					|| StringUtils.isBlank(searchMap.get("status"))) {
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("=================some param is blank in querying order info=======================");
				return response;
			}
			response = ApinResponseUtil.good(requestTime);
			long recordCount = orderDao.getCountBySearchMap(searchMap);
			if (recordCount > 0) {
				PageCommon page = new PageCommon(Long.valueOf(pageMap.get("pageSize")), recordCount,
						Long.valueOf(pageMap.get("pageIndex")), pageMap.get("sortField"), pageMap.get("sortOrder"));
				List<ApinOrder> orderList = orderDao.getListBySearchMap(page, searchMap);
				List<Map<String, Object>> orderMapList = packagePartOrder(resultMap,orderList);
				resultMap.put("routeList", orderMapList);
				resultMap.put("pageCount", page.getPageCount());
				resultMap.put("pageCurrent", page.getCurrentPage());
				response.setBody(resultMap);
			}
		} catch (Exception e) {
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
			logger.info(
					"=================may be jsonString to map get someting wrong or sql wrong =======================");
			e.printStackTrace();
			return response;
		}
		return response;
	}

	/**
	 * 封装部分订单信息
	 * 待支付
	 * 待出票
	 * @param resultMap
	 * @param orderList
	 */
	private List<Map<String, Object>> packagePartOrder(Map<String, Object> resultMap, List<ApinOrder> orderList) {
		List<Map<String, Object>> orderMapList =new ArrayList<>();
		Map<String, String> searchMap =new HashMap<>();
		for (ApinOrder apinOrder : orderList) {
			Map<String, Object> orderMap = new HashMap<>();
			searchMap.put("journeyId", String.valueOf(apinOrder.getJourneyId()));
			ApinMerchantFlightDetailInfo  flightDetailInfo= flightDetailDao.getOneFlightBySearchMap(searchMap);
			ApinRoute route = routeDao.getByProperty(ApinRoute.class, "routeNo", apinOrder.getRouteNo());
			if(flightDetailInfo != null ){
				orderMap.put("currentTime", System.currentTimeMillis());
				orderMap.put("airComp", flightDetailInfo.getAirComp());
				orderMap.put("arriveAirport", flightDetailInfo.getArriveAirport());
				orderMap.put("arriveDate", flightDetailInfo.getArriveDate());
				orderMap.put("arriveTime", flightDetailInfo.getArriveTime());
				orderMap.put("departAirport", flightDetailInfo.getDepartAirport());
				orderMap.put("departDate", flightDetailInfo.getDepartDate().getTime());
				orderMap.put("departPlace", flightDetailInfo.getDepartPlace());
				orderMap.put("departTime", flightDetailInfo.getDepartTime());
				orderMap.put("departPlace", flightDetailInfo.getDepartPlace());
				orderMap.put("destPlace", flightDetailInfo.getDestPlace());
				orderMap.put("flightNo", flightDetailInfo.getFlightNo());
				orderMap.put("hasTurn", flightDetailInfo.getHasTurn());
				orderMap.put("id", apinOrder.getId());
				orderMap.put("journeyId", flightDetailInfo.getJourneyId());
				orderMap.put("orderNo", apinOrder.getOrderNo());
				orderMap.put("routeNo", apinOrder.getRouteNo());
				orderMap.put("status", apinOrder.getStatus());
				orderMap.put("passengerNum", apinOrder.getActualPassengerNum());
				orderMap.put("price", apinOrder.getActualPayCash());
				if(route != null ){
					orderMap.put("createTime", route.getCreateTime().getTime());
					orderMap.put("showNo", route.getShowNo());
					orderMap.put("routeType", route.getRouteType());
				}
				ApinMerchantRoutePriceInfo routePriceInfo = priceInfoDao.getByID(ApinMerchantRoutePriceInfo.class, apinOrder.getPriceId());
				if(routePriceInfo != null ){
					orderMap.put("restValidateTime", routePriceInfo.getRestLastPayTime().getTime());
					orderMap.put("advancePrice", getTwoPoint(apinOrder.getTotalPrice().multiply(routePriceInfo.getDepositRatio())));
					orderMap.put("restPrice", getTwoPoint(apinOrder.getRestPrice()));
					orderMap.put("advanceRemainTime", commonOrderService.getRemainPayTime(apinOrder.getOrderNo(), apinOrder.getTravelAgencyId()));
				}
			}
			orderMapList.add(orderMap);
		}
		return orderMapList;
	}

	/**
	 * 保留两位小数
	 * @param price
	 * @return
	 */
	public BigDecimal getTwoPoint(BigDecimal price){
		if(price != null ){
			DecimalFormat myformat=new DecimalFormat("0.00");
			 String str = myformat.format(price);
			 return new BigDecimal(str);
		}return null;
	}

	/**
	 * 出票时获取航班 
	 * 和乘机人信息
	 * @param orderNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> getOrderInfoWhenPush(String orderNo) {
		long requestTime = System.currentTimeMillis();
		ApinResponse<Map<String, Object>> response = null;
		try {
			Map<String, Object> resultMap = new HashMap<>();
			if (StringUtils.isBlank(orderNo)) {
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("=================some param is blank in getOrderInfoWhenPush=======================");
				return response;
			}
			response = ApinResponseUtil.good(requestTime);
			ApinOrder order = orderDao.getByProperty(ApinOrder.class, "orderNo", orderNo);
			if(order ==null){
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("=================the orderNo is not null, but the order is null=======================");
				return response;
			}
			Map<String, String> searchMap = new HashMap<>();
			searchMap.put("journeyId", String.valueOf(order.getJourneyId()));
			ApinMerchantFlightDetailInfo flightDetailInfo = flightDetailDao.getOneFlightBySearchMap(searchMap);
			if(flightDetailInfo == null ){
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("=================the orderNo  and the order is not null,but the flightDetail is null =======================");
				return response;
			}
			ApinMerchantFlightInfo mainFlightInfo = flightInfoDao.getByProperty(ApinMerchantFlightInfo.class,"journeyId", order.getJourneyId());
			resultMap.put("departDate", flightDetailInfo.getDepartDate().getTime());
			resultMap.put("departTime", flightDetailInfo.getDepartTime());
			resultMap.put("departAirport", flightDetailInfo.getDepartAirport());
			resultMap.put("departPlace", flightDetailInfo.getDepartPlace());
			resultMap.put("destPlace", flightDetailInfo.getDestPlace());
			resultMap.put("arriveDate", flightDetailInfo.getArriveDate().getTime());
			resultMap.put("arriveTime", flightDetailInfo.getArriveTime());
			resultMap.put("arriveAirport", flightDetailInfo.getArriveAirport());
			resultMap.put("airComp", flightDetailInfo.getAirComp());
			resultMap.put("flightNo", flightDetailInfo.getFlightNo());
			resultMap.put("passengerNum", order.getActualPassengerNum());
			resultMap.put("routeType", mainFlightInfo.getRouteType());
			resultMap.put("hasTurn", flightDetailInfo.getHasTurn());
			resultMap.put("orderNo", orderNo);
			List<Map<String, String>> passMapList = passengerInfoDao.getListMapByOrderNo(order.getOrderNo());
			resultMap.put("passengerList", passMapList);
			response.setBody(resultMap);
		} catch (Exception e) {
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
			logger.info("========= the method getOrderInfoWhenPush get something wrong ========");
			e.printStackTrace();
			return response;
		}
		return response;
	}

	/**
	 * 供应商出票情况    同时满足后台系统订单出票
	 * @param orderNo 订单号
	 * @param ticketParam 票号情况
	 * @return
	 */
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
			ApinAccount account = accountDao.findById(apinOrder.getTravelAgencyId());
			List<ApinPassengerInfo> passengerList = passengerInfoDao.getListByProperty(ApinPassengerInfo.class,
					"orderNo", apinOrder.getOrderNo());
			final List<String> phoneList = new ArrayList<String>();
			final List<String> paramList = new ArrayList<>();
			final Map<String, String> paramMap = new HashMap<String, String>();
			StringBuffer passenger = new StringBuffer();
			for (int i = 0; i < passengerList.size(); i++) {
				passenger.append(passengerList.get(i).getPassengerName());
				if ((i + 1) != passengerList.size()) {
					passenger.append("、");
				}
			}
			passenger.append("，");
			paramList.add(passenger.toString());
			paramList.add(returnFlightInfo(apinOrder.getJourneyId()));
			paramList.add(customServicePhone);
			paramMap.put("userId", apinOrder.getTravelAgencyId());
			paramMap.put("messageType", "2");
			if(apinOrder.getRouteNo() != null ){
				paramMap.put("messageDetail",String.valueOf(apinOrder.getRouteNo()) );
			}
			// 某些不可描述
			if (account != null && apinOrder.getFlag() == (byte)0) {
				phoneList.add(account.getPhone());
				sendMessageThread.setModelName("ticketIssueNotice");
				sendMessageThread.setTitle("出票通知");
				sendMessageThread.setPhone(phoneList);
				sendMessageThread.setParamList(paramList);
				sendMessageThread.setParamMap(paramMap);
				taskExecutor.execute(sendMessageThread);
			}
		} else {
			return false;
		}
		return true;
	}
	
	public String returnFlightInfo(Long jounryId){
		Map<String, String> searchMap = new HashMap<>();
		searchMap.put("journeyId", String.valueOf(jounryId));
		StringBuffer sb = new StringBuffer("");
		List<ApinMerchantFlightDetailInfo> detailFlightInfoList = flightDetailDao.getFlightBySearchMap(searchMap);
		for (int i = 0; i <detailFlightInfoList.size(); i++) {
			ApinMerchantFlightDetailInfo detailInfo =  detailFlightInfoList.get(i);
			sb.append(detailInfo.getFlightNo()+" ");
			sb.append(DateUtil.formatDate(detailInfo.getDepartDate(), "MM-dd"));
			sb.append(detailInfo.getDepartAirport()+detailInfo.getDepartTime()+"起飞-").append(detailInfo.getArriveAirport()).append(""+detailInfo.getArriveTime()+"到达，");
		}
		return sb.toString();
	}

	/**
	 * 全部订单情况
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
			response = ApinResponseUtil.good(requestTime);
			long recordCount = orderDao.getCountBySearchMap(searchMap);
			if (recordCount > 0) {
				PageCommon page = new PageCommon(Long.valueOf(pageMap.get("pageSize")), recordCount,
						Long.valueOf(pageMap.get("pageIndex")), pageMap.get("sortField"), pageMap.get("sortOrder"));
				List<ApinOrder> orderList = orderDao.getListBySearchMap(page, searchMap);
				packageOrderhandledList(resultMap,orderList);
				resultMap.put("pageCount", page.getPageCount());
				resultMap.put("pageCurrent", page.getCurrentPage());
			}
			response.setBody(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("========= allInfoList get something wrong ===========");
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			return response;
		}
		return response;
	}

	/**
	 * 封装订单信息
	 * @param resultMap
	 * @param orderList
	 */
	private void packageOrderhandledList(Map<String, Object> resultMap, List<ApinOrder> orderList) {
		List<Map<String, Object>> routeMapList = new ArrayList<>(); 
		Map<String, String> searchMap = new HashMap<>();
		for (ApinOrder apinOrder : orderList) {
			searchMap.put("routeNo", String.valueOf(apinOrder.getRouteNo()));
			Map<String, Object> routeMap = new HashMap<>();
			List<ApinRouteDetailInfo> routeDetailList = routeDetailDao.getListByProperty(ApinRouteDetailInfo.class, 
					"routeNo", apinOrder.getRouteNo());
			List<Map<String, Object>> routeDetailMapList =new ArrayList<>();
			for (ApinRouteDetailInfo apinRouteDetailInfo : routeDetailList) {
				Map<String, Object> routeDetailMap = new HashMap<>();
				routeDetailMap.put("departPlace", apinRouteDetailInfo.getDepartPlace());
				routeDetailMap.put("departDate", apinRouteDetailInfo.getDepartDate().getTime());
				routeDetailMap.put("destPlace", apinRouteDetailInfo.getDestPlace());
				routeDetailMapList.add(routeDetailMap);
			}
			routeMap.put("id", apinOrder.getId());
			ApinRoute apinRoute = routeDao.getByProperty(ApinRoute.class, "routeNo", apinOrder.getRouteNo());
			if(apinRoute != null ){
				routeMap.put("routeType", apinRoute.getRouteType());
				routeMap.put("createTime", apinRoute.getCreateTime().getTime());
				routeMap.put("routeNo", apinRoute.getRouteNo());
				routeMap.put("showNo", apinRoute.getShowNo());
				routeMap.put("description", apinRoute.getDescription());
			}
			routeMap.put("passengerNum", apinOrder.getActualPassengerNum());
			if((byte)2 == apinOrder.getStatus()){
				routeMap.put("status", 5);
			}else if((byte)5 == apinOrder.getStatus()){
				routeMap.put("status", 8);
			}else{
				routeMap.put("status", apinOrder.getStatus());
			}
			routeMap.put("orderNo", apinOrder.getOrderNo());
			routeMap.put("journeyId", apinOrder.getJourneyId());
			routeMap.put("currentTime", System.currentTimeMillis());
			routeMap.put("routeDetail", routeDetailMapList);
			
			routeMap.put("passengerNum", apinOrder.getActualPassengerNum());
			routeMap.put("totalPrice", apinOrder.getActualPayCash());
			ApinMerchantRoutePriceInfo routePriceInfo = priceDao.getByID(ApinMerchantRoutePriceInfo.class, apinOrder.getPriceId());
			if(routePriceInfo != null ){
				routeMap.put("restValidateTime", routePriceInfo.getRestLastPayTime().getTime());
				routeMap.put("advancePrice", getTwoPoint(apinOrder.getTotalPrice().multiply(routePriceInfo.getDepositRatio())));
				routeMap.put("restPrice", getTwoPoint(apinOrder.getRestPrice()));
				routeMap.put("price", routePriceInfo.getPrice());
			}
			routeMap.put("advanceRemainTime", commonOrderService.getRemainPayTime(apinOrder.getOrderNo(), apinOrder.getTravelAgencyId()));
			searchMap.put("journeyId", String.valueOf(apinOrder.getJourneyId()));
			List<ApinMerchantFlightDetailInfo> flightDetailList = flightDetailDao.getFlightBySearchMap(searchMap);
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
				routeMap.put("departAirport", flightDetailInfo.getDepartAirport());
				routeMap.put("arriveAirport", flightDetailInfo.getArriveAirport());
				routeMap.put("hasTurn", flightDetailInfo.getHasTurn());
			}
			routeMapList.add(routeMap);
		}
		resultMap.put("routeList", routeMapList);
	}
	
}
