package com.apin.modules.routes.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.apin.common.utils.ValidateParamUtils;
import com.apin.modules.common.service.ApinSendMessageService;
import com.apin.modules.flights.dao.ApinMerchantFlightInfoDao;
import com.apin.modules.routes.bean.ApinMerchantRoutePriceInfo;
import com.apin.modules.routes.bean.ApinRoute;
import com.apin.modules.routes.bean.ApinRouteDetailInfo;
import com.apin.modules.routes.dao.ApinMerchantRoutePriceInfoDao;
import com.apin.modules.routes.dao.ApinRouteDao;
import com.apin.modules.routes.dao.ApinRouteDetailInfoDao;

/**
 * 后台系统出价
 * @author Young
 * @date 2017年2月6日 下午7:34:10
 * @version 1.0 
 */
@Service
public class ApinAdminOfferPriceService {

	private Logger logger = LoggerFactory.getLogger(ApinAdminOfferPriceService.class);
	
	
	
	@Autowired
	ApinMerchantRoutePriceService merchantPriceService;
	@Autowired
	ApinMerchantFlightInfoDao flightInfoDao;
	@Autowired
	ApinMerchantRoutePriceInfoDao merchantPriceDao;
	@Autowired
	ApinRouteDao routeDao;
	@Value("${offered.price.validate.time}")
	private String priceValidateTime;
	@Value("${apin.customServicePhone}")
	private String customServicePhone;
	@Autowired
    ApinSendMessageService sendMessageService;
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	ApinSendMessageThread sendMessageThread;
	@Autowired
	ApinRouteDetailInfoDao routeDetailDao;
	
	/**后台系统
	 * 行程出价
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
			logger.info("admin offeredPrice get something wrong ");
			return response;
		}
		return response;
	}
	
	/**
	 * 后台出价 
	 * @return
	 */
	@Transactional
	public boolean offerPrice(Map<String, String> paramsMap,List<Map<String, String>> paramMapList, Map<String, Object> resultMap) throws Exception{
		Map<String, String> searchMap =new HashMap<>();
		searchMap.put("routeNo", paramsMap.get("routeNo"));
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
				// 后台传入固定的userId
				routePrice.setSupplierId(paramsMap.get("userId"));
			    int isInsert = merchantPriceDao.insert(routePrice);
			    if(isInsert ==0){
			    	logger.info("when insert offerprice get something wrong");
			    	throw new Exception("=============when insert offerprice get something wrong  admin==================");
			    }
			}
			if(containMap.size() == paramMapList.size()){
				return false;
			}else if(containMap.size()==0){
				resultMap.put("message", "出价成功");
			}
			List<ApinRouteDetailInfo> routeDetailList = routeDetailDao.findBySearchMap(searchMap);
			// 第一次出价时需要有短信通知
			if((byte)2 != apinRoute.getRouteStatus()){
				final List<String> phoneList =new  ArrayList<String>();
				final List<String> paramList =new  ArrayList<String>();
				if(routeDetailList != null && routeDetailList.size()>0){
					ApinRouteDetailInfo routeDetailInfo = routeDetailList.get(0);
					StringBuffer sb = new StringBuffer();
					sb.append(DateUtil.formatDate(routeDetailInfo.getDepartDate(), "yyyy-MM-dd"));
					sb.append("号从");
					sb.append(routeDetailInfo.getDepartPlace());
					sb.append("到");
					sb.append(routeDetailInfo.getDestPlace());
					sb.append("，"); 
					paramList.add(sb.toString());
					paramList.add(customServicePhone);
					phoneList.add(apinRoute.getUserMobile());
					sendMessageThread.setModelName("firstPriceNotice");
					sendMessageThread.setTitle("商户出价通知");
					sendMessageThread.setPhone(phoneList);
					sendMessageThread.setParamList(paramList);
					taskExecutor.execute(sendMessageThread);
				}
			}
			apinRoute.setRouteStatus((byte)2);
			// 将行程行程的状态改为已经出价
			ApinRoute  updateRoute = new ApinRoute();
			updateRoute.setId(apinRoute.getId());
			updateRoute.setRouteStatus((byte)2);
			routeDao.updateNotNull(updateRoute);
			return true;
		}
		return false;
	}
	
	
}
