package com.apin.base.bean;


public class DictAirport extends Dict {

    public static final String TABLE_NAME = "apin_airport";
    private static final long serialVersionUID = 9154687996705290744L;

    private Integer id;

    private String iataCode;

    private String icaoCode;

    private String airportName;

    private String cityName;

    private String cityPinyin;

    private String cityPinyinheader;

    private String cityPinyinfirst;

    private String zoneCode;

    private Byte abroad;

    private Double longitude;

    private Double latitude;

    private String cityCode;

    private Integer status;

    private String airportPinyin;

    private String hotCityTag;
    private String timeZone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName == null ? null : airportName.trim();
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public String getCityPinyin() {
        return cityPinyin;
    }

    public void setCityPinyin(String cityPinyin) {
        this.cityPinyin = cityPinyin == null ? null : cityPinyin.trim();
    }

    public String getCityPinyinheader() {
        return cityPinyinheader;
    }

    public void setCityPinyinheader(String cityPinyinheader) {
        this.cityPinyinheader = cityPinyinheader == null ? null : cityPinyinheader.trim();
    }

    public String getCityPinyinfirst() {
        return cityPinyinfirst;
    }

    public void setCityPinyinfirst(String cityPinyinfirst) {
        this.cityPinyinfirst = cityPinyinfirst == null ? null : cityPinyinfirst.trim();
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode == null ? null : zoneCode.trim();
    }

    public Byte getAbroad() {
        return abroad;
    }

    public void setAbroad(Byte abroad) {
        this.abroad = abroad;
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

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode == null ? null : cityCode.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAirportPinyin() {
        return airportPinyin;
    }

    public void setAirportPinyin(String airportPinyin) {
        this.airportPinyin = airportPinyin == null ? null : airportPinyin.trim();
    }

    public String getHotCityTag() {
        return hotCityTag;
    }

    public void setHotCityTag(String hotCityTag) {
        this.hotCityTag = hotCityTag == null ? null : hotCityTag.trim();
    }

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
    
    
}