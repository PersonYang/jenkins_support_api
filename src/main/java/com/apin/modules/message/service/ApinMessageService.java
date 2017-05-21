package com.apin.modules.message.service;

import java.util.Arrays;
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
import com.apin.common.utils.ValidateParamUtils;
import com.apin.modules.message.bean.ApinMessage;
import com.apin.modules.message.dao.ApinMessageDao;

/**
 * 
 * 消息相关
 * @author Young
 * @date 2017年2月22日 上午9:34:59
 * @version 1.0 
 */
@Service
public class ApinMessageService {
	private static final Logger logger = LoggerFactory.getLogger(ApinMessageService.class);
	
	@Autowired
	ApinMessageDao messageDao;
	@Autowired
	Redis redis;
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> getWholeList(String searchParam, String pageParam) {
		long responseTime = System.currentTimeMillis();
		ApinResponse<Map<String, Object>> response =null;
		Map<String, Object> resultMap = new HashMap<>();
		try {
			Map<String, String> pageMap = JsonUtil.parseToMap(pageParam);
			Map<String, String> searchMap = JsonUtil.parseToMap(searchParam);
			if(!ValidateParamUtils.validatePageParam(pageMap) || StringUtils.isBlank(searchMap.get("userId"))){
				logger.info("===========ERROR_PARAM_ISBLANK============");
				response = ApinResponseUtil.bad(responseTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				response.setBody(resultMap);
				return response;
			}
			long recordCount = messageDao.getCountBySearchMap(searchMap);
			if(recordCount >0){
				PageCommon page = new PageCommon(Long.valueOf(pageMap.get("pageSize")), recordCount,Long.valueOf(pageMap.get("pageIndex")),
						pageMap.get("sortField"), pageMap.get("sortOrder"));
				List<ApinMessage> messageList = messageDao.getListBySearchMap(page,searchMap);
				resultMap.put("messageList", messageList);
				resultMap.put("pageCount", page.getPageCount());
				resultMap.put("pageCurrent", page.getCurrentPage());
			}
			response = ApinResponseUtil.good(responseTime);
			response.setBody(resultMap);
			return response;
		} catch (Exception e) {
			logger.info("===========mysql get something wrong ============");
			response = ApinResponseUtil.bad(responseTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
			response.setBody(resultMap);
			return response;
		}
	}
	
	/**
	 * 标记信息为已读或删除
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> motifyMessage(String param) {
		long responseTime = System.currentTimeMillis();
		ApinResponse<Map<String, Object>> response =null;
		Map<String, Object> resultMap = new HashMap<>();
		try {
			if(!JsonUtil.validateIsKeyValue(param)){
				logger.info("===========ERROR_PARAM_ISBLANK============");
				response = ApinResponseUtil.bad(responseTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				response.setBody(resultMap);
				return response;
			}
			Map<String, String> paramMap = JsonUtil.parseToMap(param);
			if(StringUtils.isBlank(paramMap.get("type")) || StringUtils.isBlank("messageIdArray")){
				logger.info("===========ERROR_PARAM_ISBLANK============");
				response = ApinResponseUtil.bad(responseTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				response.setBody(resultMap);
				return response;
			}
			if(!"0".equals(paramMap.get("type")) && !"1".equals(paramMap.get("type"))){
				logger.info("===========ERROR_PARAM_ISBLANK============type is wrong ");
				response = ApinResponseUtil.bad(responseTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				response.setBody(resultMap);
				return response;
			}
			String [] messIdArray = paramMap.get("messageIdArray").replace("[", "").replace("]", "").split(",");
			List<String> messIdList = Arrays.asList(messIdArray);
			if(messIdList== null || !(messIdList.size()>0 )){
				logger.info("===========ERROR_PARAM_ISBLANK============");
				response = ApinResponseUtil.bad(responseTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				response.setBody(resultMap);
				return response;
			}
			Map<String, String> paramsMap = new HashMap<>();
			// 0,标记已读。1，标记删除
			if("0".equals(paramMap.get("type"))){
				paramsMap.put("hasRead", "1");
			}else if("1".equals(paramMap.get("type"))){
				paramsMap.put("hasDelete", "1");
			}
			messageDao.updateNotNull(paramsMap,messIdList);
			response = ApinResponseUtil.good(responseTime);
			response.setBody(resultMap);
			return response;
		} catch (Exception e) {
			logger.info("===========mysql get something wrong ============");
			response = ApinResponseUtil.bad(responseTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
			response.setBody(resultMap);
			return response;
		}
	}

	/**
	 * 
	 * 获取最新四条 未读未删除的信息
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> topFive(String userId) {
		long responseTime = System.currentTimeMillis();
		ApinResponse<Map<String, Object>> response =null;
		Map<String, Object> resultMap = new HashMap<>();
		try {
			if(StringUtils.isBlank(userId)){
				logger.info("===========ERROR_PARAM_ISBLANK============"+userId);
				response = ApinResponseUtil.bad(responseTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				response.setBody(resultMap);
				return response;
			}
			List<ApinMessage> messageList = messageDao.getListByUserId(userId);
			response = ApinResponseUtil.good(responseTime);
			resultMap.put("messList", messageList);
			response.setBody(resultMap);
			return response;
		} catch (Exception e) {
			logger.info("===========mysql get something wrong ============");
			response = ApinResponseUtil.bad(responseTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
			response.setBody(resultMap);
			return response;
		}
	}



	
	
}
