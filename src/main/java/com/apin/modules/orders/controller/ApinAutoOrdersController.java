package com.apin.modules.orders.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apin.common.response.ApinResponse;
import com.apin.modules.orders.service.ApinAutoOrderService;

/**
 * 供应商订单相关
 * @author Young
 * @date 2017年1月18日 上午10:30:03
 * @version 1.0 
 */
@Controller
@RequestMapping("/api/web/support/orders/auto")
public class ApinAutoOrdersController {

	@Autowired
	ApinAutoOrderService autoOrderService;

	
	/**
	 * 供应商订单出票
	 * @return
	 */
	@RequestMapping(value="/push",method=RequestMethod.POST)
	@ResponseBody
	public ApinResponse<Map<String, Object>> pushTicket(@RequestBody String params){
		ApinResponse<Map<String, Object>> response = autoOrderService.pushTicket(params);
		return response;
	}
	
	
	/**
	 * auto 调用
	 * @param params
	 * @return
	 */
	@RequestMapping(value="/offer/price",method=RequestMethod.POST)
	@ResponseBody
	public ApinResponse<Map<String, Object>> putOfferPrice(@RequestBody String params){
		ApinResponse<Map<String, Object>> response = autoOrderService.putOfferPrice(params);
		return response;
	}
	
	
	
	
	
	
	
}
