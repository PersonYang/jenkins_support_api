package com.apin.base.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.apin.base.bean.AirLineFlight;

public interface AirLineFlightDao {

	AirLineFlight findByFlightNo(@Param("flightNo")String flightNo);
	AirLineFlight findBySearchMap(@Param("searchMap")Map<String, Object> searchMap);

	int insertNotNull(AirLineFlight lineFlight);
    
}