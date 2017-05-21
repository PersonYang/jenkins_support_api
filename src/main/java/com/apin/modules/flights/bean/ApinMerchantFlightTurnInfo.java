package com.apin.modules.flights.bean;

import java.util.Date;

import javax.persistence.Column;

import com.apin.base.bean.Base;

/**
 * @author Young
 * @date 2017年1月17日 下午7:26:43
 * @version 1.0 
 */
public class ApinMerchantFlightTurnInfo extends Base{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8875282617287092318L;
	public static final String TABLE_NAME = "apin_merchant_flight_turn_info";
	@Column
	private Integer id;
	@Column
    private Integer flightId;
	@Column
    private String turnCityCode;
	@Column
    private String turnCity;
	@Column
    private String arriveAirport;
	@Column
    private Date arriveDate;
	@Column
    private String arriveTime;
	@Column
    private String arriveAirComp;
	@Column
    private String arriveFlightNo;
	@Column
    private String arriveCabinName;
	@Column
    private Byte arriveCabinType;
	@Column
    private String arrivePlaneModel;
	@Column
    private String arriveFlyingTime;
    @Column
    private String departAirport;
    @Column
    private Date departDate;
    @Column
    private String departTime;
    @Column
    private String departAirComp;
    @Column
    private String departFlightNo;
    @Column
    private String departCabinName;
    @Column
    private Byte departCabinType;
    @Column
    private String departPlaneModel;
    @Column
    private String departFlyingTime;
    @Column
    private String stayTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFlightId() {
        return flightId;
    }

    public void setFlightId(Integer flightId) {
        this.flightId = flightId;
    }

    public String getTurnCityCode() {
        return turnCityCode;
    }

    public void setTurnCityCode(String turnCityCode) {
        this.turnCityCode = turnCityCode == null ? null : turnCityCode.trim();
    }

    public String getTurnCity() {
        return turnCity;
    }

    public void setTurnCity(String turnCity) {
        this.turnCity = turnCity == null ? null : turnCity.trim();
    }

    public String getArriveAirport() {
        return arriveAirport;
    }

    public void setArriveAirport(String arriveAirport) {
        this.arriveAirport = arriveAirport == null ? null : arriveAirport.trim();
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime == null ? null : arriveTime.trim();
    }

    public String getArriveAirComp() {
        return arriveAirComp;
    }

    public void setArriveAirComp(String arriveAirComp) {
        this.arriveAirComp = arriveAirComp == null ? null : arriveAirComp.trim();
    }

    public String getArriveFlightNo() {
        return arriveFlightNo;
    }

    public void setArriveFlightNo(String arriveFlightNo) {
        this.arriveFlightNo = arriveFlightNo == null ? null : arriveFlightNo.trim();
    }

    public String getArriveCabinName() {
        return arriveCabinName;
    }

    public void setArriveCabinName(String arriveCabinName) {
        this.arriveCabinName = arriveCabinName == null ? null : arriveCabinName.trim();
    }

    public Byte getArriveCabinType() {
        return arriveCabinType;
    }

    public void setArriveCabinType(Byte arriveCabinType) {
        this.arriveCabinType = arriveCabinType;
    }

    public String getArrivePlaneModel() {
        return arrivePlaneModel;
    }

    public void setArrivePlaneModel(String arrivePlaneModel) {
        this.arrivePlaneModel = arrivePlaneModel == null ? null : arrivePlaneModel.trim();
    }

    public String getArriveFlyingTime() {
        return arriveFlyingTime;
    }

    public void setArriveFlyingTime(String arriveFlyingTime) {
        this.arriveFlyingTime = arriveFlyingTime == null ? null : arriveFlyingTime.trim();
    }

    public String getDepartAirport() {
        return departAirport;
    }

    public void setDepartAirport(String departAirport) {
        this.departAirport = departAirport == null ? null : departAirport.trim();
    }

    public Date getDepartDate() {
        return departDate;
    }

    public void setDepartDate(Date departDate) {
        this.departDate = departDate;
    }

    public String getDepartTime() {
        return departTime;
    }

    public void setDepartTime(String departTime) {
        this.departTime = departTime == null ? null : departTime.trim();
    }

    public String getDepartAirComp() {
        return departAirComp;
    }

    public void setDepartAirComp(String departAirComp) {
        this.departAirComp = departAirComp == null ? null : departAirComp.trim();
    }

    public String getDepartFlightNo() {
        return departFlightNo;
    }

    public void setDepartFlightNo(String departFlightNo) {
        this.departFlightNo = departFlightNo == null ? null : departFlightNo.trim();
    }

    public String getDepartCabinName() {
        return departCabinName;
    }

    public void setDepartCabinName(String departCabinName) {
        this.departCabinName = departCabinName == null ? null : departCabinName.trim();
    }

    public Byte getDepartCabinType() {
        return departCabinType;
    }

    public void setDepartCabinType(Byte departCabinType) {
        this.departCabinType = departCabinType;
    }

    public String getDepartPlaneModel() {
        return departPlaneModel;
    }

    public void setDepartPlaneModel(String departPlaneModel) {
        this.departPlaneModel = departPlaneModel == null ? null : departPlaneModel.trim();
    }

    public String getDepartFlyingTime() {
        return departFlyingTime;
    }

    public void setDepartFlyingTime(String departFlyingTime) {
        this.departFlyingTime = departFlyingTime == null ? null : departFlyingTime.trim();
    }

    public String getStayTime() {
        return stayTime;
    }

    public void setStayTime(String stayTime) {
        this.stayTime = stayTime == null ? null : stayTime.trim();
    }
}