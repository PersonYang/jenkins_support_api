package com.apin.base.bean;

import javax.persistence.Column;

public class ApinCity extends Dict{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9127957964695292446L;
	public static final String TABLE_NAME = "apin_city";
    @Column
	private Integer id;
    @Column
    private String cityName;
    @Column
    private String enName;
    @Column
    private String cityCode;
    @Column
    private String fullPinyin;
    @Column
    private String pinyinHeaders;
    @Column
    private String pinyinFirst;
    @Column
    private String imgUrl;
    @Column
    private String description;
    @Column
    private String zoneCode;
    @Column
    private Integer abroad;
    @Column
    private Integer busiType;
    @Column
    private Integer pinyouHotTag;
    @Column
    private Integer pinjiHotTag;
    @Column
    private Integer cityVersion;
    @Column
    private Integer hotVersion;
    @Column
    private Double longitude;
    @Column
    private Double latitude;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName == null ? null : enName.trim();
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode == null ? null : cityCode.trim();
    }

    public String getFullPinyin() {
        return fullPinyin;
    }

    public void setFullPinyin(String fullPinyin) {
        this.fullPinyin = fullPinyin == null ? null : fullPinyin.trim();
    }

    public String getPinyinHeaders() {
        return pinyinHeaders;
    }

    public void setPinyinHeaders(String pinyinHeaders) {
        this.pinyinHeaders = pinyinHeaders == null ? null : pinyinHeaders.trim();
    }

    public String getPinyinFirst() {
        return pinyinFirst;
    }

    public void setPinyinFirst(String pinyinFirst) {
        this.pinyinFirst = pinyinFirst == null ? null : pinyinFirst.trim();
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl == null ? null : imgUrl.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode == null ? null : zoneCode.trim();
    }

    public Integer getAbroad() {
        return abroad;
    }

    public void setAbroad(Integer abroad) {
        this.abroad = abroad;
    }

    public Integer getBusiType() {
        return busiType;
    }

    public void setBusiType(Integer busiType) {
        this.busiType = busiType;
    }

    public Integer getPinyouHotTag() {
        return pinyouHotTag;
    }

    public void setPinyouHotTag(Integer pinyouHotTag) {
        this.pinyouHotTag = pinyouHotTag;
    }

    public Integer getPinjiHotTag() {
        return pinjiHotTag;
    }

    public void setPinjiHotTag(Integer pinjiHotTag) {
        this.pinjiHotTag = pinjiHotTag;
    }

    public Integer getCityVersion() {
        return cityVersion;
    }

    public void setCityVersion(Integer cityVersion) {
        this.cityVersion = cityVersion;
    }

    public Integer getHotVersion() {
        return hotVersion;
    }

    public void setHotVersion(Integer hotVersion) {
        this.hotVersion = hotVersion;
    }

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
    
    
}