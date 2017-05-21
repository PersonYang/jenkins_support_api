package com.apin.modules.routes.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apin.common.response.ApinResponse;
import com.apin.common.response.ApinResponseUtil;
import com.apin.common.response.ErrorEnum;
import com.apin.modules.flights.service.ApinFlightService;
import com.apin.modules.orders.service.ApinOrderService;
import com.apin.modules.routes.service.ApinAdminOfferPriceService;
import com.apin.modules.routes.service.ApinMerchantRoutePriceService;
import com.apin.modules.routes.service.ApinMerchantRouteService;
import com.apin.modules.routes.service.ApinRouteRePriceService;

/**
 * 
 * @author Young
 * @date 2017年1月18日 上午9:31:33
 * @version 1.0 
 */
@Controller
@RequestMapping("/api/web/support/routes")
public class ApinRoutesController {

	@Autowired
	ApinMerchantRouteService routeService;
	@Autowired
	ApinMerchantRoutePriceService routePriceService;
	@Autowired
	ApinFlightService flightService;
	@Autowired
	ApinAdminOfferPriceService adminPriceService;
	@Autowired
	ApinOrderService orderService;
	@Autowired
	ApinRouteRePriceService routeRePriceService; 
	/**
	 * 谈一谈之后
	 * 供应商 同意价格
	 * 更改价格 行程人数.
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/reoffer/price",method=RequestMethod.POST)
	@ResponseBody
	public ApinResponse<Map<String, Object>> rePutOfferPrice(@RequestBody String params){
		long requestTime = System.currentTimeMillis();
		ApinResponse<Map<String, Object>> response;
		try {
			response = routeRePriceService.rePutOfferPrice(params);
		} catch (Exception e) {
			Map<String, Object> resultMap = new HashMap<>();
			response = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			resultMap.put("status", false);
			resultMap.put("message", "操作失败，请稍后重试");
			response.setBody(resultMap);
			e.printStackTrace();
		}
		return response;
	}
	
	
	/**
	 * 支持web 和app调用
	 * 行程出价
	 * @param routeNo  行程号
	 * @param depositRatio 定金预付比例
	 * @param restLastTicketTime 出票时限
	 * @param ticketRate 出票率
	 * @param submitParam 
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/offer/price",method=RequestMethod.POST)
	@ResponseBody
	public ApinResponse<Map<String, Object>> putOfferPrice(@RequestBody String params){
		ApinResponse<Map<String, Object>> response = routeService.putOfferPrice(params);
		return response;
	}
	
	/**支持web调用
	 * 我的订单中 status
	 *  不传 查询全部订单
	 *  app调用 传入如下状态 
	 * 1，待支付。2，待出票。3，已出票，4，已失效 ,等订单
	 * @param pageParam
	 * @param searchParam
	 * @return
	 */
	@RequestMapping(value="/handled",method=RequestMethod.GET)
	@ResponseBody
	public ApinResponse<Map<String, Object>> handledList(String pageParam,String searchParam){
		ApinResponse<Map<String, Object>> response = orderService.handledList(pageParam,searchParam);
		return response;
	}
	
	
	/**
	 * 获取等待处理的行程信息
	 * 待出价，已出价.
	 * @param pageParam
	 * @param searchParam
	 * @return
	 */
	@RequestMapping(value="/waithandle",method=RequestMethod.GET)
	@ResponseBody
	public ApinResponse<Map<String, Object>> getWaitHandle(String pageParam,String searchParam){
		ApinResponse<Map<String, Object>> response = routeService.getWaitHandle(pageParam,searchParam);
		return response;
	}
	
	/**
	 * 出价时获取航班信息
	 * @param routeNo
	 * @return 
	 */
	@RequestMapping(value="/flight",method=RequestMethod.GET)
	@ResponseBody
	public ApinResponse<List<Map<String, Object>>> getEnquiryFlightInfo(String routeNo,String userId){
		ApinResponse<List<Map<String, Object>>> response = routeService.getEnquiryFlightInfo(routeNo,userId);
		return response;
	}
	/**
	 * 获取一个行程当前供应商
	 * 所有出价信息
	 * @param routeNo
	 * @return 
	 */
	@RequestMapping(value="/offered/flight",method=RequestMethod.GET)
	@ResponseBody
	public ApinResponse<List<Map<String, Object>>> getOfferedFlightInfo(String routeNo,String userId){
		ApinResponse<List<Map<String, Object>>> response = routePriceService.getOfferedFlightInfo(routeNo,userId);
		return response;
	}
	
	/**
	 * 统一获取航班详情
	 * @param journeyId 行程ID
	 * @return 
	 */
	@RequestMapping(value="/flight/detail",method=RequestMethod.GET)
	@ResponseBody
	public ApinResponse<List<Map<String, Object>>> getDetailInfo(String journeyId){
		ApinResponse<List<Map<String, Object>>> response = flightService.getDetailInfo(journeyId);
		return response;
	}
	/**
	 * 航线列表进入航线详情
	 * 头部数据获取
	 * @param journeyId 行程ID
	 * @return 
	 */
	@RequestMapping(value="/flight/head/detail",method=RequestMethod.GET)
	@ResponseBody
	public ApinResponse<Map<String, Object>> getHeadDetailInfo(String groupId){
		ApinResponse<Map<String, Object>> response = flightService.getHeadDetailInfo(groupId);
		return response;
	}
	
	/**
	 * 后台系统报价情况
	 * @param params
	 * @return
	 */
	@RequestMapping(value="/admin/offer/price",method=RequestMethod.POST)
	@ResponseBody
	public ApinResponse<Map<String, Object>> adminPutOfferPrice(@RequestBody String params){
		ApinResponse<Map<String, Object>> response = adminPriceService.putOfferPrice(params);
		return response;
	}

	
	
}
