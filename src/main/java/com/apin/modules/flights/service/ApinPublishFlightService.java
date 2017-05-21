package com.apin.modules.flights.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apin.common.utils.JsonUtil;

/**
 * 发布航班所用service
 * 涉及到单程，往返，多程
 * @author Young
 * @date 2017年1月21日 上午10:32:46
 * @version 1.0 
 */
@Service
public class ApinPublishFlightService {

	private Logger logger = LoggerFactory.getLogger(ApinPublishFlightService.class);
	@Autowired
	ApinSingleStrategyService singleStrategyService;
	@Autowired
	ApinRoundStrategyService roundStrategyService;
	@Autowired
	ApinUnionStrategyService unionStrategyService;
	/**
	 * 将航班信息保存进数据库
	 * @param paramMap 
	 * @param userId 用户ID
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> resultMapFlightInfo(Map<String, String> paramMap){
		List<Map<String, String>> flightMapList = JsonUtil.parseToMapInList(paramMap.get("flightDetail"));
		Map<String, Object> returnResultMap = null;
		try {
			//单程航班添加
			if("1".equals(paramMap.get("flightType"))){
				returnResultMap = singleStrategyService.addSingleFlight(paramMap,flightMapList,paramMap.get("userId"));
			}else if("2".equals(paramMap.get("flightType"))){
				returnResultMap = roundStrategyService.addRoundFlight(paramMap,flightMapList,paramMap.get("userId"));
			}else if("3".equals(paramMap.get("flightType"))){
				returnResultMap = unionStrategyService.addUnionFlight(paramMap,flightMapList,paramMap.get("userId"));
			}else{
				returnResultMap =new HashMap<>();
				returnResultMap.put("status", false);
				returnResultMap.put("message", "参数错误,flightType");
				return returnResultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnResultMap = new HashMap<>();
			returnResultMap.put("status", false);
			returnResultMap.put("message", "内部错误");
			logger.info("=======the transactionl get something wrong ==============");
			return returnResultMap;
		}
		return returnResultMap;
	}

	
}
