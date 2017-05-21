package com.apin.base.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.apin.common.ibatis.BaseSQLTemplate;

public interface BaseDAO<T> {

	/**
	 * 根据ID查询单条数据
	 * 
	 * @param id
	 * @return
	 */
	@SelectProvider(type = BaseSQLTemplate.class, method = "getByID")
	@ResultMap("result")
	 T getByID(@Param("base") Class<?> clz, @Param("id") Integer id);

	
	
	/**
	 * 根据PO中已设置的字段查询匹配的单条记录。
	 * 
	 * @return
	 */
	@SelectProvider(type = BaseSQLTemplate.class, method = "getByProperty")
	@ResultMap("result")
	 T getByProperty(@Param("base") Class<?> clz,
						   @Param("propertyName") String propertyName, @Param("value") Object value);

	
	
	
	/**
	 * 无分页查询，根据PO中已设置的字段查询同时全部匹配的记录
	 * @param value
	 * @return
	 */
	@SelectProvider(type = BaseSQLTemplate.class, method = "getListByProperty")
	@ResultMap("result")
	 List<T> getListByProperty(@Param("base") Class<?> clz,
									 @Param("propertyName") String propertyName, @Param("value") Object value);


	/**
	 * 无分页查询，根据PO中已设置的字段查询同时全部匹配的记录
	 * @param value
	 * @return
	 */
	@SelectProvider(type = BaseSQLTemplate.class, method = "getListByPropertyMap")
	@ResultMap("result")
	List<T> getListByPropertyMap(@Param("base") Class<?> clz,@Param("paramMap")Map<String,Object> paramMap);


	/**
	 * 查询全部
	 * 
	 * @return
	 */
	@SelectProvider(type = BaseSQLTemplate.class, method = "getListAll")
	@ResultMap("result")
	 List<T> getListAll(Class<?> clz);
	
	
	
	/**
	 * 统计记录数
	 * 
	 * @param t
	 * @return
	 */
	@SelectProvider(type = BaseSQLTemplate.class, method = "getCount")
	 long getCount(Class<?> clz);
	
	
	
	// ====以下部分的SQL不需要手动实现====================================================
	/**
	 * 增加记录
	 * 
	 * @param t
	 * @return
	 */
	@InsertProvider(type = BaseSQLTemplate.class, method = "insert" )
	@Options(useGeneratedKeys=true, keyProperty="id")
	 int insert(T t);

	/**
	 * 修改记录
	 * 
	 * @param t
	 * @return
	 */
	@UpdateProvider(type = BaseSQLTemplate.class, method = "update")
	@Options(useGeneratedKeys=true, keyProperty="id")
	 int update(T t);

	/**
	 * 删除记录
	 * 
	 * @return
	 */
	@DeleteProvider(type = BaseSQLTemplate.class, method = "delete")
	 int delete(T obj);

	// ===========未实现===================================================

	/**
	 * 批量删除
	 * 
	 * @param list
	 * @return
	 */
	 int batchDelete(List<Integer> list);

	/**
	 * 批量修改
	 * 
	 * @param map
	 * @return
	 */
	 int batchUpdate(Map<String, Object> map);




}
