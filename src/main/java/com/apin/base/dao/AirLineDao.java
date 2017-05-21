package com.apin.base.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.apin.base.bean.AirLine;

public interface AirLineDao {

	int insertNotNull(AirLine airLine);
	AirLine findBySearchMap(@Param("searchMap")Map<String, String> searchMap);
	AirLine findById(@Param("id")Integer airLineId);
    
}