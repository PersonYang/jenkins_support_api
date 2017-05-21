package com.apin.modules.routes.bean;

import java.util.Date;

import javax.persistence.Column;

import com.apin.base.bean.Base;

public class ApinRouteDetailInfo extends Base{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5638834804634913552L;
	public static final String TABLE_NAME = "apin_route_detail_info";
	@Column
	private Integer id;
	@Column
    private Integer parentId;
	@Column
    private Byte tripNumber;
	@Column
    private Long routeNo;
	@Column
    private String departPlaceCode;
	@Column
    private String departPlace;
	@Column
    private String destPlaceCode;
	@Column
    private String destPlace;
	@Column
    private Date departDate;

    @Override
	public Integer getId() {
        return id;
    }

    @Override
	public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Byte getTripNumber() {
        return tripNumber;
    }

    public void setTripNumber(Byte tripNumber) {
        this.tripNumber = tripNumber;
    }

    public Long getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(Long routeNo) {
        this.routeNo = routeNo;
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

    public Date getDepartDate() {
        return departDate;
    }

    public void setDepartDate(Date departDate) {
        this.departDate = departDate;
    }
}