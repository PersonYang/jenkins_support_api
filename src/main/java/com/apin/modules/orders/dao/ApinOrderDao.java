package com.apin.modules.orders.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.apin.base.dao.BaseDAO;
import com.apin.common.utils.PageCommon;
import com.apin.modules.orders.bean.ApinOrder;

/**
 * 客户订单相关
 * @author Young
 * @date 2016年10月8日
 */
public interface ApinOrderDao extends BaseDAO<ApinOrder>{

	/**
	 * 当前相关订单的数目
	 * @param searchMap
	 * @return
	 */
	long getCountBySearchMap(@Param("searchMap")Map<String, String> searchMap);
	/**
	 * 当前用户的订单
	 * @param page
	 * @param searchMap
	 * @return
	 */
	List<ApinOrder> getListBySearchMap(@Param("page")PageCommon page, @Param("searchMap")Map<String, String> searchMap);
	
	ApinOrder getOrderByPriceList(@Param("priceIdList")List<Integer> priceIdList);
	int updateNotNull(ApinOrder updateOrder);
	
   
}