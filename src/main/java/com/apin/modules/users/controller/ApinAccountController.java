package com.apin.modules.users.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apin.common.response.ApinResponse;
import com.apin.modules.users.service.ApinUserService;

/**
 * 
 * 
 * 
 * @author Young
 * @date 2017年2月9日 下午5:12:32
 * @version 1.0 
 */
@Controller
@RequestMapping("/api/web/support/user")
public class ApinAccountController {

	
	@Autowired
	ApinUserService userService;
	
	@RequestMapping(value="/register",method=RequestMethod.POST)
	@ResponseBody
	public ApinResponse<Map<String, Object>> registerMerchant(@RequestBody String params){
		ApinResponse<Map<String, Object>> response = userService.registerMerchant(params);
		return response;
	}
}
