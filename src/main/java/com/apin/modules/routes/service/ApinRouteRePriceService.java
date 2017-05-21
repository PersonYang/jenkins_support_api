package com.apin.modules.routes.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apin.common.response.ApinResponse;
import com.apin.common.response.ApinResponseUtil;
import com.apin.common.response.ErrorEnum;
import com.apin.common.utils.JsonUtil;
import com.apin.modules.routes.bean.ApinMerchantRoutePriceInfo;
import com.apin.modules.routes.bean.ApinRoute;
import com.apin.modules.routes.dao.ApinMerchantRoutePriceInfoDao;
import com.apin.modules.routes.dao.ApinRouteDao;

/**
 * 
 * 供应商同意旅行社谈一谈价格之后
 * 将原来的价格更改，
 * @author Young
 * @date 2017年2月24日 下午1:50:16
 * @version 1.0 
 */
@Service
public class ApinRouteRePriceService {

	private static Logger logger =LoggerFactory.getLogger(ApinRouteRePriceService.class);

	@Autowired
	ApinMerchantRoutePriceInfoDao priceDao;
	@Autowired
	ApinRouteDao routeDao;
	
	@SuppressWarnings("unchecked")
	@Transactional
	public ApinResponse<Map<String, Object>> rePutOfferPrice(String params) throws Exception {
		long requestTime = System.currentTimeMillis();
		Map<String, Object> resultMap = new HashMap<>();
		ApinResponse<Map<String, Object>> response = null;
		// 验证提交的参数是否为空
		Map<String, String> paramsMap = JsonUtil.parseToMap(params);
		logger.info("param"+params);
		if (!StringUtils.isNumeric(paramsMap.get("personNum")) || !StringUtils.isNumeric(paramsMap.get("price"))
				|| StringUtils.isBlank(paramsMap.get("priceId")) || StringUtils.isBlank(paramsMap.get("userId"))) {
			resultMap.put("status", false);
			resultMap.put("message", "参数为空");
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
			response.setBody(resultMap);
			return response;
		}
		ApinMerchantRoutePriceInfo priceInfo = priceDao.getByID(ApinMerchantRoutePriceInfo.class,
				Integer.valueOf(paramsMap.get("priceId")));
		if (priceInfo == null || !paramsMap.get("userId").equals(priceInfo.getSupplierId())) {
			resultMap.put("status", false);
			resultMap.put("message", "参数出错");
			logger.info("======the priceInfo is null ,or the priceInfo  ower is not equals userId =====");
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
			response.setBody(resultMap);
			return response;
		}
		ApinRoute apinRoute = routeDao.getByProperty(ApinRoute.class, "routeNo", priceInfo.getRouteNo());
		if(apinRoute == null){
			resultMap.put("status", false);
			resultMap.put("message", "参数出错");
			logger.info("======apinRoute is null =====");
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
			response.setBody(resultMap);
			return response;
		}
		priceInfo.setHasChat((byte) 0);
		priceInfo.setPrice(new BigDecimal(paramsMap.get("price")));
		if (priceDao.update(priceInfo) > 0) {
			ApinRoute updateRoute = new  ApinRoute();
				updateRoute.setId(apinRoute.getId());
				updateRoute.setPassengerNum(Integer.valueOf(paramsMap.get("personNum")));
				if(routeDao.updateNotNull(updateRoute) >0){
					resultMap.put("status", true);
					resultMap.put("message", "操作成功");
				}else{
					throw new Exception("更改行程人数信息,MYSQL出错");
			  }
		}else{
			resultMap.put("status", false);
			resultMap.put("message", "操作失败");
		}
		response = ApinResponseUtil.good(requestTime, resultMap);
		response.setBody(resultMap);
		return response;
	}
	
	
	
	
	
	
	
}
