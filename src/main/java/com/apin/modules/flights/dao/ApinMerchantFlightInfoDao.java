package com.apin.modules.flights.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.apin.base.dao.BaseDAO;
import com.apin.common.utils.PageCommon;
import com.apin.modules.flights.bean.ApinMerchantFlightInfo;

public interface ApinMerchantFlightInfoDao extends BaseDAO<ApinMerchantFlightInfo>{

	/**
	 * 商户列表中使用
	 * @param searchMap
	 * @return
	 */
	long getCountBySearchMap(@Param("searchMap")Map<String, String> searchMap);
	List<Map<String, Object>> getMapListBySearchMap(@Param("page")PageCommon page,
			@Param("searchMap")Map<String, String> searchMap);
	int updateNotNull(ApinMerchantFlightInfo flightInfo);
	int updateByMap(@Param("updateMap")Map<String, Object> updateMap);
	ApinMerchantFlightInfo getOneByGroupId(@Param("groupId")String groupId);
	
    
}