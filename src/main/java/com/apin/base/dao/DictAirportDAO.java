package com.apin.base.dao;

import java.util.List;

import com.apin.base.bean.DictAirport;

/**
 * 基础 机场信息dao
 * @author Young
 * @date 2017年1月11日 下午4:43:33
 * @version 1.0 
 */
public interface DictAirportDAO extends BaseDAO<DictAirport>{

	List<DictAirport> findList();
	
}
