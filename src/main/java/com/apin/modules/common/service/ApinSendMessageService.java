package com.apin.modules.common.service;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.apin.common.utils.HttpJuheFlightUtils;
import com.apin.common.utils.HttpRequest;
import com.apin.common.utils.JsonUtil;
import com.google.gson.JsonObject;

/**
 * 消息推送服务
 * 短信通知服务
 * @author Young
 * @date 2016年10月29日
 */
@Service
public class ApinSendMessageService {

	private static final Logger logger = LoggerFactory.getLogger(ApinSendMessageService.class);
	
	@Value("${apin.send.message.url}")
	private String sendMsgUrl;
	@Value("${apin.push.message.url}")
	private String pushMessageUrl;
	
	/**
	 * @param modelName 模板名称
	 * @param title 模板标题
	 * @param phoneList 参数列表
	 * @param paramList 手机列表
	 * @return
	 */
	public boolean sendMessage(String modelName,String title,List<String> phone,List<String> paramList){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("modelName", modelName);
		jsonObject.addProperty("title", title);
		jsonObject.addProperty("phones", phone.toString());
		jsonObject.addProperty("params", paramList.toString());
		try {
			logger.info("=====================sendMsgUrl================"+sendMsgUrl);
			String result = HttpRequest.postJson(sendMsgUrl, jsonObject, "utf-8");
			String code = JsonUtil.getJsonStr("code", JsonUtil.getJsonStr("head", result));
			logger.info("=====================the message result code ================"+code+"msg"+JsonUtil.getJsonStr("msg", JsonUtil.getJsonStr("head", result)));
			if("00000000".equals(code)){
				return true;
			}
		} catch (HttpException e) {
			e.printStackTrace();
			logger.info("=====================to send message get fail exception================"+modelName);
			return false;
		}
		logger.info("=====================get the interface successful,but the back code is not 00000000================"+modelName);
		return false;
	}
	
	/**
	 * 添加消息 调用web旅行社端
	 * 接口
	 *	paramMap
	 * @return
	 */
	public boolean pushMessage(Map<String, String> paramMap){
		try {
			logger.info("=====================sendMsgUrl================"+sendMsgUrl);
			String result = HttpJuheFlightUtils.net(pushMessageUrl, paramMap, "POST");
			String code = JsonUtil.getJsonStr("code", JsonUtil.getJsonStr("head", result));
			logger.info("=====================the message result code ================"+code+"msg"+JsonUtil.getJsonStr("msg", JsonUtil.getJsonStr("head", result)));
			if("00000000".equals(code)){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("=====================pushMessage get fail exception================");
			return false;
		}
		logger.info("============the travel web interface get something wrong ================");
		return false;
	}
}
