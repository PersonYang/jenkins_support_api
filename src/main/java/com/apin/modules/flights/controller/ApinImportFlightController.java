package com.apin.modules.flights.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.apin.common.response.ApinResponse;
import com.apin.common.response.ApinResponseUtil;
import com.apin.common.response.ErrorEnum;
import com.apin.modules.flights.service.ApinImportFlightSevice;


/**
 * 供应商通过表格实现航班添加的功能
 * 航班导入功能
 * @author Young
 * @date 2017年2月4日 上午9:25:54
 * @version 1.0 
 */
@Controller
@RequestMapping("/api/web/support/flight/import")
public class ApinImportFlightController {

	private static final Logger logger = LoggerFactory.getLogger(ApinImportFlightController.class);
	
	@Autowired
	ApinImportFlightSevice importFlightSevice;
	/**
	 * excel 批量导入航班信息
	 * @param file
	 * @param request
	 * @param model
	 * @param httpSession
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="excel",method=RequestMethod.POST)
	@ResponseBody
	public ApinResponse<Map<String, Object>> upload(@RequestParam(value = "file", required = false)MultipartFile file,
			HttpServletRequest request,String userId) {
		Map<String, Object> resultMap = new HashMap<>();
		ApinResponse<Map<String, Object>> apinResponse =null;
		long startDate = System.currentTimeMillis();
		if(file.getSize() == 0){
			apinResponse = ApinResponseUtil.good(startDate);
			resultMap.put("status", false);
			resultMap.put("message", "请选择文件");
			return apinResponse;
		}
		if(StringUtils.isBlank(userId) ){
			apinResponse = ApinResponseUtil.bad(startDate, ErrorEnum.ERROR_PARAM_ISBLANK);
			resultMap.put("status", false);
			resultMap.put("message", "userId is null");
			return apinResponse;
		}
        // 获得文件名：   
        String filename = file.getOriginalFilename();   
        // 获得输入流：   
        try {
			InputStream input = file.getInputStream();
			FileInputStream fileInput = (FileInputStream) input;
			apinResponse = importFlightSevice.importExecel(fileInput, userId, filename);
			long endDate = System.currentTimeMillis();
			logger.info("====================================="+(endDate - startDate)/1000);
		} catch (IOException e) {
			apinResponse = ApinResponseUtil.bad(startDate, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			resultMap.put("status", false);
			resultMap.put("message", "请选择文件");
			resultMap.put("status", "数据格式错误请修改");
			e.printStackTrace();
			return apinResponse;
		}  
		return apinResponse;  
	}
	
	
}
