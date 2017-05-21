package com.apin.modules.airline.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apin.base.service.AirLineFlightService;
import com.apin.common.response.ApinResponse;

/**
 * 获取航线航班接口
 * @author Young
 * @date 2017年1月11日 上午11:21:21
 * @version 1.0 
 */
@Controller
@RequestMapping("/api/web/airline")
public class ApinAirLineController {

	@Autowired
	AirLineFlightService airLineService;
	
	/**
	 * 获取航班信息
	 * @param param 航班信息参数 航班号
	 * @param auth 身份验证
	 * @return
	 */
	@RequestMapping(value="info",method=RequestMethod.GET)
	@ResponseBody
	public ApinResponse<Map<String, Object>> getInfo(String flightNo,String departDate){
		return airLineService.getFlightInfo(flightNo,departDate);
	}
}
