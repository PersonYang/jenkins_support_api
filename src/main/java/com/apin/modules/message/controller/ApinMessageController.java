package com.apin.modules.message.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apin.common.response.ApinResponse;
import com.apin.modules.message.service.ApinMessageService;

/**
 * 消息部分 
 * @author Young
 * @date 2017年2月21日 下午4:21:15
 * @version 1.0 
 */
@Controller
@RequestMapping("/api/web/message")
public class ApinMessageController {

	
	@Autowired
	ApinMessageService messageService;
	/**
	 * 获取旅行社消息相关
	 * @param searchParam
	 * @param pageParam
	 * @return 
	 */
	@RequestMapping(value="whole",method=RequestMethod.GET)
	@ResponseBody
	public ApinResponse<Map<String, Object>> getWholeList(String searchParam,String pageParam){
		ApinResponse<Map<String, Object>> response = messageService.getWholeList(searchParam,pageParam);
		return response;
	}
	
	@RequestMapping(value="topfive",method=RequestMethod.GET)
	@ResponseBody
	public ApinResponse<Map<String, Object>> getWholeList(String userId){
		ApinResponse<Map<String, Object>> response = messageService.topFive(userId);
		return response;
	}
	
	/**
	 * 将信息标记为已读，或删除
	 * @param param
	 * @return
	 */
	@RequestMapping(value="motify",method=RequestMethod.POST)
	@ResponseBody
	public ApinResponse<Map<String, Object>> motifyMessage(@RequestBody String param){
		ApinResponse<Map<String, Object>> response = messageService.motifyMessage(param);
		return response;
	}
	
}
