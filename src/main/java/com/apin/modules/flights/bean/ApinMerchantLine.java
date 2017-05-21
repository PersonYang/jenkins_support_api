package com.apin.modules.flights.bean;

import java.util.Date;

/**
 * 供应商航线情况
 * @author Young
 * @date 2017年1月17日 下午9:03:35
 * @version 1.0 
 */
public class ApinMerchantLine {
    
	private String id;
    private Byte isEnable;
    private String staffId;
    private Date createTime;
    private Integer apinMerchantId;
    private String fromCityName;
    private String fromCityCode;
    private String toCityName;
    private String toCityCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Byte getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Byte isEnable) {
        this.isEnable = isEnable;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId == null ? null : staffId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getApinMerchantId() {
        return apinMerchantId;
    }

    public void setApinMerchantId(Integer apinMerchantId) {
        this.apinMerchantId = apinMerchantId;
    }

    public String getFromCityName() {
        return fromCityName;
    }

    public void setFromCityName(String fromCityName) {
        this.fromCityName = fromCityName == null ? null : fromCityName.trim();
    }

    public String getFromCityCode() {
        return fromCityCode;
    }

    public void setFromCityCode(String fromCityCode) {
        this.fromCityCode = fromCityCode == null ? null : fromCityCode.trim();
    }

    public String getToCityName() {
        return toCityName;
    }

    public void setToCityName(String toCityName) {
        this.toCityName = toCityName == null ? null : toCityName.trim();
    }

    public String getToCityCode() {
        return toCityCode;
    }

    public void setToCityCode(String toCityCode) {
        this.toCityCode = toCityCode == null ? null : toCityCode.trim();
    }
}