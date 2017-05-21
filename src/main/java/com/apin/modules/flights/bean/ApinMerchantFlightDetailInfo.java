package com.apin.modules.flights.bean;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.apin.base.bean.Base;

/**
 * @author Young
 * @date 2017年1月17日 下午7:27:07
 * @version 1.0 
 */
public class ApinMerchantFlightDetailInfo extends Base {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5728226645565397489L;
	public static final String TABLE_NAME = "apin_merchant_flight_detail_info";
	@Column
	private Integer id;
	@Column
    private Integer parentId;
	@Column
    private Byte tripNumber;
	@Column
    private Long journeyId;
	@Column
    private String airComp;
	@Column
    private String flightNo;
	@Column
    private String departPlaceCode;
	@Column
    private String departPlace;
	@Column
    private String destPlaceCode;
	@Column
    private String destPlace;
	@Column
    private String departAirport;
	@Column
    private String arriveAirport;
	@Column
    private Date departDate;
	@Column
    private Date arriveDate;
	@Column
    private String departTime;
	@Column
    private String arriveTime;
    @Column
    private Byte hasTurn;
    @Column
    private String cabinName;
    @Column
    private Byte cabinType;
    @Column
    private String planeModel;
    @Column
    private String flyingTime;
    @Column
    private Byte flag;
    
    


	public Byte getFlag() {
		return flag;
	}

	public void setFlag(Byte flag) {
		this.flag = flag;
	}

	private List<ApinMerchantFlightTurnInfo> turnList;

    public Integer getId() {
        return id;
    }

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

    public Long getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(Long journeyId) {
        this.journeyId = journeyId;
    }

    public String getAirComp() {
        return airComp;
    }

    public void setAirComp(String airComp) {
        this.airComp = airComp == null ? null : airComp.trim();
    }

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo == null ? null : flightNo.trim();
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

    public String getDepartAirport() {
        return departAirport;
    }

    public void setDepartAirport(String departAirport) {
        this.departAirport = departAirport == null ? null : departAirport.trim();
    }

    public String getArriveAirport() {
        return arriveAirport;
    }

    public void setArriveAirport(String arriveAirport) {
        this.arriveAirport = arriveAirport == null ? null : arriveAirport.trim();
    }

    public Date getDepartDate() {
        return departDate;
    }

    public void setDepartDate(Date departDate) {
        this.departDate = departDate;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public String getDepartTime() {
        return departTime;
    }

    public void setDepartTime(String departTime) {
        this.departTime = departTime == null ? null : departTime.trim();
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime == null ? null : arriveTime.trim();
    }

    public Byte getHasTurn() {
        return hasTurn;
    }

    public void setHasTurn(Byte hasTurn) {
        this.hasTurn = hasTurn;
    }

    public String getCabinName() {
        return cabinName;
    }

    public void setCabinName(String cabinName) {
        this.cabinName = cabinName == null ? null : cabinName.trim();
    }

    public Byte getCabinType() {
        return cabinType;
    }

    public void setCabinType(Byte cabinType) {
        this.cabinType = cabinType;
    }

    public String getPlaneModel() {
        return planeModel;
    }

    public void setPlaneModel(String planeModel) {
        this.planeModel = planeModel == null ? null : planeModel.trim();
    }

    public String getFlyingTime() {
        return flyingTime;
    }

    public void setFlyingTime(String flyingTime) {
        this.flyingTime = flyingTime == null ? null : flyingTime.trim();
    }

	public List<ApinMerchantFlightTurnInfo> getTurnList() {
		return turnList;
	}

	public void setTurnList(List<ApinMerchantFlightTurnInfo> turnList) {
		this.turnList = turnList;
	}

    
    
}