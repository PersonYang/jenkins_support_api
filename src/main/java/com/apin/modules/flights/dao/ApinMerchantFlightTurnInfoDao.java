package com.apin.modules.flights.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.apin.base.dao.BaseDAO;
import com.apin.modules.flights.bean.ApinMerchantFlightTurnInfo;

public interface ApinMerchantFlightTurnInfoDao extends BaseDAO<ApinMerchantFlightTurnInfo>{

	int deleteFlightById(@Param("id")Integer id);

	/**
	 * 通过航班ID获取行程的转机信息
	 * @param searchMap
	 * @return
	 */
	List<ApinMerchantFlightTurnInfo> findListBySearchMap(@Param("searchMap")Map<String, String> searchMap);
	
	
}