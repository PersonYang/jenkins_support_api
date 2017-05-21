package com.apin.modules.message.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.apin.common.utils.PageCommon;
import com.apin.modules.message.bean.ApinMessage;

/**
 * 消息相关
 * @author Young
 * @date 2017年2月22日 下午1:46:43
 * @version 1.0 
 */
public interface ApinMessageDao {

	long getCountBySearchMap(@Param("searchMap")Map<String, String> searchMap);
	List<ApinMessage> getListBySearchMap(@Param("page")PageCommon page,@Param("searchMap") Map<String, String> searchMap);
	int updateNotNull(@Param("paramMap")Map<String, String> paramsMap, @Param("messIdList")List<String> messIdList);
	int insertNotNull(ApinMessage message);
	List<ApinMessage> getListByUserId(@Param("userId")String userId);
	
}