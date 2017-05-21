package com.apin.common.thread;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.apin.modules.common.service.ApinSendMessageService;

/**
 * 消息推送线程
 * 短信发送线程
 * @author Young
 * @date 2016年11月8日
 */
public class ApinSendMessageThread implements Runnable{

	
	private String modelName;
	private String title;
	private List<String> phone;
	private List<String> paramList;
	private Map<String, String> paramMap 
	;
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setPhone(List<String> phone) {
		this.phone = phone;
	}
	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}
	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	@Autowired
	ApinSendMessageService sendMessageSerice;
	@Override
	public void run() {
		sendMessageSerice.sendMessage(modelName, title, phone, paramList);
		sendMessageSerice.pushMessage(paramMap);
	}

}
