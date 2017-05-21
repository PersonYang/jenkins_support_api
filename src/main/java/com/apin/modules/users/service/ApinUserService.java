package com.apin.modules.users.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apin.common.response.ApinResponse;
import com.apin.common.response.ApinResponseUtil;
import com.apin.common.response.ErrorEnum;
import com.apin.common.utils.JsonUtil;
import com.apin.common.utils.ValidateParamUtils;
import com.apin.modules.users.bean.ApinAccount;
import com.apin.modules.users.dao.ApinAccountDao;

/**
 * @author Young
 * @date 2017年2月9日 下午5:17:32
 * @version 1.0 
 */
@Service
public class ApinUserService {
	
	private final Logger logger = LoggerFactory.getLogger(ApinUserService.class);
	
	@Autowired
	ApinAccountDao accountDao;
	/**
	 * 注册用户
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> registerMerchant(String params) {
		long requestTime = System.currentTimeMillis();
		ApinResponse<Map<String, Object>> response = null;
		try {
			Map<String, Object> resultMap = new HashMap<>();
			if (!ValidateParamUtils.validateRegisterParam(params)) {
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("=================registerMerchant is blank =======================");
				return response;
			}
			Map<String, String> paramMap = JsonUtil.parseToMap(params);
			ApinAccount  account = accountDao.findById(paramMap.get("user_id"));
			if(account == null ){
				response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				logger.info("=================registerMerchant is blank =======================");
				return response;
			}
			Map<String, Object> merchantMap = accountDao.getMerchantByPhone(account.getPhone());
			if(merchantMap != null ){
				resultMap.put("status", false);
				resultMap.put("message", "您已经提交过注册信息，请等待审核通过");
				response = ApinResponseUtil.good(requestTime);
				response.setBody(resultMap);
				return response;
			}
			paramMap.put("phone", account.getPhone());
			paramMap.put("is_merchant","0");
			paramMap.put("flag", "0");
			paramMap.remove("user_id");
			if(accountDao.insertMerchantMap(paramMap)>0){
				resultMap.put("status", true);
				resultMap.put("message", "申请成功");
			}else{
				resultMap.put("status", false);
				resultMap.put("message", "申请失败");
			}
			response = ApinResponseUtil.good(requestTime);
			response.setBody(resultMap);
		} catch (Exception e) {
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
			logger.info(
					"=================registerMerchant =======================");
			e.printStackTrace();
			return response;
		}
		return response;
	}
	
	
	
	
	
	
}
