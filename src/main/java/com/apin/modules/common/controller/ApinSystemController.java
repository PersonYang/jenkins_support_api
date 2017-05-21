package com.apin.modules.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * 查看系统各种信息情况
 * @author Young
 * @date 2017年3月3日 下午2:17:38
 * @version 1.0 
 */
@Controller
@RequestMapping("/api/web/support/sys")
public class ApinSystemController {

	
	@RequestMapping("/thread")
	public String showThread(){
		return "showThread";
	}
}
