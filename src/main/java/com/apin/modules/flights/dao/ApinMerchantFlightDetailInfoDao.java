package com.apin.modules.flights.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.apin.base.dao.BaseDAO;
import com.apin.modules.flights.bean.ApinMerchantFlightDetailInfo;

public interface ApinMerchantFlightDetailInfoDao extends BaseDAO<ApinMerchantFlightDetailInfo>{

	/**
	 * 根据条件获取 一个journeyId的父航班
	 * @param searchMap
	 * @return
	 */
	ApinMerchantFlightDetailInfo getOneFlightBySearchMap(@Param("searchMap")Map<String, String> searchMap);
	List<ApinMerchantFlightDetailInfo> getFlightBySearchMap(@Param("searchMap")Map<String, String> searchMap);
	/**
	 * 新版本查询航班详情  1，供应商出价时获取航班信息 用到.2
	 * @param joumryIdList
	 * @param searchMap
	 * @return
	 */
	List<ApinMerchantFlightDetailInfo> newGetOfferedFlight(@Param("joumryIdList")List<String> joumryIdList, @Param("searchMap")Map<String, String> searchMap);

	Map<String, Object> getHeadDetailInfo(String groupId);
	Map<String, Object> getBackDepartDate(Object groupId);
	List<Map<String, Object>> getUnionDepartDate(Object journeyId);
}