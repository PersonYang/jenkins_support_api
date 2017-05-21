package com.apin.modules.flights.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.apin.common.response.ApinResponse;
import com.apin.common.response.ApinResponseUtil;
import com.apin.common.response.ErrorEnum;
import com.apin.common.utils.JsonUtil;

/**
 * 新版本航班相关
 * @author Young
 * @date 2017年3月13日 上午11:16:08
 * @version 1.0 
 */
@Service
public class ApinFlightVersionService {

	private  final Logger logger = LoggerFactory.getLogger(ApinFlightVersionService.class);
	@Autowired
	ApinRoundStrategyService roundStrategyService;
	@Autowired
	ApinSingleStrategyService singleStrategyService;
	@Autowired
	ApinUnionStrategyService unionStrategyService;
	/**
	 * 发布航班相关
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> publishFlight(String param) {
		long requestTime =System.currentTimeMillis();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ApinResponse<Map<String, Object>> apinResponse =null;
		try {
			logger.info("===the method publishFlight v2 params is ===="+param);
			Map<String, String> paramMap = JsonUtil.parseToMap(param);
			if(!PublishFlightUtils.valiParams(param)){
				logger.info("==========some param is blank v2 ========");
				apinResponse = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				resultMap.put("status", false);
				resultMap.put("message", "空参数");
				apinResponse.setBody(resultMap);
				return apinResponse;
			}
			//单程航班添加
			List<Map<String, String>> flightMapList = JsonUtil.parseToMapInList(paramMap.get("flightDetail"));
			if("1".equals(paramMap.get("flightType"))){
				resultMap = singleStrategyService.addSingleFlightVersion(paramMap,flightMapList,paramMap.get("userId"));
			}else if("2".equals(paramMap.get("flightType"))){
				resultMap = roundStrategyService.addRoundFlightVersion(paramMap,flightMapList,paramMap.get("userId"));
			}else if("3".equals(paramMap.get("flightType"))){
				resultMap = unionStrategyService.addUnionFlightVersion(paramMap,flightMapList,paramMap.get("userId"));
			}else{
				resultMap =new HashMap<>();
				resultMap.put("status", false);
				resultMap.put("message", "参数错误,flightType");
			}
			apinResponse = ApinResponseUtil.good(requestTime);
			apinResponse.setBody(resultMap);
			return apinResponse;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("==========the method publishFlight get somthing wrong v2 ========");
			apinResponse = ApinResponseUtil.good(requestTime);
			resultMap.put("status", false);
			resultMap.put("message", "网络异常，请稍后再试!");
			apinResponse.setBody(resultMap);
			return apinResponse;
		}
	}


	
}
