package com.apin.common.ibatis;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.DELETE_FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.INSERT_INTO;
import static org.apache.ibatis.jdbc.SqlBuilder.SELECT;
import static org.apache.ibatis.jdbc.SqlBuilder.SET;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.UPDATE;
import static org.apache.ibatis.jdbc.SqlBuilder.VALUES;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.apin.base.bean.Base;
import com.apin.common.exception.PoStructureException;
import com.apin.common.utils.ConvertUtil;

public class BaseSQLTemplate<T extends Base> {

    /**
     * 通过ID查找单条记录
     *
     * @param para
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getByID(Map<String, Object> para) {

        BEGIN();
        SELECT("*");
        FROM(getTableName((Class) para.get("base")));
        WHERE("id= #{id} ");
        return SQL();
    }

    /**
     * 根据PO中已设置的字段查询匹配的单条记录
     *
     * @param para
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getByProperty(Map<String, Object> para) {
        Class clz = (Class) para.get("base");
        String propertyName = (String) para.get("propertyName");
        Object value = para.get("value");
        Field field;
        try {
            field = clz.getDeclaredField(propertyName);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new PoStructureException("无法在PO中找到'" + propertyName + "'属性", e);
        }
        String columnName = ConvertUtil.getColumnByPropertyName(propertyName);

        return "select * from " + getTableName(clz)
                + " where " + columnName + "='" + value + "' limit 1";
    }

    /**
     * 根据PO中已设置的字段查询匹配的单条记录
     *
     * @param para
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getListByProperty(Map<String, Object> para) {
        Class clz = (Class) para.get("base");
        String propertyName = (String) para.get("propertyName");
        Object value = para.get("value");
        Field field;
        try {
            field = clz.getDeclaredField(propertyName);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new PoStructureException("无法在PO中找到'" + propertyName + "'属性", e);
        }
        String columnName = ConvertUtil.getColumnByPropertyName(propertyName);

        return "select * from " + getTableName(clz)
                + " where " + columnName + "='" + value + "'";
    }

    public String getListByPropertyMap(Map<String,Object> para){
        Class clz = (Class) para.get("base");
        Map<String,Object> params =(Map<String,Object>) para.get("paramMap");

        String sql = "SELECT * FROM "+ getTableName(clz)+ " WHERE " ;
        Set properties = params.keySet();
        String columnName ;
        Object value ;
        Iterator iterator = properties.iterator();
       while (iterator.hasNext()){
           String propertyName = (String)iterator.next();
           columnName = ConvertUtil.getColumnByPropertyName(propertyName);
           value = params.get(propertyName);
           sql += columnName +" = " +value;
       }
        return sql;
    }

    /**
     * 查询所有记录
     *
     * @param clz
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getListAll(Class clz) {
        BEGIN();
        SELECT("*");
        FROM(getTableName(clz));
        return SQL();
    }


    /**
     * 统计记录数
     *
     * @param clz
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getCount(Class clz) {
        return "select count(*) from " + getTableName(clz);
    }

    // ==============增 删 改========================================
    public String insert(T obj) {
        BEGIN();

        INSERT_INTO(getTableName(obj.getClass()));
        obj.caculationColumnList();
        VALUES(obj.returnInsertColumnsName(), obj.returnInsertColumnsDefine());
        return SQL();
    }

    public String update(T obj) {
        String idname = "id";

        BEGIN();

        UPDATE(getTableName(obj.getClass()));
        obj.caculationColumnList();
        SET(obj.returnUpdateSet());
        WHERE(idname + "=#{" + idname + "}");

        return SQL();
    }

    public String delete(T obj) {
        String idname = "id";

        BEGIN();

        DELETE_FROM(getTableName(obj.getClass()));
        WHERE(idname + "=#{" + idname + "}");

        return SQL();
    }

    @SuppressWarnings("rawtypes")
    private static String getTableName(Class clz) {
        try {
            Field field = clz.getField("TABLE_NAME");
            if (field != null) {
                if (StringUtils.isNotBlank(field.get(null).toString())) {
                    return field.get(null).toString();
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new PoStructureException("PO未定义 @Id");
    }



}