package com.apin.modules.routes.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.apin.base.dao.BaseDAO;
import com.apin.common.utils.PageCommon;
import com.apin.modules.routes.bean.ApinRoute;

/**
 * 商户行程订单相关
 * @author Young
 * @date 2017年1月18日 下午4:01:25
 * @version 1.0 
 */
public interface ApinRouteDao extends BaseDAO<ApinRoute>{

	long getAllCountBySearchMap(@Param("searchMap")Map<String, String> searchMap);
	List<ApinRoute> getAllListBySearchMap(@Param("page")PageCommon page,@Param("searchMap")Map<String, String> searchMap);
	
	
	/**
	 * 出价 未出价 用到
	 * @param searchMap
	 * @param routeNoList
	 * @return
	 */
	long getCountBySearchMap(@Param("searchMap")Map<String, String> searchMap,@Param("routeNoList") List<Integer> routeNoList);
	/**
	 *  出价 未出价 用到
	 * @param page
	 * @param searchMap
	 * @param routeNoList
	 * @return
	 */
	List<ApinRoute> getListBySearchMap(@Param("page")PageCommon page, @Param("searchMap")Map<String, String> searchMap,
			@Param("routeNoList") List<Integer> routeNoList);
	int updateNotNull(ApinRoute updateRoute);
		
}
