package com.apin.modules.routes.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.apin.base.dao.BaseDAO;
import com.apin.modules.routes.bean.ApinRouteDistributeMerchantInfo;

public interface ApinRouteDistributeMerchantInfoDao extends BaseDAO<ApinRouteDistributeMerchantInfo> {

	List<ApinRouteDistributeMerchantInfo> getListByMap(@Param("searchMap")Map<String, String> searchMap);
	
	/**
	 * 查询当前商户的出价，和等待出价的情况
	 * @param searchMap
	 * @return List<Integer>  routeNoList 
	 */
	List<Integer> getRouteNoListByMap(@Param("searchMap")Map<String, String> searchMap);
	
	long getRouteNoCountByMap(@Param("searchMap")Map<String, String> searchMap);
	List<Long> getRouteDistributeList(Map<String, String> searchMap);
	int updateNotNull(ApinRouteDistributeMerchantInfo distributeInfo);
	/**
	 * 不匹配的 需要通知的商户ID
	 * @param searchMap
	 * @return
	 */
	List<Integer> getMerchantId(@Param("searchMap")Map<String, String> searchMap);
	int cancleRoute(@Param("routeNo")String routeNo);
	
	ApinRouteDistributeMerchantInfo getOneByRouteNo(@Param("routeNo")String routeNo);

	/**
	 * 获取供应商某个行程的分发
	 * @param searchMap
	 * @return
	 */
	ApinRouteDistributeMerchantInfo findOneBySearchMap(@Param("searchMap")Map<String, String> searchMap);

}