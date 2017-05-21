package com.apin.modules.orders.service;

import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.apin.common.utils.HttpRequest;
import com.apin.common.utils.JsonUtil;

@Service
public class ApinCommonOrdersService {
	
	private static final Logger logger = LoggerFactory.getLogger(ApinCommonOrdersService.class);
	
	@Value("${apin.product_path}")
	private String productPath;
	
	
	/**
	 * 获取订单的定金剩余支付时间
	 * @param orderNo 订单号
	 * @param userId 订单用户ID
	 * @return
	 */
	public String getRemainPayTime(Long orderNo,String userId){
		String getRemainPayTimeUrl = productPath+"/api/transactionService/orders/"+orderNo+"/remainTime";
		String result;
		try {
			result = HttpRequest.httpGet(getRemainPayTimeUrl, "userId="+userId, "utf-8");
			String code = JsonUtil.getJsonStr("code", JsonUtil.getJsonStr("head", result));
			if("00000000".equals(code)){
				logger.info("====getRemainPayTime====value"+JsonUtil.getJsonStr("remainPayTime", JsonUtil.getJsonStr("body", result)));
				return JsonUtil.getJsonStr("remainPayTime", JsonUtil.getJsonStr("body", result));
			}else{
				logger.info("====getRemainPayTime====other code");
				return "0";
			}
		} catch (HttpException e) {
			e.printStackTrace();
			logger.info("====getRemainPayTime====wrongException");
			return "0";
		}
	}
}
