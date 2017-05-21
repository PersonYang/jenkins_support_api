package com.apin.base.bean;

import javax.persistence.Column;

/**
 * 
 * 航空公司
 * @author Young
 * @date 2016年10月13日
 */
public class ApinAirComp extends Base{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7047590903243282055L;
	public static final String TABLE_NAME = "apin_air_comp";
	@Column
	private Integer id;
	@Column
    private String name;
	@Column
    private String iataCode;
	@Column
    private String icaoCode;
	@Column
    private String nationCode;
	@Column
    private String logoIco;
	@Column
    private String pinyinFirst;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode == null ? null : iataCode.trim();
    }

    public String getIcaoCode() {
        return icaoCode;
    }

    public void setIcaoCode(String icaoCode) {
        this.icaoCode = icaoCode == null ? null : icaoCode.trim();
    }

    public String getNationCode() {
        return nationCode;
    }

    public void setNationCode(String nationCode) {
        this.nationCode = nationCode == null ? null : nationCode.trim();
    }

    public String getLogoIco() {
        return logoIco;
    }

    public void setLogoIco(String logoIco) {
        this.logoIco = logoIco == null ? null : logoIco.trim();
    }

    public String getPinyinFirst() {
        return pinyinFirst;
    }

    public void setPinyinFirst(String pinyinFirst) {
        this.pinyinFirst = pinyinFirst == null ? null : pinyinFirst.trim();
    }
}