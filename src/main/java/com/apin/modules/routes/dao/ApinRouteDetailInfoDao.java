package com.apin.modules.routes.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.apin.base.dao.BaseDAO;
import com.apin.modules.routes.bean.ApinRouteDetailInfo;

public interface ApinRouteDetailInfoDao extends BaseDAO<ApinRouteDetailInfo>{

	List<ApinRouteDetailInfo> findBySearchMap(@Param("searchMap")Map<String, String> searchMap);
    
}