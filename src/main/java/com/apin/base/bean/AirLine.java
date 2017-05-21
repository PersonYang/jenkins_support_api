package com.apin.base.bean;

/**
 * 基础航线信息
 * @author Young
 * @date 2017年1月11日 下午4:42:00
 * @version 1.0 
 */
public class AirLine {
    private Integer id;

    private String departPlaceCode;

    private String departPlace;

    private String destPlaceCode;

    private String destPlace;

    private Byte lineType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDepartPlaceCode() {
        return departPlaceCode;
    }

    public void setDepartPlaceCode(String departPlaceCode) {
        this.departPlaceCode = departPlaceCode == null ? null : departPlaceCode.trim();
    }

    public String getDepartPlace() {
        return departPlace;
    }

    public void setDepartPlace(String departPlace) {
        this.departPlace = departPlace == null ? null : departPlace.trim();
    }

    public String getDestPlaceCode() {
        return destPlaceCode;
    }

    public void setDestPlaceCode(String destPlaceCode) {
        this.destPlaceCode = destPlaceCode == null ? null : destPlaceCode.trim();
    }

    public String getDestPlace() {
        return destPlace;
    }

    public void setDestPlace(String destPlace) {
        this.destPlace = destPlace == null ? null : destPlace.trim();
    }

    public Byte getLineType() {
        return lineType;
    }

    public void setLineType(Byte lineType) {
        this.lineType = lineType;
    }

}