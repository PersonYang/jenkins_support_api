package com.apin.modules.routes.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import com.apin.modules.common.service.ApinSendMessageService;
import com.apin.modules.flights.dao.ApinMerchantFlightInfoDao;
import com.apin.modules.routes.bean.ApinMerchantRoutePriceInfo;
import com.apin.modules.routes.bean.ApinRoute;
import com.apin.modules.routes.bean.ApinRouteDetailInfo;
import com.apin.modules.routes.bean.ApinRouteDistributeMerchantInfo;
import com.apin.modules.routes.dao.ApinMerchantRoutePriceInfoDao;
import com.apin.modules.routes.dao.ApinRouteDao;
import com.apin.modules.routes.dao.ApinRouteDetailInfoDao;
import com.apin.modules.routes.dao.ApinRouteDistributeMerchantInfoDao;

/**
 * 
 * 商户出价表对应
 * @author Young
 * @date 2017年1月18日 上午9:47:08
 * @version 1.0 
 */
@Service
public class ApinMerchantRoutePriceService {

	private static final Logger logger = LoggerFactory.getLogger(ApinMerchantRoutePriceService.class);
	
	@Autowired
	ApinMerchantFlightInfoDao flightInfoDao;
	@Autowired
	ApinMerchantRoutePriceInfoDao merchantPriceDao;
	@Autowired
	ApinRouteDistributeMerchantInfoDao distributeMerchantInfoDao;
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
			searchMap.remove("userId");
			List<ApinRouteDetailInfo> routeDetailList = routeDetailDao.findBySearchMap(searchMap);
			// 第一次出价时需要有短信通知
			// 加上flag的判断
			if((byte)2 != apinRoute.getRouteStatus() &&  apinRoute.getFlag() == (byte)0){
				final List<String> phoneList =new  ArrayList<String>();
				final List<String> paramList =new  ArrayList<String>();
				Map<String, String> paramMap =  new HashMap<>();
				// 调用旅行社web 进行消息推送
				paramMap.put("messageDetail", String.valueOf(apinRoute.getRouteNo()));
				paramMap.put("messageType", "1");
				paramMap.put("userId", apinRoute.getTravelAgencyId());
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
					sendMessageThread.setParamMap(paramMap);
					taskExecutor.execute(sendMessageThread);
				}
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

	/**
	 * 获取改行程当前供应商所有的出价航班信息
	 * @param routeNo
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<List<Map<String, Object>>> getOfferedFlightInfo(String routeNo, String userId) {
		long requestTime = System.currentTimeMillis();
		Map<String, Object> resultMap = new HashMap<>();
		ApinResponse<List<Map<String, Object>>> response =null;
		// 验证提交的参数是否为空
		if(StringUtils.isBlank(routeNo) || StringUtils.isBlank(userId)){
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
			return response;
		}
		try {
			response = ApinResponseUtil.good(requestTime, resultMap);
			List<Map<String, Object>> resultMapList = merchantPriceDao.getOfferedFlightInfo(routeNo,userId);
			response.setBody(resultMapList);
		} catch (Exception e) {
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
			logger.info("there is a question in querying flightInfo ");
			return response;
		}
		return response;
	}

	
	
	
	
	
	
	
}
