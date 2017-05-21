package com.apin.modules.users.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.apin.base.dao.BaseDAO;
import com.apin.modules.users.bean.ApinAccount;

public interface ApinAccountDao extends BaseDAO<ApinAccount>{

	ApinAccount findByPhone(@Param("phone")String phone);
	ApinAccount findById(@Param("userId")String clientId);
	int insertMerchantMap(@Param("paramMap")Map<String, String> paramMap);
	Map<String, Object> getMerchantByPhone(String phone);


	
}