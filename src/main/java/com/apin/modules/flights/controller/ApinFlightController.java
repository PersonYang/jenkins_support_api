package com.apin.modules.flights.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apin.common.response.ApinResponse;
import com.apin.modules.flights.service.ApinAdminPublishFlightService;
import com.apin.modules.flights.service.ApinFlightService;

/**
 * flash version
 * @author Young
 * @date 2017年1月16日 上午9:26:18
 * @version 1.0 
 */
@Controller
@RequestMapping("/api/web/support/flight")
public class ApinFlightController {

	@Autowired
	ApinFlightService flightService;
	@Autowired
	ApinAdminPublishFlightService adminPushFlightService;
	
	
	
	/**
	 * 后台系统的临时团
	 * 航班信息添加
	 * 发布航班
	 * @param userId 用户ID
	 * @param param 航班参数情况
	 * @return
	 */
	@RequestMapping(value="/admin/publish",method=RequestMethod.POST)
	@ResponseBody
	public ApinResponse<Map<String, Object>> publishFlightFromAdmin(@RequestBody String param){
		ApinResponse<Map<String, Object>> response = adminPushFlightService.publishFlightFromAdmin(param);
		return response;
	}
	
	/**
	 * 发布航班
	 * @param userId 用户ID
	 * @param param 航班参数情况
	 * @return
	 */
	@RequestMapping(value="/publish",method=RequestMethod.POST)
	@ResponseBody
	public ApinResponse<Map<String, Object>> publishFlight(@RequestBody String param){
		ApinResponse<Map<String, Object>> response = flightService.publishFlight(param);
		return response;
	}
	


	/**
	 * 航线列表
	 * @param pageParam
	 * @param searchParam
	 * @return
	 */
	@RequestMapping(value="/whole",method=RequestMethod.GET)
	@ResponseBody
	public ApinResponse<Map<String, Object>> getWholeList(String pageParam,String searchParam){
		ApinResponse<Map<String, Object>> response = flightService.getWholeList(pageParam,searchParam);
		return response;
	}
	
	/**
	 * 删除航线(假删除) 同组航线
	 * @param pageParam
	 * @param searchParam
	 * @return
	 */
	@RequestMapping(value="/remove",method=RequestMethod.GET)
	@ResponseBody
	public ApinResponse<Map<String, Object>> removeFlight(String groupId){
		ApinResponse<Map<String, Object>> response = flightService.removeFlight(groupId);
		return response;
	}
	/**
	 * 编辑航线
	 * @param pageParam
	 * @param searchParam
	 * @return
	 */
	@RequestMapping(value="/motify",method=RequestMethod.POST)
	@ResponseBody
	public ApinResponse<Map<String, Object>> motifyFlightInfo(@RequestBody String filghtParam){
		ApinResponse<Map<String, Object>> response = flightService.motifyFlightInfo(filghtParam);
		return response;
	}
	
	
	
}
