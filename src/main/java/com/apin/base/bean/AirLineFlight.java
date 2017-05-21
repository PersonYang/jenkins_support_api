package com.apin.base.bean;

/**
 * 基础航班信息
 * @author Young
 * @date 2017年1月11日 下午4:42:15
 * @version 1.0 
 */
public class AirLineFlight {
    private Integer id;

    private Integer airLineId;

    private String airComp;

    private String airCompCode;

    private String flightNo;

    private String departAirportCode;

    private String departAirport;
    private String departPlace;
    private String departPlaceCode;
    private String destPlace;
    private String destPlaceCode;
    

	private String arriveAirportCode;

    private String arriveAirport;

    private String depTerminal;

    private String arrTerminal;

    private String departTime;

    private String arriveTime;

    private String onTimeRate;

    private Byte isMiddleStop;

    private String middleCity;

    private String middleCityCode;

    private String middleAirport;

    private String middleAirportCode;

    private String middleArriveTime;

    private String middleDepartTime;

    private String boardingGate;

    private String dutyCounter;

    private String luggageGate;
    private String flyTime;
    
    

    public String getFlyTime() {
		return flyTime;
	}

	public void setFlyTime(String flyTime) {
		this.flyTime = flyTime;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getDepartPlace() {
		return departPlace;
	}

	public void setDepartPlace(String departPlace) {
		this.departPlace = departPlace;
	}

	public String getDepartPlaceCode() {
		return departPlaceCode;
	}

	public void setDepartPlaceCode(String departPlaceCode) {
		this.departPlaceCode = departPlaceCode;
	}

	public String getDestPlace() {
		return destPlace;
	}

	public void setDestPlace(String destPlace) {
		this.destPlace = destPlace;
	}

	public String getDestPlaceCode() {
		return destPlaceCode;
	}

	public void setDestPlaceCode(String destPlaceCode) {
		this.destPlaceCode = destPlaceCode;
	}

    public Integer getAirLineId() {
        return airLineId;
    }

    public void setAirLineId(Integer airLineId) {
        this.airLineId = airLineId;
    }

    public String getAirComp() {
        return airComp;
    }

    public void setAirComp(String airComp) {
        this.airComp = airComp == null ? null : airComp.trim();
    }

    public String getAirCompCode() {
        return airCompCode;
    }

    public void setAirCompCode(String airCompCode) {
        this.airCompCode = airCompCode == null ? null : airCompCode.trim();
    }

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo == null ? null : flightNo.trim();
    }

    public String getDepartAirportCode() {
        return departAirportCode;
    }

    public void setDepartAirportCode(String departAirportCode) {
        this.departAirportCode = departAirportCode == null ? null : departAirportCode.trim();
    }

    public String getDepartAirport() {
        return departAirport;
    }

    public void setDepartAirport(String departAirport) {
        this.departAirport = departAirport == null ? null : departAirport.trim();
    }

    public String getArriveAirportCode() {
        return arriveAirportCode;
    }

    public void setArriveAirportCode(String arriveAirportCode) {
        this.arriveAirportCode = arriveAirportCode == null ? null : arriveAirportCode.trim();
    }

    public String getArriveAirport() {
        return arriveAirport;
    }

    public void setArriveAirport(String arriveAirport) {
        this.arriveAirport = arriveAirport == null ? null : arriveAirport.trim();
    }

    public String getDepTerminal() {
        return depTerminal;
    }

    public void setDepTerminal(String depTerminal) {
        this.depTerminal = depTerminal == null ? null : depTerminal.trim();
    }

    public String getArrTerminal() {
        return arrTerminal;
    }

    public void setArrTerminal(String arrTerminal) {
        this.arrTerminal = arrTerminal == null ? null : arrTerminal.trim();
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

    public String getOnTimeRate() {
        return onTimeRate;
    }

    public void setOnTimeRate(String onTimeRate) {
        this.onTimeRate = onTimeRate == null ? null : onTimeRate.trim();
    }

    public Byte getIsMiddleStop() {
        return isMiddleStop;
    }

    public void setIsMiddleStop(Byte isMiddleStop) {
        this.isMiddleStop = isMiddleStop;
    }

    public String getMiddleCity() {
        return middleCity;
    }

    public void setMiddleCity(String middleCity) {
        this.middleCity = middleCity == null ? null : middleCity.trim();
    }

    public String getMiddleCityCode() {
        return middleCityCode;
    }

    public void setMiddleCityCode(String middleCityCode) {
        this.middleCityCode = middleCityCode == null ? null : middleCityCode.trim();
    }

    public String getMiddleAirport() {
        return middleAirport;
    }

    public void setMiddleAirport(String middleAirport) {
        this.middleAirport = middleAirport == null ? null : middleAirport.trim();
    }

    public String getMiddleAirportCode() {
        return middleAirportCode;
    }

    public void setMiddleAirportCode(String middleAirportCode) {
        this.middleAirportCode = middleAirportCode == null ? null : middleAirportCode.trim();
    }

    public String getMiddleArriveTime() {
        return middleArriveTime;
    }

    public void setMiddleArriveTime(String middleArriveTime) {
        this.middleArriveTime = middleArriveTime == null ? null : middleArriveTime.trim();
    }

    public String getMiddleDepartTime() {
        return middleDepartTime;
    }

    public void setMiddleDepartTime(String middleDepartTime) {
        this.middleDepartTime = middleDepartTime == null ? null : middleDepartTime.trim();
    }

    public String getBoardingGate() {
        return boardingGate;
    }

    public void setBoardingGate(String boardingGate) {
        this.boardingGate = boardingGate == null ? null : boardingGate.trim();
    }

    public String getDutyCounter() {
        return dutyCounter;
    }

    public void setDutyCounter(String dutyCounter) {
        this.dutyCounter = dutyCounter == null ? null : dutyCounter.trim();
    }

    public String getLuggageGate() {
        return luggageGate;
    }

    public void setLuggageGate(String luggageGate) {
        this.luggageGate = luggageGate == null ? null : luggageGate.trim();
    }
}