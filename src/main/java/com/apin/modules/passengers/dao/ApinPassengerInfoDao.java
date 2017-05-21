package com.apin.modules.passengers.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.apin.base.dao.BaseDAO;
import com.apin.modules.passengers.bean.ApinPassengerInfo;


/**
 * @author Young
 * @date 2017年1月20日 上午10:50:12
 * @version 1.0 
 */
public interface ApinPassengerInfoDao extends BaseDAO<ApinPassengerInfo>{

	List<Map<String, String>> getListMapByOrderNo(@Param("orderNo")Long orderNo);

}