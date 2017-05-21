package com.apin.modules.routes.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.apin.base.dao.BaseDAO;
import com.apin.modules.routes.bean.ApinMerchantRoutePriceInfo;

/**
 * 
 * 商户出价表
 * @author Young
 * @date 2016年9月29日
 */
public interface ApinMerchantRoutePriceInfoDao extends BaseDAO<ApinMerchantRoutePriceInfo>{

	List<Integer> getRouteNoList(@Param("searchMap")Map<String, String> searchMap);

	List<Long> getJoumryIdList(@Param("searchMap")Map<String, String> searchMap);

	int insertNotNull(ApinMerchantRoutePriceInfo routePrice);
	List<ApinMerchantRoutePriceInfo> getListMap(@Param("searchMap")Map<String, Object> searchMap);
	
	List<Integer> getIdList(@Param("searchMap")Map<String, String> searchMap);
	
	Long getOneJoumryId(@Param("searchMap")Map<String, String> searchMap);
	
	ApinMerchantRoutePriceInfo getOneRoutePrice(@Param("searchMap")Map<String, String> searchMap);

	/**
	 * 获取一个行程当前供应商所有出价的航班信息
	 * @param routeNo
	 * @param userId
	 * @return
	 */
	List<Map<String, Object>> getOfferedFlightInfo(@Param("routeNo")String routeNo,@Param("userId")String userId);
	
}