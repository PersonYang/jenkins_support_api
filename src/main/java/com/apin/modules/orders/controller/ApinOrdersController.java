package com.apin.modules.orders.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apin.common.response.ApinResponse;
import com.apin.modules.orders.service.ApinOrderService;

/**
 * 供应商订单相关
 * @author Young
 * @date 2017年1月18日 上午10:30:03
 * @version 1.0 
 */
@Controller
@RequestMapping("/api/web/support/orders")
public class ApinOrdersController {

	
	@Autowired
	ApinOrderService orderService;
	/**
	 * flash version  for web
	 *  waiting pay money
	 *  waiting push ticket 
	 * @param pageParam 
	 * @param searchParam
	 * @return 
	 */
	@RequestMapping(value="/partinfo",method=RequestMethod.GET)
	@ResponseBody
	public ApinResponse<Map<String, Object>> getOrderInfo(String pageParam,String searchParam){
		ApinResponse<Map<String, Object>> response = orderService.getOrderInfo(pageParam,searchParam);
		return response;
	}
	
	
	/**
	 * 出票时获取航班乘机人信息
	 * @return
	 */
	@RequestMapping(value="/flight",method=RequestMethod.GET)
	@ResponseBody
	public ApinResponse<Map<String, Object>> getOrderInfoWhenPush(String orderNo){
		ApinResponse<Map<String, Object>> response = orderService.getOrderInfoWhenPush(orderNo);
		return response;
	}
	
	/**
	 * 供应商订单出票
	 * @return
	 */
	@RequestMapping(value="/push",method=RequestMethod.POST)
	@ResponseBody
	public ApinResponse<Map<String, Object>> pushTicket(@RequestBody String params){
		ApinResponse<Map<String, Object>> response = orderService.pushTicket(params);
		return response;
	}
	/**
	 * 后台订单出票
	 * @return
	 */
	@RequestMapping(value="/admin/push",method=RequestMethod.POST)
	@ResponseBody
	public ApinResponse<Map<String, Object>> adminPushTicket(@RequestBody String params){
		ApinResponse<Map<String, Object>> response = orderService.pushTicket(params);
		return response;
	}
	
	
	
	
	
	
}
