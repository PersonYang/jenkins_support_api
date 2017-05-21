package com.apin.modules.flights.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apin.common.response.ApinResponse;
import com.apin.modules.flights.service.ApinFlightVersionService;

/**
 * 
 * 改版发布航班
 * @author Young
 * @date 2017年3月13日 上午11:14:21
 * @version 1.0 
 */
@Controller
@RequestMapping("/api/web/support/flight/v2")
public class ApinFlightVersionController {

	
	@Autowired
	ApinFlightVersionService flightVersionService;
	/**
	 * app web 发布航班
	 * @param param
	 * @return
	 */
	@RequestMapping(value="/publish",method=RequestMethod.POST)
	@ResponseBody
	public ApinResponse<Map<String, Object>> publishFlight(@RequestBody String param){
		ApinResponse<Map<String, Object>> response = flightVersionService.publishFlight(param);
		return response;
	}
	
	
}
